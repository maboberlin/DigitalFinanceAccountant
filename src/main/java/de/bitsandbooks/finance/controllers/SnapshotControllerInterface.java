package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import javax.validation.constraints.NotEmpty;
import reactor.core.publisher.Mono;

public interface SnapshotControllerInterface {

  Mono<UserSnapshotsDto> getUserSnapshots(@NotEmpty String userId);

  Mono<SnapshotEntity> createSnapshot(@NotEmpty String userAccountId, @NotEmpty String currency);
}
