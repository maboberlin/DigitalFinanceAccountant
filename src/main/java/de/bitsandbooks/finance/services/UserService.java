package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import java.util.List;

public interface UserService {
  UserAccountEntity createUserAccount(
      String userExternalIdentifier, UserAccountEntity userAccountEntity);

  UserEntity getUserByExternalIdentifier(String userExternalIdentifier);

  UserEntity getUserByEmailAddress(String emailAddress);

  List<UserEntity> getAllUsers();

  UserEntity getUser(String eMail, String accountIdentifier);

  UserEntity createUser(UserEntity userEntity);

  List<UserAccountEntity> getUserAccounts(String userExternalIdentifier);

  Void deleteUserAccount(String userExternalIdentifier, String accountExternalIdentifier);
}
