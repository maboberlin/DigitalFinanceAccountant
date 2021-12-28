package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.Currency;
import de.bitsandbooks.finance.model.PriceDto;
import reactor.core.publisher.Mono;

public interface ForexService {

  Mono<PriceDto> convertToCurrency(Currency fromCurrency, Currency toCurrency);
}
