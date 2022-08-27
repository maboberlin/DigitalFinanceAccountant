package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.dtos.FinanceTotalDto;
import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import java.util.List;
import reactor.core.publisher.Mono;

public interface FinanceDataService {
  Mono<FinanceTotalDto> getTotalAmount(String userAccountId, String currency, Boolean byType);

  List<FinancePositionEntity> getAllPositions(String userAccountId);

  List<FinancePositionEntity> addPositions(
      List<FinancePositionEntity> positionDtoList, String userAccountId);

  List<FinancePositionEntity> putPositions(
      List<FinancePositionEntity> positionDtoList, String userAccountId);

  void deletePosition(String userExternalIdentifier, String positionExternalIdentifier);
}
