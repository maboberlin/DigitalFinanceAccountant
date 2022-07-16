package de.bitsandbooks.finance.connectors.alphavantage;

import de.bitsandbooks.finance.connectors.AbstractFinanceDataConnector;
import de.bitsandbooks.finance.connectors.ConnectorType;
import de.bitsandbooks.finance.connectors.FinanceDataConnector;
import de.bitsandbooks.finance.connectors.ForexService;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageCurrencyDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageQuoteDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageTimeSeriesDailyAdjustedDto;
import de.bitsandbooks.finance.connectors.helpers.RoundHelper;
import de.bitsandbooks.finance.model.dtos.PriceDto;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Order(10)
@Component
@RequiredArgsConstructor
public class AlphaVantageConnector extends AbstractFinanceDataConnector
    implements FinanceDataConnector, ForexService {

  @NonNull private final AlphaVantageApi alphaVantageApi;

  @Override
  public ConnectorType getConnectorType() {
    return ConnectorType.ALPHA_VANTAGE;
  }

  @Override
  public Mono<PriceDto> getActualPrice(String identifier) {
    log.info(
        "Get actual quote price with '{}' API for identifier: {}",
        this.getConnectorType(),
        identifier);
    return getLastDailyPriceFromQuote(identifier);
  }

  private Mono<PriceDto> getLastDailyPriceFromQuote(String identifier) {
    return alphaVantageApi
        .getQuote(identifier)
        .map(AlphaVantageQuoteDto::getGlobalQuote)
        .map(AlphaVantageQuoteDto.GlobalQuote::get_05Price)
        .map(BigDecimal::new)
        .map(RoundHelper::roundUpHalfBigDecimalDouble)
        .switchIfEmpty(
            Mono.error(
                () ->
                    new IllegalStateException(
                        "AlphaVantage API returned quote with missing daily price data for identifier: "
                            + identifier)))
        .map(PriceDto::new);
  }

  private Mono<PriceDto> getLastDailyPriceFromDailyAdjusted(String identifier) {
    return alphaVantageApi
        .getTimeSeriesDailyAdjusted(identifier)
        .map(el -> getMostActualDayData(identifier, el))
        .map(
            el ->
                new PriceDto(
                    RoundHelper.roundUpHalfBigDecimalDouble(new BigDecimal(el.get_4Close()))));
  }

  @Override
  public Mono<PriceDto> convertToCurrency(String fromCurrency, String toCurrency) {
    log.info(
        "Get actual currency price with '{}' API from currency '{}' to currency '{}'",
        this.getConnectorType(),
        fromCurrency,
        toCurrency);
    return alphaVantageApi
        .getCurrencyExchange(fromCurrency, toCurrency)
        .map(AlphaVantageCurrencyDto::getCurrencyData)
        .map(AlphaVantageCurrencyDto.CurrencyData::get_5ExchangeRate)
        .map(RoundHelper::roundUpHalfBigDecimalDouble)
        .switchIfEmpty(
            Mono.error(
                () ->
                    new IllegalStateException(
                        "AlphaVantage API returned exchange rate with missing currency data for identifiers: "
                            + fromCurrency
                            + " -> "
                            + toCurrency)))
        .map(PriceDto::new);
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
