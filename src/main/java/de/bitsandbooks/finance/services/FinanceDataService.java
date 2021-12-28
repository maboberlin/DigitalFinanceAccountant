package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.Currency;
import de.bitsandbooks.finance.model.FinancePositionEntity;
import de.bitsandbooks.finance.model.FinanceTotalDto;
import java.util.List;
import reactor.core.publisher.Mono;

public interface FinanceDataService {
  Mono<FinanceTotalDto> getTotalAmount(String userId, Currency currency);

  List<FinancePositionEntity> getAllPositions(String userId);

  void addPositions(List<FinancePositionEntity> positionDtoList, String userId);

  void putPositions(List<FinancePositionEntity> positionDtoList, String userId);
}
