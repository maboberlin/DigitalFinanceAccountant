package de.bitsandbooks.finance.connectors.helpers;

import de.bitsandbooks.finance.config.CacheConfiguration;
import de.bitsandbooks.finance.connectors.FinanceDataConnector;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinanceConnectorSearcher {

  @NonNull private final List<FinanceDataConnector> connectorList;

  @Cacheable(value = CacheConfiguration.CONNECTOR_CACHE, unless = "#result == null")
  public FinanceDataConnector findConnector(String identifier) {
    for (FinanceDataConnector financeDataConnector : connectorList) {
      if (financeDataConnector.hasPositionDataForLastDailyClosingPrice(identifier)) {
        return financeDataConnector;
      }
    }
    return null;
  }
}
