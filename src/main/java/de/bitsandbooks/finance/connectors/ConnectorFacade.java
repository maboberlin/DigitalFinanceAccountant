package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.config.CacheConfiguration;
import de.bitsandbooks.finance.connectors.helpers.FinanceConnectorSearcher;
import de.bitsandbooks.finance.connectors.helpers.ForexServiceSearcher;
import de.bitsandbooks.finance.exceptions.PositionsNotExistingException;
import de.bitsandbooks.finance.model.dtos.PriceDto;
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

  private static final int TIMEOUT_SECONDS = 20;

  @NonNull private final ForexServiceSearcher forexServiceSearcher;

  @NonNull private final FinanceConnectorSearcher financeConnectorSearcher;

  @SneakyThrows
  @Cacheable(value = CacheConfiguration.QUOTE_CACHE, unless = "#result == null")
  public Mono<PriceDto> getActualPrice(String identifier) {
    CompletableFuture<FinanceDataConnector> connector = getConnector(identifier);
    return connector.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).getActualPrice(identifier).cache();
  }

  @Cacheable(value = CacheConfiguration.CURRENCY_EXCHANGE_CACHE, unless = "#result == null")
  public Mono<PriceDto> convertToCurrency(String fromCurrency, String toCurrency) {
    ForexService forexService = forexServiceSearcher.getBestForexService();
    return forexService.convertToCurrency(fromCurrency, toCurrency).cache();
  }

  public void checkPositionExists(String identifier) {
    CompletableFuture<FinanceDataConnector> connector = getConnector(identifier);
    try {
      connector.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw buildNotFoundException(identifier);
    }
  }

  private CompletableFuture<FinanceDataConnector> getConnector(String identifier) {
    return CompletableFuture.supplyAsync(
        () ->
            Optional.ofNullable(financeConnectorSearcher.findConnector(identifier))
                .orElseThrow(() -> buildNotFoundException(identifier)));
  }

  private PositionsNotExistingException buildNotFoundException(String identifier) {
    return new PositionsNotExistingException(
        "No connector found for finance data position: " + identifier);
  }
}
