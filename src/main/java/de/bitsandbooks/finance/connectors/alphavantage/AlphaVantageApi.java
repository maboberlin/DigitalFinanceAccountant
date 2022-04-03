package de.bitsandbooks.finance.connectors.alphavantage;

import de.bitsandbooks.finance.connectors.AbstractApi;
import de.bitsandbooks.finance.connectors.ConnectorType;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageCurrencyDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageQuoteDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphaVantageTimeSeriesDailyAdjustedDto;
import de.bitsandbooks.finance.connectors.alphavantage.dto.AlphavantageDto;
import de.bitsandbooks.finance.exceptions.ApiException;
import de.bitsandbooks.finance.exceptions.ConnectorException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class AlphaVantageApi extends AbstractApi {

  private static final String TIME_SERIES_DAILY_ADJUSTED_FUNCTION = "TIME_SERIES_DAILY_ADJUSTED";
  private static final String CURRENCY_EXCHANGE_RATE_FUNCTION = "CURRENCY_EXCHANGE_RATE";
  private static final String GLOBAL_QUOTE_FUNCTION = "GLOBAL_QUOTE";
  private static final String OUTPUT_COMPACT = "compact";

  private static final String QUERY_BASE_PATH = "/query";
  private static final String QUERY_BY_IDENTIFIER =
      "function={function}&symbol={identifier}&outputsize={output}&apikey={key}&datatype=json";
  private static final String QUERY_BY_QUOTE =
      "function={function}&symbol={identifier}&apikey={key}&datatype=json";
  private static final String QUERY_BY_CURRENCY =
      "function={function}&from_currency={fromCurrency}&to_currency={toCurrency}&apikey={key}&datatype=json";

  private final String apiToken;
  private final WebClient alphavantageWebClient;
  private final AlphaVantageRequestDelayService alphaVantageRequestDelayService;
  private final Validator validator;

  public AlphaVantageApi(
      @Value("${dfa.alphavantage.base-url}") String baseUrl,
      @Value("${dfa.alphavantage.api-token}") String apiToken,
      @Value("${dfa.request.timeout.ms}") Integer requestTimeOutMillis,
      AlphaVantageRequestDelayService alphaVantageRequestDelayService,
      Validator validator) {
    this.alphavantageWebClient = super.initiateDefaultWebClient(baseUrl, requestTimeOutMillis);
    this.apiToken = apiToken;
    this.alphaVantageRequestDelayService = alphaVantageRequestDelayService;
    this.validator = validator;
  }

  public Mono<AlphaVantageTimeSeriesDailyAdjustedDto> getTimeSeriesDailyAdjusted(String identifier)
      throws ConnectorException {
    Function<UriBuilder, URI> queryByIdentifier = getTimeSeriesQueryByIdentifier(identifier);
    return performGetRequest(queryByIdentifier, AlphaVantageTimeSeriesDailyAdjustedDto.class);
  }

  public Mono<AlphaVantageQuoteDto> getQuote(String identifier) {
    Function<UriBuilder, URI> queryByIdentifier = getQuoteQueryByIdentifier(identifier);
    return performGetRequest(queryByIdentifier, AlphaVantageQuoteDto.class);
  }

  public Mono<AlphaVantageCurrencyDto> getCurrencyExchange(String fromCurrency, String toCurrency) {
    Function<UriBuilder, URI> queryByCurrency = getQueryByCurrency(fromCurrency, toCurrency);
    return performGetRequest(queryByCurrency, AlphaVantageCurrencyDto.class);
  }

  private Function<UriBuilder, URI> getTimeSeriesQueryByIdentifier(String identifier) {
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("function", TIME_SERIES_DAILY_ADJUSTED_FUNCTION);
    urlParams.put("identifier", identifier);
    urlParams.put("output", OUTPUT_COMPACT);
    urlParams.put("key", apiToken);

    return builder -> builder.path(QUERY_BASE_PATH).query(QUERY_BY_IDENTIFIER).build(urlParams);
  }

  private Function<UriBuilder, URI> getQuoteQueryByIdentifier(String identifier) {
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("function", GLOBAL_QUOTE_FUNCTION);
    urlParams.put("identifier", identifier);
    urlParams.put("key", apiToken);

    return builder -> builder.path(QUERY_BASE_PATH).query(QUERY_BY_QUOTE).build(urlParams);
  }

  private Function<UriBuilder, URI> getQueryByCurrency(String fromCurrency, String toCurrency) {
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("function", CURRENCY_EXCHANGE_RATE_FUNCTION);
    urlParams.put("fromCurrency", fromCurrency);
    urlParams.put("toCurrency", toCurrency);
    urlParams.put("key", apiToken);

    return builder -> builder.path(QUERY_BASE_PATH).query(QUERY_BY_CURRENCY).build(urlParams);
  }

  private <T extends AlphavantageDto> Mono<T> performGetRequest(
      Function<UriBuilder, URI> uriFunction, Class<T> clazz) {
    return alphavantageWebClient
        .get()
        .uri(uriFunction)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(
            res -> {
              if (res.statusCode().is2xxSuccessful()) {
                return res.bodyToMono(clazz);
              } else if (res.statusCode().is4xxClientError()) {
                return buildErrorMono(res, "Error in request with status code '%s'");
              } else if (res.statusCode().is5xxServerError()) {
                return buildErrorMono(res, "Error from server with status code '%s'");
              } else {
                return buildErrorMono(res, "Error with status code '%s'");
              }
            })
        .publishOn(Schedulers.boundedElastic())
        .onErrorMap(e -> new ConnectorException(e.getMessage(), ConnectorType.ALPHA_VANTAGE, e))
        .<T>handle(
            (el, sink) -> {
              if (el.getErrorMessage() != null) {
                String errorMsg =
                    String.format(
                        "Error response from alphavantage api with message: %s",
                        el.getErrorMessage());
                sink.error(new ConnectorException(errorMsg, ConnectorType.ALPHA_VANTAGE));
              } else {
                sink.next(el);
              }
            })
        .doOnNext(this::validate)
        .doOnTerminate(alphaVantageRequestDelayService::resetTimer)
        .delaySubscription(Duration.ofMillis(alphaVantageRequestDelayService.getDelayRequests()));
  }

  private <T extends AlphavantageDto> void validate(T obj) {
    Set<ConstraintViolation<T>> violations = this.validator.validate(obj);
    if (violations.size() > 0) {
      throw new IllegalArgumentException(violations.toString());
    }
  }

  private <T extends AlphavantageDto> Mono<T> buildErrorMono(
      ClientResponse res, String msgPattern) {
    String msg = String.format(msgPattern, res.statusCode());
    return Mono.error(new ApiException(msg));
  }
}
