package de.bitsandbooks.finance.services;

import de.bitsandbooks.finance.model.UserEntity;

public interface UserService {
  void createUser(UserEntity userEntity);

  UserEntity getUser(String id);
}
