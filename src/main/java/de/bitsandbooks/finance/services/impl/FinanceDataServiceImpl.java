package de.bitsandbooks.finance.services.impl;

import static java.util.Optional.ofNullable;
import static reactor.core.publisher.Mono.just;

import de.bitsandbooks.finance.connectors.ConnectorFacade;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.dtos.*;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.repositories.FinancePositionRepository;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.services.FinanceDataService;
import java.math.BigDecimal;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinanceDataServiceImpl implements FinanceDataService {

  @NonNull private final ConnectorFacade connectorFacade;

  @NonNull private final FinancePositionRepository financePositionRepository;

  @NonNull private final UserAccountRepository userAccountRepository;

  @Transactional(readOnly = true)
  @Override
  public Mono<FinanceTotalDto> getTotalAmount(
      String externalIdentifier, String currency, Boolean byType) {
    return byType
        ? getFinanceTotalDtoInternalByType(externalIdentifier, currency)
        : getFinanceTotalDtoInternal(externalIdentifier, currency);
  }

  private Mono<FinanceTotalDto> getFinanceTotalDtoInternal(
      String externalIdentifier, String currency) {
    return this.getAllPositions(externalIdentifier)
        .flatMap(el -> positionToCurrencyValue(el, currency))
        .map(el -> el.getValueDto().getPrice())
        .reduceWith(() -> BigDecimal.ZERO, BigDecimal::add)
        .map(el -> new FinanceTotalDto(currency, el, null));
  }

  private Mono<FinanceTotalDto> getFinanceTotalDtoInternalByType(
      String externalIdentifier, String currency) {
    return this.getAllPositions(externalIdentifier)
        .flatMap(el -> positionToCurrencyValue(el, currency))
        .collectList()
        .map(this::buildPositionTypeMap)
        .map(el -> this.buildFinanceTotalDto(el, currency));
  }

  private Mono<ValueWithTypeDto> positionToCurrencyValue(
      FinancePositionEntity position, String currency) {
    boolean positionValueCurrencyEqualsCurrency = position.getCurrency().equals(currency);
    if (positionValueCurrencyEqualsCurrency) {
      return just(
          new ValueWithTypeDto(
              new ValueDto(position.getCurrency(), position.getPrice()), position.getType()));
    } else {
      return connectorFacade
          .convertToCurrency(position.getCurrency(), currency)
          .map(exchangeRateDto -> exchangeRateDto.getExchangeRate().multiply((position.getPrice())))
          .map(
              exchangedPrice ->
                  new ValueWithTypeDto(new ValueDto(currency, exchangedPrice), position.getType()));
    }
  }

  private Map<PositionType, BigDecimal> buildPositionTypeMap(
      Collection<ValueWithTypeDto> valueWithTypeDtoCollection) {
    Map<PositionType, BigDecimal> result = new HashMap<>();
    valueWithTypeDtoCollection.forEach(
        el -> {
          if (result.containsKey(el.getPositionType())) {
            BigDecimal mapEntry = result.get(el.getPositionType());
            BigDecimal value = el.getValueDto().getPrice();
            BigDecimal newMapEntry = mapEntry.add(value);
            result.put(el.getPositionType(), newMapEntry);
          } else {
            BigDecimal value = el.getValueDto().getPrice();
            result.put(el.getPositionType(), value == null ? BigDecimal.ZERO : value);
          }
        });
    return result;
  }

  private FinanceTotalDto buildFinanceTotalDto(
      Map<PositionType, BigDecimal> positionTypeValueDtoMap, String currency) {
    BigDecimal total =
        positionTypeValueDtoMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    return new FinanceTotalDto(currency, total, positionTypeValueDtoMap);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<FinancePositionEntity> getAllPositions(String externalIdentifier) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    List<FinancePositionEntity> financePositionEntityList =
        financePositionRepository.findByUserAccount(userAccountEntity);
    return Flux.fromIterable(financePositionEntityList)
        .flatMap(
            financePositionEntity ->
                positionToValue(financePositionEntity)
                    .flatMap(
                        valueDto -> {
                          financePositionEntity.setPrice(valueDto.getPrice());
                          financePositionEntity.setCurrency(valueDto.getCurrency());
                          if (!valueDto.getCurrency().equals(financePositionEntity.getCurrency())) {
                            throw new IllegalStateException(
                                "Entity currency and connector result currency differ");
                          }
                          return just(financePositionEntity);
                        }));
  }

  private Mono<ValueDto> positionToValue(FinancePositionEntity position) {
    boolean resolvePosition = position.getType().isResolve();
    if (resolvePosition) {
      return connectorFacade
          .getActualValue(position.getIdentifier())
          .map(
              valueDto -> {
                String currency = ofNullable(valueDto.getCurrency()).orElse(position.getCurrency());
                BigDecimal price = valueDto.getPrice().multiply(position.getAmount());
                return new ValueDto(currency, price);
              });
    } else {
      return just(new ValueDto(position.getCurrency(), position.getAmount()));
    }
  }

  @Transactional
  @Override
  public List<FinancePositionEntity> addPositions(
      List<FinancePositionEntity> toAddFinancePositionEntityList, String externalIdentifier) {
    checkPositionsExist(toAddFinancePositionEntityList);
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    toAddFinancePositionEntityList.forEach(
        financePositionEntity -> {
          financePositionEntity.setUserAccount(userAccountEntity);
          financePositionEntity.setExternalIdentifier(UUID.randomUUID().toString());
          financePositionEntity.setCurrency(financePositionEntity.getCurrency().toUpperCase());
          userAccountEntity.getFinancePositionEntityList().add(financePositionEntity);
        });
    updateCurrencies(userAccountEntity.getFinancePositionEntityList());
    financePositionRepository.saveAll(toAddFinancePositionEntityList);
    userAccountRepository.save(userAccountEntity);
    return IteratorUtils.toList(toAddFinancePositionEntityList.iterator());
  }

  @Transactional
  @Override
  public List<FinancePositionEntity> putPositions(
      List<FinancePositionEntity> financePositionEntityList, String externalIdentifier) {
    checkPositionsExist(financePositionEntityList);
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    financePositionEntityList.forEach(
        entity -> {
          entity.setExternalIdentifier(UUID.randomUUID().toString());
          entity.setUserAccount(userAccountEntity);
          entity.setCurrency(entity.getCurrency().toUpperCase());
        });
    userAccountEntity.getFinancePositionEntityList().clear();
    userAccountEntity.getFinancePositionEntityList().addAll(financePositionEntityList);
    updateCurrencies(userAccountEntity.getFinancePositionEntityList());
    financePositionRepository.saveAll(financePositionEntityList);
    userAccountRepository.save(userAccountEntity);
    return IteratorUtils.toList(userAccountEntity.getFinancePositionEntityList().iterator());
  }

  private void updateCurrencies(List<FinancePositionEntity> financePositionEntityList) {
    financePositionEntityList.forEach(
        financePositionEntity -> {
          if (financePositionEntity.getType().isResolve()) {
            String resolvedCurrency =
                connectorFacade.getCurrency(financePositionEntity.getIdentifier());
            financePositionEntity.setCurrency(resolvedCurrency.toUpperCase());
          }
        });
  }

  @Transactional
  @Override
  public Void deletePosition(String userExternalIdentifier, String positionExternalIdentifier) {
    Long aLong = financePositionRepository.deleteByExternalIdentifier(positionExternalIdentifier);
    if (aLong == 0) {
      log.warn(
          "FinancePositionEntity with externalIdentifier '{}' could not be found and though not be deleted.",
          positionExternalIdentifier);
    }
    return null;
  }

  private UserAccountEntity getUserAccountEntity(String externalIdentifier) {
    return userAccountRepository
        .findByExternalIdentifier(externalIdentifier)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserAccountEntity.class.getName(),
                    String.format(
                        "Could not find user with externalIdentifier '%s'", externalIdentifier)));
  }

  private void checkPositionsExist(List<FinancePositionEntity> financePositionEntityList) {
    financePositionEntityList
        .stream()
        .map(FinancePositionEntity::getIdentifier)
        .forEach(connectorFacade::checkPositionExists);
  }
}
