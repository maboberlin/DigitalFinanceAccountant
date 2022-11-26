package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.dtos.PositionType;
import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import java.math.BigDecimal;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

public interface SnapshotControllerInterface {

  Mono<UserSnapshotsDto> getUserSnapshots(@NotEmpty String userId);

  Mono<SnapshotEntity> createSnapshot(
      @NotEmpty String userAccountId, @NotNull Map<PositionType, BigDecimal> positionTypeValueMap);
}
