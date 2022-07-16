package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.dtos.PriceDto;
import reactor.core.publisher.Mono;

public interface FinanceDataConnector {
  ConnectorType getConnectorType();

  boolean hasPositionDataForLastDailyClosingPrice(String identifier);

  Mono<PriceDto> getActualPrice(String identifier);
}
