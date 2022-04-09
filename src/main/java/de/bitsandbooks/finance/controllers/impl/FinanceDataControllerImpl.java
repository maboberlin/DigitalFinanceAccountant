package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.FinanceDataControllerInterface;
import de.bitsandbooks.finance.model.FinancePositionEntity;
import de.bitsandbooks.finance.model.FinanceTotalDto;
import de.bitsandbooks.finance.services.FinanceDataService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/finance")
@RestController
public class FinanceDataControllerImpl implements FinanceDataControllerInterface {

  @NonNull private final FinanceDataService financeDataService;

  @Override
  @RequestMapping(
    value = "/total/{userExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public Mono<FinanceTotalDto> getTotalAmount(
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier,
      @NotNull @RequestParam("currency") String currency,
      @RequestParam("byType") Boolean byType) {
    log.info("Calculating total amount for user '{}'", userExternalIdentifier);
    return financeDataService.getTotalAmount(
        userExternalIdentifier, currency.toUpperCase(), byType);
  }

  @Override
  @RequestMapping(
    value = "/positions/{userExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public List<FinancePositionEntity> getAllPositions(
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier) {
    log.info("Getting all positions for user '{}'", userExternalIdentifier);
    return financeDataService.getAllPositions(userExternalIdentifier);
  }

  @Override
  @RequestMapping(
    value = "/positions/{userExternalIdentifier}",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Void addPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier) {
    log.info("Add positions for user '{}'", userExternalIdentifier);
    financeDataService.addPositions(financePositionEntityList, userExternalIdentifier);
    return null;
  }

  @Override
  @RequestMapping(
    value = "/positions/{userExternalIdentifier}",
    method = RequestMethod.PUT,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Void putPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier) {
    log.info("Put positions for user '{}'", userExternalIdentifier);
    financeDataService.putPositions(financePositionEntityList, userExternalIdentifier);
    return null;
  }
}
