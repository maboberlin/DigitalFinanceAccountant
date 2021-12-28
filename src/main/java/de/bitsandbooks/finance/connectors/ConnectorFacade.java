package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.config.CacheConfiguration;
import de.bitsandbooks.finance.connectors.helpers.FinanceConnectorSearcher;
import de.bitsandbooks.finance.connectors.helpers.ForexServiceSearcher;
import de.bitsandbooks.finance.model.Currency;
import de.bitsandbooks.finance.model.PriceDto;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConnectorFacade {

  @NonNull private final ForexServiceSearcher forexServiceSearcher;

  @NonNull private final FinanceConnectorSearcher financeConnectorSearcher;

  @Cacheable(CacheConfiguration.QUOTE_CACHE)
  public Mono<PriceDto> getActualPrice(String identifier) {
    FinanceDataConnector connector =
        Optional.ofNullable(financeConnectorSearcher.findConnector(identifier))
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No connector found for finance data position: " + identifier));
    return connector.getActualPrice(identifier);
  }

  @Cacheable(CacheConfiguration.CURRENCY_EXCHANGE_CACHE)
  public Mono<PriceDto> convertToCurrency(Currency fromCurrency, Currency toCurrency) {
    ForexService forexService = forexServiceSearcher.getBestForexService();
    return forexService.convertToCurrency(fromCurrency, toCurrency);
  }
}
