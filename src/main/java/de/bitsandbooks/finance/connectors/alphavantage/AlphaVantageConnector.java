package de.bitsandbooks.finance.connectors.alphavantage;

import de.bitsandbooks.finance.connectors.ConnectorType;
import de.bitsandbooks.finance.connectors.FinanceDataConnector;
import de.bitsandbooks.finance.connectors.ForexService;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageCurrencyDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageQuoteDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageTimeSeriesDailyAdjustedDto;
import de.bitsandbooks.finance.model.dtos.ExchangeRateDto;
import de.bitsandbooks.finance.model.dtos.ValueDto;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlphaVantageConnector implements FinanceDataConnector, ForexService {

  private static final String DEFAULT_ALPHAVANTAGE_CURRENCY = "USD";

  @NonNull private final AlphaVantageApi alphaVantageApi;

  @Override
  public ConnectorType getConnectorType() {
    return ConnectorType.ALPHA_VANTAGE;
  }

  @Override
  public int getOrder() {
    return 10;
  }

  @Override
  public Mono<ValueDto> getActualValue(String identifier) {
    log.info(
        "Get actual quote price with '{}' API for identifier: {}",
        this.getConnectorType(),
        identifier);
    return getLastDailyPriceFromQuote(identifier)
        .map(price -> new ValueDto(DEFAULT_ALPHAVANTAGE_CURRENCY, price));
  }

  private Mono<BigDecimal> getLastDailyPriceFromQuote(String identifier) {
    return alphaVantageApi
        .getQuote(identifier)
        .map(AlphaVantageQuoteDto::getGlobalQuote)
        .map(AlphaVantageQuoteDto.GlobalQuote::get_05Price)
        .map(BigDecimal::new)
        .switchIfEmpty(
            Mono.error(
                () ->
                    new IllegalStateException(
                        "AlphaVantage API returned quote with missing daily price data for identifier: "
                            + identifier)));
  }

  private Mono<BigDecimal> getLastDailyPriceFromDailyAdjusted(String identifier) {
    return alphaVantageApi
        .getTimeSeriesDailyAdjusted(identifier)
        .map(el -> getMostActualDayData(identifier, el))
        .map(el -> new BigDecimal(el.get_4Close()));
  }

  @Override
  public Mono<ExchangeRateDto> convertToCurrency(String fromCurrency, String toCurrency) {
    log.info(
        "Get actual currency price with '{}' API from currency '{}' to currency '{}'",
        this.getConnectorType(),
        fromCurrency,
        toCurrency);
    return alphaVantageApi
        .getCurrencyExchange(fromCurrency, toCurrency)
        .map(AlphaVantageCurrencyDto::getCurrencyData)
        .map(AlphaVantageCurrencyDto.CurrencyData::get_5ExchangeRate)
        .switchIfEmpty(
            Mono.error(
                () ->
                    new IllegalStateException(
                        "AlphaVantage API returned exchange rate with missing currency data for identifiers: "
                            + fromCurrency
                            + " -> "
                            + toCurrency)))
        .map(price -> new ExchangeRateDto(toCurrency, price));
  }

  private AlphaVantageTimeSeriesDailyAdjustedDto.DailyData getMostActualDayData(
      String identifier,
      AlphaVantageTimeSeriesDailyAdjustedDto alphaVantageTimeSeriesDailyAdjustedDto) {
    return alphaVantageTimeSeriesDailyAdjustedDto
        .getDailyDataMap()
        .entrySet()
        .stream()
        .sorted(compareByDateComparator())
        .map(Map.Entry::getValue)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "AlphaVantage API returned daily time series with empty daily time series list of data points for identifier: "
                        + identifier));
  }

  private Comparator<? super Map.Entry<String, AlphaVantageTimeSeriesDailyAdjustedDto.DailyData>>
      compareByDateComparator() {
    return Map.Entry.comparingByKey(Comparator.reverseOrder());
  }
}
