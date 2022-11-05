package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.FinanceDataControllerInterface;
import de.bitsandbooks.finance.model.dtos.FinanceTotalDto;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import de.bitsandbooks.finance.services.FinanceDataService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/finance")
@RestController
public class FinanceDataControllerImpl implements FinanceDataControllerInterface {

  @NonNull private final FinanceDataService financeDataService;

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'read')")
  @RequestMapping(
    value = "/total/{userAccountExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public Mono<FinanceTotalDto> getTotalAmount(
      @NotEmpty @PathVariable("userAccountExternalIdentifier") String userAccountExternalIdentifier,
      @NotNull @RequestParam("currency") String currency,
      @RequestParam("byType") Boolean byType) {
    log.info("Calculating total amount for user '{}'", userAccountExternalIdentifier);
    return financeDataService.getTotalAmount(
        userAccountExternalIdentifier, currency.toUpperCase(), byType);
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'read')")
  @RequestMapping(
    value = "/positions/{userAccountExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public Flux<FinancePositionEntity> getAllPositions(
      @NotEmpty @PathVariable("userAccountExternalIdentifier")
          String userAccountExternalIdentifier) {
    log.info("Getting all positions for user '{}'", userAccountExternalIdentifier);
    return financeDataService.getAllPositions(userAccountExternalIdentifier);
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/positions/{userAccountExternalIdentifier}",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Flux<FinancePositionEntity> addPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty @PathVariable("userAccountExternalIdentifier")
          String userAccountExternalIdentifier) {
    log.info("Add positions for user '{}'", userAccountExternalIdentifier);
    return Mono.fromCallable(
            () ->
                financeDataService.addPositions(
                    financePositionEntityList, userAccountExternalIdentifier))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany(Flux::fromIterable);
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/positions/{userAccountExternalIdentifier}",
    method = RequestMethod.PUT,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Flux<FinancePositionEntity> putPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty @PathVariable("userAccountExternalIdentifier")
          String userAccountExternalIdentifier) {
    log.info("Put positions for user '{}'", userAccountExternalIdentifier);
    return Mono.fromCallable(
            () ->
                financeDataService.putPositions(
                    financePositionEntityList, userAccountExternalIdentifier))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany(Flux::fromIterable);
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/positions/{userAccountExternalIdentifier}/{positionExternalIdentifier}",
    method = RequestMethod.DELETE,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.OK)
  public Mono<Void> deletePosition(
      @NotEmpty @PathVariable("userAccountExternalIdentifier") String userAccountExternalIdentifier,
      @NotEmpty @PathVariable("positionExternalIdentifier") String positionExternalIdentifier) {
    log.info(
        "Delete position '{}' for user '{}'",
        positionExternalIdentifier,
        userAccountExternalIdentifier);
    return Mono.fromCallable(
            () ->
                financeDataService.deletePosition(
                    userAccountExternalIdentifier, positionExternalIdentifier))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userAccountExternalIdentifier, 'EXTERNAL_ACCOUNT_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/positions/{userAccountExternalIdentifier}/{positionExternalIdentifier}",
    method = RequestMethod.PUT,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.OK)
  public Mono<FinancePositionEntity> putPosition(
      @Valid @NotNull @RequestBody FinancePositionEntity financePositionEntity,
      @NotEmpty @PathVariable("userAccountExternalIdentifier") String userAccountExternalIdentifier,
      @NotEmpty @PathVariable("positionExternalIdentifier") String positionExternalIdentifier) {
    log.info(
        "Update position '{}' for user '{}'",
        positionExternalIdentifier,
        userAccountExternalIdentifier);
    return financeDataService.putPosition(
        financePositionEntity, userAccountExternalIdentifier, positionExternalIdentifier);
  }
}
