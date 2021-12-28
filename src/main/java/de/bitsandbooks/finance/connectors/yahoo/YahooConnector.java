package de.bitsandbooks.finance.connectors.yahoo;

import de.bitsandbooks.finance.connectors.AbstractFinanceDataConnector;
import de.bitsandbooks.finance.connectors.ConnectorType;
import de.bitsandbooks.finance.connectors.FinanceDataConnector;
import de.bitsandbooks.finance.connectors.ForexService;
import de.bitsandbooks.finance.connectors.helpers.RoundHelper;
import de.bitsandbooks.finance.connectors.yahoo.dto.Chart;
import de.bitsandbooks.finance.connectors.yahoo.dto.Meta;
import de.bitsandbooks.finance.connectors.yahoo.dto.Result;
import de.bitsandbooks.finance.connectors.yahoo.dto.YahooChartDto;
import de.bitsandbooks.finance.model.Currency;
import de.bitsandbooks.finance.model.PriceDto;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Order(0)
@Component
@RequiredArgsConstructor
public class YahooConnector extends AbstractFinanceDataConnector
    implements FinanceDataConnector, ForexService {

  @NonNull private final YahooApi yahooApi;

  @Override
  public ConnectorType getConnectorType() {
    return ConnectorType.YAHOO;
  }

  @Override
  public Mono<PriceDto> getActualPrice(String identifier) {
    log.info(
        "Get actual quote price with '{}' API for identifier: {}",
        this.getConnectorType(),
        identifier);
    return yahooApi
        .getQuote(identifier)
        .map(yahooChartDto -> this.getRegularMarketPrice(identifier, yahooChartDto))
        .map(price -> new PriceDto(new BigDecimal(price)));
  }

  @Override
  public Mono<PriceDto> convertToCurrency(Currency fromDto, Currency toDto) {
    log.info(
        "Get actual currency price with '{}' API from currency '{}' to currency '{}'",
        this.getConnectorType(),
        fromDto.getIdentifier(),
        toDto.getIdentifier());
    return yahooApi
        .getCurrencyExchange(fromDto.getIdentifier(), toDto.getIdentifier())
        .map(
            yahooChartDto ->
                getRegularMarketPrice(
                    fromDto.getIdentifier() + " -> " + toDto.getIdentifier(), yahooChartDto))
        .map(price -> new PriceDto(new BigDecimal(price)));
  }

  private Double getRegularMarketPrice(String identifier, YahooChartDto yahooChartDto) {
    return Optional.ofNullable(yahooChartDto)
        .map(YahooChartDto::getChart)
        .map(Chart::getResult)
        .map(list -> list.isEmpty() ? null : list.get(0))
        .map(Result::getMeta)
        .map(Meta::getRegularMarketPrice)
        .map(RoundHelper::roundUpHalfDouble)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Yahoo API returned quote with missing data for identifier: " + identifier));
  }
}
