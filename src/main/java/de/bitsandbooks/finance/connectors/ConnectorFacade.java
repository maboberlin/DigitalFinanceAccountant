package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.config.CacheConfiguration;
import de.bitsandbooks.finance.connectors.helpers.FinanceConnectorSearcher;
import de.bitsandbooks.finance.connectors.helpers.ForexServiceSearcher;
import de.bitsandbooks.finance.model.PriceDto;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConnectorFacade {

  @NonNull private final ForexServiceSearcher forexServiceSearcher;

  @NonNull private final FinanceConnectorSearcher financeConnectorSearcher;

  @SneakyThrows
  @Cacheable(value = CacheConfiguration.QUOTE_CACHE, unless = "#result == null")
  public Mono<PriceDto> getActualPrice(String identifier) {
    CompletableFuture<FinanceDataConnector> connector =
        CompletableFuture.supplyAsync(
            () ->
                Optional.ofNullable(financeConnectorSearcher.findConnector(identifier))
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "No connector found for finance data position: " + identifier)));
    return connector.get(1l, TimeUnit.MINUTES).getActualPrice(identifier).cache();
  }

  @Cacheable(value = CacheConfiguration.CURRENCY_EXCHANGE_CACHE, unless = "#result == null")
  public Mono<PriceDto> convertToCurrency(String fromCurrency, String toCurrency) {
    ForexService forexService = forexServiceSearcher.getBestForexService();
    return forexService.convertToCurrency(fromCurrency, toCurrency).cache();
  }
}
