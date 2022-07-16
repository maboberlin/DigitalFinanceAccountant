package de.bitsandbooks.finance.repositories;

import de.bitsandbooks.finance.model.entities.FinancePositionEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FinancePositionRepository
    extends PagingAndSortingRepository<FinancePositionEntity, String> {
  List<FinancePositionEntity> findByUserAccount(UserAccountEntity userAccountEntity);

  Long deleteByExternalIdentifier(String externalIdentifier);
}
