package de.bitsandbooks.finance.connectors.yahoo;

import de.bitsandbooks.finance.connectors.ConnectorType;
import de.bitsandbooks.finance.connectors.FinanceDataConnector;
import de.bitsandbooks.finance.connectors.ForexService;
import de.bitsandbooks.finance.connectors.yahoo.dto.Chart;
import de.bitsandbooks.finance.connectors.yahoo.dto.Meta;
import de.bitsandbooks.finance.connectors.yahoo.dto.Result;
import de.bitsandbooks.finance.connectors.yahoo.dto.YahooChartDto;
import de.bitsandbooks.finance.model.dtos.ExchangeRateDto;
import de.bitsandbooks.finance.model.dtos.ValueDto;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class YahooConnector implements FinanceDataConnector, ForexService {

  @NonNull private final YahooApi yahooApi;

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public ConnectorType getConnectorType() {
    return ConnectorType.YAHOO;
  }

  @Override
  public Mono<ValueDto> getActualValue(String identifier) {
    log.info(
        "Get actual quote price with '{}' API for identifier: {}",
        this.getConnectorType(),
        identifier);
    return yahooApi
        .getQuote(identifier)
        .map(
            yahooChartDto -> {
              Double price = this.getRegularMarketPrice(identifier, yahooChartDto);
              String currency = this.getCurrency(identifier, yahooChartDto);
              return new ValueDto(currency, new BigDecimal(price));
            });
  }

  @Override
  public Mono<ExchangeRateDto> convertToCurrency(String fromCurrency, String toCurrency) {
    log.info(
        "Get actual currency price with '{}' API from currency '{}' to currency '{}'",
        this.getConnectorType(),
        fromCurrency,
        toCurrency);
    return yahooApi
        .getCurrencyExchange(fromCurrency, toCurrency)
        .map(
            yahooChartDto ->
                getRegularMarketPrice(fromCurrency + " -> " + toCurrency, yahooChartDto))
        .map(price -> new ExchangeRateDto(toCurrency, new BigDecimal(price)));
  }

  private Double getRegularMarketPrice(String identifier, YahooChartDto yahooChartDto) {
    return Optional.ofNullable(yahooChartDto)
        .map(YahooChartDto::getChart)
        .map(Chart::getResult)
        .map(list -> list.isEmpty() ? null : list.get(0))
        .map(Result::getMeta)
        .map(Meta::getRegularMarketPrice)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Yahoo API returned quote with missing price data for identifier: "
                        + identifier));
  }

  private String getCurrency(String identifier, YahooChartDto yahooChartDto) {
    return Optional.ofNullable(yahooChartDto)
        .map(YahooChartDto::getChart)
        .map(Chart::getResult)
        .map(list -> list.isEmpty() ? null : list.get(0))
        .map(Result::getMeta)
        .map(Meta::getCurrency)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Yahoo API returned quote with missing currency data for identifier: "
                        + identifier));
  }
}
