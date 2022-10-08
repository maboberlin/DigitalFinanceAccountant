package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.dtos.ExchangeRateDto;
import reactor.core.publisher.Mono;

public interface ForexService {

  Mono<ExchangeRateDto> convertToCurrency(String fromCurrency, String toCurrency);
}
