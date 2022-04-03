package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.UserAccountEntity;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public interface UserControllerInterface {
  UserAccountEntity createUser(@Valid UserAccountEntity userAccountEntity);

  UserAccountEntity getUser(@NotEmpty String userExternalIdentifier);

  List<UserAccountEntity> getAllUsers();

  UserAccountEntity findUser(@NotEmpty String eMail, @NotEmpty String accountIdentifier);
}
