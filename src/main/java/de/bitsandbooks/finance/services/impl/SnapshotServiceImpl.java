package de.bitsandbooks.finance.services.impl;

import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.dtos.PositionType;
import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.repositories.SnapshotRepository;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.repositories.UserRepository;
import de.bitsandbooks.finance.services.SnapshotService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnapshotServiceImpl implements SnapshotService {

  @NonNull private final UserRepository userRepository;

  @NonNull private final UserAccountRepository userAccountRepository;

  @NonNull private final SnapshotRepository snapshotRepository;

  @Override
  @Transactional(readOnly = true)
  public UserSnapshotsDto getSnapshots(String userExternalIdentifier) {
    UserEntity userEntity = getUserEntity(userExternalIdentifier);
    List<UserAccountEntity> userAccountEntityList = userEntity.getUserAccountEntityList();
    Map<String, List<SnapshotEntity>> accountIdSnapshotMap = new HashMap<>();
    userAccountEntityList.forEach(
        userAccountEntity -> {
          List<SnapshotEntity> snapshotEntityList =
              snapshotRepository.findByUserAccount(userAccountEntity);
          accountIdSnapshotMap.put(userAccountEntity.getExternalIdentifier(), snapshotEntityList);
        });
    return new UserSnapshotsDto(accountIdSnapshotMap);
  }

  @Override
  public SnapshotEntity createSnapshot(
      String userAccountExternalIdentifier, Map<PositionType, BigDecimal> positionTypeValueMap) {
    UserAccountEntity userAccountEntity = getUserAccountEntity(userAccountExternalIdentifier);
    SnapshotEntity snapshotEntity = buildSnapShotEntity(userAccountEntity, positionTypeValueMap);
    return snapshotRepository.save(snapshotEntity);
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

  private SnapshotEntity buildSnapShotEntity(
      UserAccountEntity userAccountEntity, Map<PositionType, BigDecimal> positionTypeValueMap) {
    SnapshotEntity snapshotEntity = new SnapshotEntity();
    snapshotEntity.setUserAccount(userAccountEntity);
    snapshotEntity.setSnapshotTimestamp(OffsetDateTime.now());
    positionTypeValueMap.forEach(
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
  }
}
