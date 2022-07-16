package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.dtos.FinanceTotalDto;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import java.util.List;
import reactor.core.publisher.Mono;

public interface FinanceDataService {
  Mono<FinanceTotalDto> getTotalAmount(String userId, String currency, Boolean byType);

  List<FinancePositionEntity> getAllPositions(String userId);

  List<FinancePositionEntity> addPositions(
      List<FinancePositionEntity> positionDtoList, String userId);

  List<FinancePositionEntity> putPositions(
      List<FinancePositionEntity> positionDtoList, String userId);

  void deletePosition(String userExternalIdentifier, String positionExternalIdentifier);
}
