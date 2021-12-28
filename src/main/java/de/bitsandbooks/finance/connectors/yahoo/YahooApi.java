package de.bitsandbooks.finance.connectors.yahoo;

import de.bitsandbooks.finance.connectors.AbstractApi;
import de.bitsandbooks.finance.connectors.ConnectorType;
import de.bitsandbooks.finance.connectors.yahoo.dto.YahooChartDto;
import de.bitsandbooks.finance.exceptions.ApiException;
import de.bitsandbooks.finance.exceptions.ConnectorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class YahooApi extends AbstractApi {

  private static final String QUERY_BY_CHART_IDENTIFIER = "/chart/{identifier}";

  private final WebClient yahooWebClient;

  public YahooApi(
      @Value("${dfa.yahoo.base-url}") String baseUrl,
      @Value("${dfa.request.timeout.ms}") Integer requestTimeOutMillis) {
    this.yahooWebClient = this.initiateDefaultWebClient(baseUrl, requestTimeOutMillis);
  }

  public Mono<YahooChartDto> getQuote(String identifier) {
    Function<UriBuilder, URI> queryByIdentifier = getChartByIdentifier(identifier);
    return performGetRequest(queryByIdentifier);
  }

  public Mono<YahooChartDto> getCurrencyExchange(String fromIdentifier, String toIdentifier) {
    String identifier = buildCurrencyExchangeIdentifier(fromIdentifier, toIdentifier);
    Function<UriBuilder, URI> queryByIdentifier = getChartByIdentifier(identifier);
    return performGetRequest(queryByIdentifier);
  }

  private String buildCurrencyExchangeIdentifier(String fromIdentifier, String toIdentifier) {
    return String.format("%s%s=X", fromIdentifier.toUpperCase(), toIdentifier.toUpperCase());
  }

  private Function<UriBuilder, URI> getChartByIdentifier(String identifier) {
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("identifier", identifier);

    return builder -> builder.path(QUERY_BY_CHART_IDENTIFIER).build(urlParams);
  }

  private Mono<YahooChartDto> performGetRequest(Function<UriBuilder, URI> uriFunction) {
    return yahooWebClient
        .get()
        .uri(uriFunction)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(
            res -> {
              if (res.statusCode().is2xxSuccessful()) {
                return res.bodyToMono(YahooChartDto.class);
              } else if (res.statusCode().is4xxClientError()) {
                return buildErrorMono(res, "Error in request with status code '%s'");
              } else if (res.statusCode().is5xxServerError()) {
                return buildErrorMono(res, "Error from server with status code '%s'");
              } else {
                return buildErrorMono(res, "Error with status code '%s'");
              }
            })
        .onErrorMap(e -> new ConnectorException(e.getMessage(), ConnectorType.YAHOO, e));
  }

  private Mono<YahooChartDto> buildErrorMono(ClientResponse res, String msgPattern) {
    String msg = String.format(msgPattern, res.statusCode());
    return Mono.error(new ApiException(msg));
  }
}
