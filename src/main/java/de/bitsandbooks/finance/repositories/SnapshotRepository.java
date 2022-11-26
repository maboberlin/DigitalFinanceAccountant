package de.bitsandbooks.finance.repositories;

import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SnapshotRepository extends PagingAndSortingRepository<SnapshotEntity, String> {

  List<SnapshotEntity> findByUserAccount(UserAccountEntity userAccountEntity);
}
