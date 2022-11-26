package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.dtos.PositionType;
import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import java.math.BigDecimal;
import java.util.Map;

public interface SnapshotService {
  UserSnapshotsDto getSnapshots(String userExternalIdentifier);

  SnapshotEntity createSnapshot(
      String userAccountExternalIdentifier, Map<PositionType, BigDecimal> positionTypeValueMap);
}
