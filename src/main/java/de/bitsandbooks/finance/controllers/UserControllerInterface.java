package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public interface UserControllerInterface {
  UserAccountEntity createUserAccount(
      @NotEmpty String userExternalIdentifier, @Valid UserAccountEntity userEntity);

  UserEntity getUser(@NotEmpty String userExternalIdentifier);

  List<UserEntity> getAllUsers();

  UserEntity findUser(@NotEmpty String eMail, @NotEmpty String accountIdentifier);
}
