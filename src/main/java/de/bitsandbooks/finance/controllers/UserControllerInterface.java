package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.UserEntity;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public interface UserControllerInterface {
  Void createUser(@Valid UserEntity userEntity);

  UserEntity getUser(@NotEmpty String id);
}
