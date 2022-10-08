package de.bitsandbooks.finance.connectors;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractFinanceDataConnector implements FinanceDataConnector {

  @Override
  public boolean hasPositionDataForLastDailyClosingPrice(String identifier) {
    log.info(
        "Check if '{}' API has position for identifier: {}", this.getConnectorType(), identifier);
    try {
      return this.hasActualValue(identifier).switchIfEmpty(Mono.just(Boolean.FALSE)).block();
    } catch (Exception e) {
      log.warn(
          "Connector '{}' could not find position for identifier: '{}'",
          this.getConnectorType().toString(),
          identifier);
      return false;
    }
  }
}
