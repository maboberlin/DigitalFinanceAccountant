package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.FinancePositionEntity;
import de.bitsandbooks.finance.model.FinanceTotalDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface FinanceDataControllerInterface {
  Mono<FinanceTotalDto> getTotalAmount(
      @NotEmpty String userExternalIdentifier, @NotNull String currency, Boolean byType);

  List<FinancePositionEntity> getAllPositions(@NotEmpty String userExternalIdentifier);

  List<FinancePositionEntity> addPositions(
      @Valid @NotEmpty List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty String userExternalIdentifier);

  List<FinancePositionEntity> putPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty String userExternalIdentifier);

  Void deletePosition(
      @NotEmpty String userExternalIdentifier, @NotEmpty String positionExternalIdentifier);
}
