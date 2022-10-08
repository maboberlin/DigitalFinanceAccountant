package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.dtos.ValueDto;
import reactor.core.publisher.Mono;

public interface FinanceDataConnector {
  ConnectorType getConnectorType();

  boolean hasPositionDataForLastDailyClosingPrice(String identifier);

  Mono<ValueDto> getActualValue(String identifier);

  Mono<Boolean> hasActualValue(String identifier);

  Mono<String> getCurrency(String identifier);
}
