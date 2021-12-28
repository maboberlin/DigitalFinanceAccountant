package de.bitsandbooks.finance.repositories;

import de.bitsandbooks.finance.model.FinancePositionEntity;
import de.bitsandbooks.finance.model.UserEntity;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FinancePositionRepository
    extends PagingAndSortingRepository<FinancePositionEntity, String> {
  List<FinancePositionEntity> findByUser(UserEntity userEntity);
}
