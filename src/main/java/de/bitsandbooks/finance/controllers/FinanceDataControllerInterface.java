package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.dtos.FinanceTotalDto;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinanceDataControllerInterface {
  Mono<FinanceTotalDto> getTotalAmount(
      @NotEmpty String userExternalIdentifier, @NotNull String currency, Boolean byType);

  Flux<FinancePositionEntity> getAllPositions(@NotEmpty String userExternalIdentifier);

  Flux<FinancePositionEntity> addPositions(
      @Valid @NotEmpty List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty String userExternalIdentifier);

  Flux<FinancePositionEntity> putPositions(
      @Valid @NotEmpty @RequestBody List<FinancePositionEntity> financePositionEntityList,
      @NotEmpty String userExternalIdentifier);

  Mono<Void> deletePosition(
      @NotEmpty String userExternalIdentifier, @NotEmpty String positionExternalIdentifier);
}
