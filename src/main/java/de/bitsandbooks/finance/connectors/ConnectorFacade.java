package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.config.CacheConfiguration;
import de.bitsandbooks.finance.connectors.helpers.ForexServiceSearcher;
import de.bitsandbooks.finance.exceptions.PositionsNotExistingException;
import de.bitsandbooks.finance.model.dtos.ExchangeRateDto;
import de.bitsandbooks.finance.model.dtos.ValueDto;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConnectorFacade {

  @NonNull private final ForexServiceSearcher forexServiceSearcher;

  @NonNull private final List<FinanceDataConnector> connectorList;

  @Cacheable(value = CacheConfiguration.QUOTE_CACHE, unless = "#result == null")
  public Mono<ValueDto> getActualValue(String identifier) {
    return getActualValueInternal(identifier, 0)
        .switchIfEmpty(Mono.error(buildNotFoundException(identifier)));
  }

  private Mono<ValueDto> getActualValueInternal(String identifier, int connectorIndex) {
    if (connectorIndex >= connectorList.size()) {
      return Mono.empty();
    }
    return connectorList
        .get(connectorIndex)
        .getActualValue(identifier)
        .switchIfEmpty(this.getActualValueInternal(identifier, connectorIndex + 1));
  }

  @Cacheable(value = CacheConfiguration.CURRENCY_EXCHANGE_CACHE, unless = "#result == null")
  public Mono<ExchangeRateDto> convertToCurrency(String fromCurrency, String toCurrency) {
    ForexService forexService = forexServiceSearcher.getBestForexService();
    return forexService.convertToCurrency(fromCurrency, toCurrency).cache();
  }

  private PositionsNotExistingException buildNotFoundException(String identifier) {
    return new PositionsNotExistingException(
        "No connector found for finance data position: " + identifier);
  }
}
