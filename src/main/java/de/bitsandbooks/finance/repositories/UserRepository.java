package de.bitsandbooks.finance.repositories;

import de.bitsandbooks.finance.model.UserEntity;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, String> {
  Optional<UserEntity> findByMailAddress(String eMail);
}
