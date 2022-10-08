package de.bitsandbooks.finance.services.impl;

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
import java.util.stream.Collectors;
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

  @Transactional(readOnly = true)
  @Override
  public Flux<FinancePositionEntity> getAllPositions(String externalIdentifier) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    List<FinancePositionEntity> financePositionEntityList =
        financePositionRepository.findByUserAccount(userAccountEntity);
    return Flux.fromIterable(financePositionEntityList)
        .flatMap(
            financePositionEntity ->
                positionToPrice(financePositionEntity)
                    .flatMap(
                        priceDto -> {
                          financePositionEntity.setValue(priceDto.getPrice());
                          return just(financePositionEntity);
                        }));
  }

  private Mono<PriceDto> positionToPrice(FinancePositionEntity position) {
    return position.getType().isResolve()
        ? connectorFacade
            .getActualPrice(position.getIdentifier())
            .map(priceDto -> new PriceDto(priceDto.getPrice().multiply(position.getAmount())))
        : just(new PriceDto(position.getAmount()));
  }

  @Transactional
  @Override
  public List<FinancePositionEntity> addPositions(
      List<FinancePositionEntity> toAddFinancePositionEntityList, String externalIdentifier) {
    checkPositionsExist(toAddFinancePositionEntityList);
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    Map<String, FinancePositionEntity> existingFinancePositionEntityMap =
        userAccountEntity
            .getFinancePositionEntityList()
            .stream()
            .collect(Collectors.toMap(FinancePositionEntity::getIdentifier, entity -> entity));
    mergeOrAddPositions(
        userAccountEntity, toAddFinancePositionEntityList, existingFinancePositionEntityMap);
    userAccountRepository.save(userAccountEntity);
    return IteratorUtils.toList(toAddFinancePositionEntityList.iterator());
  }

  private void mergeOrAddPositions(
      UserAccountEntity userAccountEntity,
      List<FinancePositionEntity> toAddFinancePositionEntityList,
      Map<String, FinancePositionEntity> existingFinancePositionEntityMap) {
    toAddFinancePositionEntityList.forEach(
        toAddFinancePositionEntity -> {
          FinancePositionEntity existingFinancePositionEntity =
              existingFinancePositionEntityMap.get(toAddFinancePositionEntity.getIdentifier());
          if (isEqualPosition(toAddFinancePositionEntity, existingFinancePositionEntity)) {
            existingFinancePositionEntity.setAmount(
                existingFinancePositionEntity
                    .getAmount()
                    .add(toAddFinancePositionEntity.getAmount()));
          } else {
            toAddFinancePositionEntity.setExternalIdentifier(UUID.randomUUID().toString());
            toAddFinancePositionEntity.setUserAccount(userAccountEntity);
            toAddFinancePositionEntity.setCurrency(
                toAddFinancePositionEntity.getCurrency().toUpperCase());
            userAccountEntity.getFinancePositionEntityList().add(toAddFinancePositionEntity);
          }
        });
  }

  private boolean isEqualPosition(
      FinancePositionEntity toAddFinancePositionEntity,
      FinancePositionEntity existingFinancePositionEntity) {
    return existingFinancePositionEntity != null
        && existingFinancePositionEntity
            .getCurrency()
            .equals(toAddFinancePositionEntity.getCurrency())
        && existingFinancePositionEntity.getType().equals(toAddFinancePositionEntity.getType());
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
    Iterable<FinancePositionEntity> financePositionEntities =
        financePositionRepository.saveAll(financePositionEntityList);
    userAccountRepository.save(userAccountEntity);
    return IteratorUtils.toList(financePositionEntities.iterator());
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

  private void checkPositionsExist(List<FinancePositionEntity> financePositionEntityList) {
    financePositionEntityList
        .stream()
        .map(FinancePositionEntity::getIdentifier)
        .forEach(connectorFacade::checkPositionExists);
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

  private Mono<FinanceTotalDto> getFinanceTotalDtoInternal(
      String externalIdentifier, String currency) {
    return this.getAllPositions(externalIdentifier)
        .flatMap(el -> positionToCurrencyValue(el, currency))
        .map(el -> el.getValueDto().getValue())
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

  private FinanceTotalDto buildFinanceTotalDto(
      Map<PositionType, BigDecimal> positionTypeValueDtoMap, String currency) {
    BigDecimal total =
        positionTypeValueDtoMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    return new FinanceTotalDto(currency, total, positionTypeValueDtoMap);
  }

  private Mono<ValueWithTypeDto> positionToCurrencyValue(
      FinancePositionEntity position, String currency) {
    PriceDto priceDto = new PriceDto(position.getValue());
    Mono<ValueDto> valueDto =
        position.getCurrency().equals(currency)
            ? just(new ValueDto(currency, priceDto.getPrice()))
            : this.exchangeToCurrency(priceDto, position.getCurrency(), currency);
    return valueDto.map(el -> new ValueWithTypeDto(el, position.getType()));
  }

  private Mono<ValueDto> exchangeToCurrency(
      PriceDto priceDto, String fromCurrency, String toCurrency) {
    return connectorFacade
        .convertToCurrency(fromCurrency, toCurrency)
        .map(
            exchangePriceDto -> priceDto.getPrice().multiply(exchangePriceDto.getPriceNotRounded()))
        .map(value -> new ValueDto(toCurrency, value));
  }

  private Map<PositionType, BigDecimal> buildPositionTypeMap(
      Collection<ValueWithTypeDto> valueWithTypeDtoCollection) {
    Map<PositionType, BigDecimal> result = new HashMap<>();
    valueWithTypeDtoCollection.forEach(
        el -> {
          if (result.containsKey(el.getPositionType())) {
            BigDecimal mapEntry = result.get(el.getPositionType());
            BigDecimal value = el.getValueDto().getValueUnrounded();
            BigDecimal newMapEntry = mapEntry.add(value);
            result.put(el.getPositionType(), newMapEntry);
          } else {
            BigDecimal value = el.getValueDto().getValueUnrounded();
            result.put(el.getPositionType(), value == null ? BigDecimal.ZERO : value);
          }
        });
    return result;
  }
}
