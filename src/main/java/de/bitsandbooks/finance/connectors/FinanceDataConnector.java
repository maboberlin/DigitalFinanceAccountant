package de.bitsandbooks.finance.connectors;

import de.bitsandbooks.finance.model.dtos.ValueDto;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

public interface FinanceDataConnector extends Ordered {
  ConnectorType getConnectorType();

  Mono<ValueDto> getActualValue(String identifier);
}
