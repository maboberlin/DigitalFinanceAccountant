package de.bitsandbooks.finance.services.impl;

import de.bitsandbooks.finance.connectors.ConnectorFacade;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.dtos.*;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.repositories.FinancePositionRepository;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.repositories.UserRepository;
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

  @NonNull private final UserRepository userRepository;

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
  public List<FinancePositionEntity> getAllPositions(String externalIdentifier) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    return financePositionRepository.findByUserAccount(userAccountEntity);
  }

  @Transactional
  @Override
  public List<FinancePositionEntity> addPositions(
      List<FinancePositionEntity> financePositionEntityList, String externalIdentifier) {
    checkPositionsExist(financePositionEntityList);
    UserAccountEntity userAccountEntity = getUserAccountEntity(externalIdentifier);
    financePositionEntityList.forEach(
        entity -> {
          entity.setExternalIdentifier(UUID.randomUUID().toString());
          entity.setUserAccount(userAccountEntity);
          userAccountEntity.getFinancePositionEntityList().add(entity);
          entity.setCurrency(entity.getCurrency().toUpperCase());
        });
    Iterable<FinancePositionEntity> financePositionEntities =
        financePositionRepository.saveAll(financePositionEntityList);
    userAccountRepository.save(userAccountEntity);
    return IteratorUtils.toList(financePositionEntities.iterator());
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
  public void deletePosition(String userExternalIdentifier, String positionExternalIdentifier) {
    Long aLong = financePositionRepository.deleteByExternalIdentifier(positionExternalIdentifier);
    if (aLong == 0) {
      log.warn(
          "FinancePositionEntity with externalIdentifier '{}' could not be found and though not be deleted.",
          positionExternalIdentifier);
    }
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
    List<FinancePositionEntity> positionEntityList = this.getAllPositions(externalIdentifier);
    return Flux.fromStream(positionEntityList.stream())
        .flatMap(el -> positionToActualValue(el, currency))
        .map(el -> el.getValueDto().getValue())
        .reduceWith(() -> BigDecimal.ZERO, BigDecimal::add)
        .map(el -> new FinanceTotalDto(currency, el, null));
  }

  private Mono<FinanceTotalDto> getFinanceTotalDtoInternalByType(
      String externalIdentifier, String currency) {
    List<FinancePositionEntity> positionEntityList = this.getAllPositions(externalIdentifier);
    return Flux.fromStream(positionEntityList.stream())
        .flatMap(el -> positionToActualValue(el, currency))
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

  private Mono<ValueWithTypeDto> positionToActualValue(
      FinancePositionEntity position, String currency) {
    Mono<PriceDto> priceDto = positionToPrice(position);
    Mono<ValueDto> valueDto = priceToValue(position, currency, priceDto);
    return valueDto.map(el -> new ValueWithTypeDto(el, position.getType()));
  }

  private Mono<PriceDto> positionToPrice(FinancePositionEntity position) {
    return position.getType() == PositionType.CURRENCY
        ? Mono.just(new PriceDto(position.getAmount()))
        : connectorFacade.getActualPrice(position.getIdentifier());
  }

  private Mono<ValueDto> priceToValue(
      FinancePositionEntity position, String currency, Mono<PriceDto> priceDto) {
    Mono<ValueDto> valueDto =
        (position.getCurrency().equals(currency))
            ? priceDto.map(el -> new ValueDto(currency, el.getPrice()))
            : priceDto.flatMap(el -> this.exchangeToCurrency(el, position.getCurrency(), currency));
    return position.getType() == PositionType.CURRENCY
        ? valueDto
        : valueDto.map(el -> new ValueDto(currency, el.getValue().multiply(position.getAmount())));
  }

  private Mono<ValueDto> exchangeToCurrency(
      PriceDto priceDto, String fromCurrency, String toCurrency) {
    return connectorFacade
        .convertToCurrency(fromCurrency, toCurrency)
        .map(exchangePriceDto -> priceDto.getPrice().multiply(exchangePriceDto.getPrice()))
        .map(value -> new ValueDto(toCurrency, value));
  }

  private Map<PositionType, BigDecimal> buildPositionTypeMap(
      Collection<ValueWithTypeDto> valueWithTypeDtoCollection) {
    Map<PositionType, BigDecimal> result = new HashMap<>();
    valueWithTypeDtoCollection.forEach(
        el -> {
          if (result.containsKey(el.getPositionType())) {
            BigDecimal mapEntry = result.get(el.getPositionType());
            BigDecimal value = el.getValueDto().getValue();
            BigDecimal newMapEntry = mapEntry.add(value);
            result.put(el.getPositionType(), newMapEntry);
          } else {
            BigDecimal value = el.getValueDto().getValue();
            result.put(el.getPositionType(), value == null ? BigDecimal.ZERO : value);
          }
        });
    return result;
  }
}
