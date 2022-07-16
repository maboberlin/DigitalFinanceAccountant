package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.dtos.PriceDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFinanceDataConnector implements FinanceDataConnector {

  @Override
  public boolean hasPositionDataForLastDailyClosingPrice(String identifier) {
    log.info(
        "Check if '{}' API has position for identifier: {}", this.getConnectorType(), identifier);
    try {
      PriceDto priceDto = this.getActualPrice(identifier).block();
      return priceDto != null;
    } catch (Exception e) {
      log.warn(
          "Connector '{}' could not find position for identifier: '{}'",
          this.getConnectorType().toString(),
          identifier,
          e);
      return false;
    }
  }
}
