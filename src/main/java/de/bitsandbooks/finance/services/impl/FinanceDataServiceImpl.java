package de.bitsandbooks.finance.services.impl;

import static reactor.core.publisher.Mono.just;

import de.bitsandbooks.finance.connectors.ConnectorFacade;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.dtos.*;
import de.bitsandbooks.finance.model.entities.Currency;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.repositories.FinancePositionRepository;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.services.FinanceDataService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

  private static final int TIMEOUT_SECONDS = 20;

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
        .flatMap(this::updatePositionPriceAndCurrency);
  }

  @Transactional
  @Override
  public List<FinancePositionEntity> addPositions(
      List<FinancePositionEntity> toAddFinancePositionEntityList, String externalIdentifier) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    Iterable<FinancePositionEntity> financePositionEntityIterable =
        Flux.fromIterable(toAddFinancePositionEntityList)
            .flatMap((this::updatePositionPriceAndCurrency))
            .map(
                entity -> {
                  entity.setUserAccount(userAccountEntity);
                  entity.setExternalIdentifier(UUID.randomUUID().toString());
                  userAccountEntity.getFinancePositionEntityList().add(entity);
                  return entity;
                })
            .collectList()
            .block(Duration.of(TIMEOUT_SECONDS, ChronoUnit.SECONDS));
    userAccountRepository.save(userAccountEntity);
    return financePositionEntityIterable == null
        ? Collections.emptyList()
        : IteratorUtils.toList(financePositionEntityIterable.iterator());
  }

  @Transactional
  @Override
  public List<FinancePositionEntity> putPositions(
      List<FinancePositionEntity> financePositionEntityList, String externalIdentifier) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    userAccountEntity.getFinancePositionEntityList().clear();
    Iterable<FinancePositionEntity> financePositionEntityIterable =
        Flux.fromIterable(financePositionEntityList)
            .flatMap((this::updatePositionPriceAndCurrency))
            .map(
                entity -> {
                  entity.setUserAccount(userAccountEntity);
                  entity.setExternalIdentifier(UUID.randomUUID().toString());
                  userAccountEntity.getFinancePositionEntityList().add(entity);
                  return entity;
                })
            .collectList()
            .block(Duration.of(TIMEOUT_SECONDS, ChronoUnit.SECONDS));
    userAccountRepository.save(userAccountEntity);
    return financePositionEntityIterable == null
        ? Collections.emptyList()
        : IteratorUtils.toList(financePositionEntityIterable.iterator());
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

  @Transactional
  @Override
  public Mono<FinancePositionEntity> putPosition(
      FinancePositionEntity financePositionEntity,
      String userAccountExternalIdentifier,
      String positionExternalIdentifier) {
    FinancePositionEntity existingFinancePositionEntity =
        financePositionRepository
            .findByExternalIdentifier(positionExternalIdentifier)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        FinancePositionEntity.class.getName(),
                        String.format(
                            "Could not find finance position with externalIdentifier '%s'",
                            positionExternalIdentifier)));
    updatePositionValues(financePositionEntity, existingFinancePositionEntity);
    FinancePositionEntity savedFinancePositionEntity =
        financePositionRepository.save(existingFinancePositionEntity);
    return updatePositionPriceAndCurrency(savedFinancePositionEntity);
  }

  private void updatePositionValues(
      FinancePositionEntity financePositionEntity,
      FinancePositionEntity existingFinancePositionEntity) {
    if (!financePositionEntity.getType().equals(existingFinancePositionEntity.getType())) {
      existingFinancePositionEntity.setType(financePositionEntity.getType());
    }
    if (!financePositionEntity.getName().equals(existingFinancePositionEntity.getName())) {
      existingFinancePositionEntity.setName(financePositionEntity.getName());
    }
    if (!financePositionEntity.getAmount().equals(existingFinancePositionEntity.getAmount())) {
      existingFinancePositionEntity.setAmount(financePositionEntity.getAmount());
    }
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

  private Mono<FinancePositionEntity> updatePositionPriceAndCurrency(FinancePositionEntity entity) {
    boolean isCurrency = Currency.isCurrency(entity.getIdentifier());
    if (!isCurrency) {
      return connectorFacade
          .getActualValue(entity.getIdentifier())
          .map(
              valueDto -> {
                entity.setCurrency(valueDto.getCurrency().toUpperCase());
                entity.setPrice(valueDto.getPrice().multiply(entity.getAmount()));
                return entity;
              });
    } else {
      entity.setCurrency(entity.getIdentifier());
      entity.setPrice(entity.getAmount());
      return Mono.just(entity);
    }
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
}
