package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.dtos.PriceDto;
import reactor.core.publisher.Mono;

public interface ForexService {

  Mono<PriceDto> convertToCurrency(String fromCurrency, String toCurrency);
}
