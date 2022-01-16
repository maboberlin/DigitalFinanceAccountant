package de.bitsandbooks.finance.services.impl;

import de.bitsandbooks.finance.connectors.ConnectorFacade;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.*;
import de.bitsandbooks.finance.repositories.FinancePositionRepository;
import de.bitsandbooks.finance.repositories.UserRepository;
import de.bitsandbooks.finance.services.FinanceDataService;
import java.math.BigDecimal;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class FinanceDataServiceImpl implements FinanceDataService {

  @NonNull private final ConnectorFacade connectorFacade;

  @NonNull private final FinancePositionRepository financePositionRepository;

  @NonNull private final UserRepository userRepository;

  @Override
  public Mono<FinanceTotalDto> getTotalAmount(String userEMail, String currency) {
    return getFinanceTotalDtoInternal(userEMail, currency);
  }

  @Override
  public List<FinancePositionEntity> getAllPositions(String userEMail) {
    UserEntity userEntity = getUserEntity(userEMail);
    return financePositionRepository.findByUser(userEntity);
  }

  @Override
  public void addPositions(
      List<FinancePositionEntity> financePositionEntityList, String userEMail) {
    UserEntity userEntity = getUserEntity(userEMail);
    financePositionEntityList.forEach(
        entity -> {
          entity.setUser(userEntity);
          userEntity.getFinancePositionEntityList().add(entity);
          entity.setCurrency(entity.getCurrency().toUpperCase());
        });
    financePositionRepository.saveAll(financePositionEntityList);
    userRepository.save(userEntity);
  }

  @Override
  public void putPositions(
      List<FinancePositionEntity> financePositionEntityList, String userEMail) {
    UserEntity userEntity = getUserEntity(userEMail);
    financePositionEntityList.forEach(
        entity -> {
          entity.setUser(userEntity);
          entity.setCurrency(entity.getCurrency().toUpperCase());
        });
    userEntity.getFinancePositionEntityList().clear();
    userEntity.getFinancePositionEntityList().addAll(financePositionEntityList);
    financePositionRepository.saveAll(financePositionEntityList);
    userRepository.save(userEntity);
  }

  private UserEntity getUserEntity(String userEMail) {
    return userRepository
        .findByMailAddress(userEMail)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserEntity.class.getName(),
                    String.format("Could not find user with eMail '%s'", userEMail)));
  }

  private Mono<FinanceTotalDto> getFinanceTotalDtoInternal(String userEMail, String currency) {
    List<FinancePositionEntity> positionEntityList = this.getAllPositions(userEMail);
    return Flux.fromStream(positionEntityList.stream())
        .flatMap(el -> positionToActualValue(el, currency))
        .map(ValueDto::getValue)
        .reduceWith(() -> BigDecimal.ZERO, BigDecimal::add)
        .map(el -> new FinanceTotalDto(currency, el));
  }

  private Mono<ValueDto> positionToActualValue(FinancePositionEntity position, String currency) {
    Mono<PriceDto> priceDto =
        position.getType() == PositionType.CURRENCY
            ? Mono.just(new PriceDto(position.getAmount()))
            : connectorFacade.getActualPrice(position.getIdentifier());
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
}
