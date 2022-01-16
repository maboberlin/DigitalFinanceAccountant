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
  Mono<FinanceTotalDto> getTotalAmount(@NotEmpty String userId, @NotNull String currency);

  List<FinancePositionEntity> getAllPositions(@NotEmpty String userId);

  Void addPositions(
      @Valid @NotEmpty List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty String userEmail);

  Void putPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty String userEmail);
}
