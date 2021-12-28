package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.FinanceDataControllerInterface;
import de.bitsandbooks.finance.model.Currency;
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
@RequestMapping("finance")
@RestController
public class FinanceDataControllerImpl implements FinanceDataControllerInterface {

  @NonNull private final FinanceDataService financeDataService;

  @Override
  @RequestMapping(
    value = "/total/{userEMail}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public Mono<FinanceTotalDto> getTotalAmount(
      @NotEmpty @PathVariable("userEMail") String userEMail,
      @NotNull @RequestParam("currency") Currency currency) {
    log.info("Calculating total amount for user '{}'", userEMail);
    return financeDataService.getTotalAmount(userEMail, currency);
  }

  @Override
  @RequestMapping(
    value = "/positions/{userEMail}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public List<FinancePositionEntity> getAllPositions(
      @NotEmpty @PathVariable("userEMail") String userEMail) {
    log.info("Getting all positions for user '{}'", userEMail);
    return financeDataService.getAllPositions(userEMail);
  }

  @Override
  @RequestMapping(
    value = "/positions/{userEMail}",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Void addPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty @PathVariable("userEMail") String userEMail) {
    log.info("Add positions for user '{}'", userEMail);
    financeDataService.addPositions(financePositionEntityList, userEMail);
    return null;
  }

  @Override
  @RequestMapping(
    value = "/positions/{userEMail}",
    method = RequestMethod.PUT,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Void putPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty @PathVariable("userEMail") String userEMail) {
    log.info("Put positions for user '{}'", userEMail);
    financeDataService.putPositions(financePositionEntityList, userEMail);
    return null;
  }
}
