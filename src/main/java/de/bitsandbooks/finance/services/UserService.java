package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.UserAccountEntity;
import java.util.List;

public interface UserService {
  UserAccountEntity createUser(UserAccountEntity userAccountEntity);

  UserAccountEntity getUser(String userExternalIdentifier);

  List<UserAccountEntity> getAllUsers();

  UserAccountEntity getUser(String eMail, String accountIdentifier);
}
