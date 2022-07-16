package de.bitsandbooks.finance.repositories;

import de.bitsandbooks.finance.model.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, String> {
  Optional<UserEntity> findByExternalIdentifier(String externalIdentifier);

  Optional<UserEntity> findByMailAddress(String emailAddress);
}
