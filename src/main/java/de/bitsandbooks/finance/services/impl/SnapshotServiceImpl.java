package de.bitsandbooks.finance.services.impl;

import static java.util.Optional.ofNullable;

import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.dtos.FinanceTotalDto;
import de.bitsandbooks.finance.model.dtos.PositionType;
import de.bitsandbooks.finance.model.dtos.SnapshotListDto;
import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.repositories.SnapshotRepository;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.repositories.UserRepository;
import de.bitsandbooks.finance.services.FinanceDataService;
import de.bitsandbooks.finance.services.SnapshotService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnapshotServiceImpl implements SnapshotService {

  @NonNull private final FinanceDataService financeDataService;

  @NonNull private final UserRepository userRepository;

  @NonNull private final UserAccountRepository userAccountRepository;

  @NonNull private final SnapshotRepository snapshotRepository;

  @Override
  @Transactional(readOnly = true)
  public UserSnapshotsDto getSnapshots(String userExternalIdentifier) {
    UserEntity userEntity = getUserEntity(userExternalIdentifier);
    List<UserAccountEntity> userAccountEntityList = userEntity.getUserAccountEntityList();
    Map<String, SnapshotListDto> accountIdSnapshotListMap = new HashMap<>();
    userAccountEntityList.forEach(
        userAccountEntity -> {
          List<SnapshotEntity> snapshotEntityList =
              snapshotRepository.findByUserAccount(userAccountEntity);
          snapshotEntityList.sort(Comparator.comparing(SnapshotEntity::getSnapshotTimestamp));
          snapshotEntityList.forEach(this::calculateSnapshotTotal);
          SnapshotListDto snapshotListDto =
              new SnapshotListDto(userAccountEntity.getAccountIdentifier(), snapshotEntityList);
          accountIdSnapshotListMap.put(userAccountEntity.getExternalIdentifier(), snapshotListDto);
        });
    return new UserSnapshotsDto(accountIdSnapshotListMap);
  }

  @Override
  public Mono<SnapshotEntity> createSnapshot(
      String userAccountExternalIdentifier, String currency) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(userAccountExternalIdentifier);
    Mono<FinanceTotalDto> totalAmount =
        financeDataService.getTotalAmount(userAccountExternalIdentifier, currency, true);
    return buildSnapShotEntity(userAccountEntity, totalAmount, currency)
        .map(snapshotRepository::save)
        .map(
            snapshotEntity -> {
              this.calculateSnapshotTotal(snapshotEntity);
              return snapshotEntity;
            });
  }

  private UserEntity getUserEntity(String userExternalIdentifier) {
    return userRepository
        .findByExternalIdentifier(userExternalIdentifier)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserEntity.class.getName(),
                    String.format(
                        "Couldn't find user with externalIdentifier '%s'",
                        userExternalIdentifier)));
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

  private Mono<SnapshotEntity> buildSnapShotEntity(
      UserAccountEntity userAccountEntity,
      Mono<FinanceTotalDto> positionTypeValueMap,
      String currency) {
    SnapshotEntity snapshotEntity = new SnapshotEntity();
    snapshotEntity.setUserAccount(userAccountEntity);
    snapshotEntity.setSnapshotTimestamp(OffsetDateTime.now());
    snapshotEntity.setCurrency(currency);
    return positionTypeValueMap.map(
        financeTotalDto -> {
          Map<PositionType, BigDecimal> valueByPosition = financeTotalDto.getValueByPosition();
          valueByPosition.forEach(
              (type, value) -> {
                switch (type) {
                  case CURRENCY:
                    snapshotEntity.setCurrencyValue(value);
                    return;
                  case BOND:
                    snapshotEntity.setBondsValue(value);
                    return;
                  case STOCK:
                    snapshotEntity.setStocksValue(value);
                    return;
                  case KRYPTO:
                    snapshotEntity.setKryptoValue(value);
                    return;
                  case REAL_ESTATE:
                    snapshotEntity.setRealEstateValue(value);
                    return;
                  case RESOURCE:
                    snapshotEntity.setResourceValue(value);
                    return;
                }
              });
          return snapshotEntity;
        });
  }

  private void calculateSnapshotTotal(SnapshotEntity entity) {
    BigDecimal total = BigDecimal.ZERO;
    total =
        total
            .add(ofNullable(entity.getBondsValue()).orElse(BigDecimal.ZERO))
            .add(ofNullable(entity.getCurrencyValue()).orElse(BigDecimal.ZERO))
            .add(ofNullable(entity.getKryptoValue()).orElse(BigDecimal.ZERO))
            .add(ofNullable(entity.getRealEstateValue()).orElse(BigDecimal.ZERO))
            .add(ofNullable(entity.getResourceValue()).orElse(BigDecimal.ZERO))
            .add(ofNullable(entity.getStocksValue()).orElse(BigDecimal.ZERO));
    entity.setTotal(total);
  }
}
