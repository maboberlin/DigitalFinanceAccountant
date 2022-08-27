package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserControllerInterface {
  Mono<UserAccountEntity> createUserAccount(
      @NotEmpty String userExternalIdentifier, @Valid UserAccountEntity userEntity);

  Mono<UserEntity> getUser(@NotEmpty String userExternalIdentifier);

  Flux<UserEntity> getAllUsers();

  Mono<UserEntity> findUser(@NotEmpty String eMail, @NotEmpty String accountIdentifier);
}
