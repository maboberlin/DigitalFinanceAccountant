package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.SnapshotControllerInterface;
import de.bitsandbooks.finance.model.dtos.UserSnapshotsDto;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import de.bitsandbooks.finance.services.SnapshotService;
import javax.validation.constraints.NotEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/snapshots")
@RestController
public class SnapshotControllerInterfaceImpl implements SnapshotControllerInterface {

  @NonNull private final SnapshotService snapshotService;

  @Override
  @PreAuthorize("hasPermission(#userExternalIdentifier, 'USER_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/{userExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserSnapshotsDto> getUserSnapshots(
      @NotEmpty @PathVariable(name = "userExternalIdentifier") String userExternalIdentifier) {
    log.info("Fetching snapshots for user '{}'", userExternalIdentifier);
    return Mono.fromCallable(() -> snapshotService.getSnapshots(userExternalIdentifier));
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'read')")
  @RequestMapping(
    value = "/{userExternalIdentifier}/{userAccountExternalIdentifier}",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<SnapshotEntity> createSnapshot(
      @NotEmpty @PathVariable(name = "userAccountExternalIdentifier")
          String userAccountExternalIdentifier,
      @NotEmpty @RequestParam("currency") String currency) {
    log.info("Creating snapshot for userAccountIdentifier '{}'", userAccountExternalIdentifier);
    return snapshotService.createSnapshot(userAccountExternalIdentifier, currency);
  }
}
