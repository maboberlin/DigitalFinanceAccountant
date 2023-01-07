package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import reactor.core.publisher.Mono;

public interface SnapshotService {
  UserSnapshotsDto getSnapshots(String userExternalIdentifier);

  Mono<SnapshotEntity> createSnapshot(String userAccountExternalIdentifier, String currency);
}
