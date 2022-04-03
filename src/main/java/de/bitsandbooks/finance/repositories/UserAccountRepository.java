package de.bitsandbooks.finance.repositories;

import de.bitsandbooks.finance.model.UserAccountEntity;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserAccountRepository
    extends PagingAndSortingRepository<UserAccountEntity, String> {
  Optional<UserAccountEntity> findByExternalIdentifier(String externalIdentifier);

  Optional<UserAccountEntity> findByMailAddressAndAccountIdentifier(
      String eMail, String accountIdentifier);
}
