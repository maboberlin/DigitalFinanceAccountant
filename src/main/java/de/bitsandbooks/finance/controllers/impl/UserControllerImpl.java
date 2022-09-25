package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.UserControllerInterface;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.services.UserService;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/user")
@RestController
public class UserControllerImpl implements UserControllerInterface {

  @NonNull private final UserService userService;

  @Override
  @PreAuthorize("hasPermission(#userExternalIdentifier, 'USER_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/{userExternalIdentifier}/accounts",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<UserAccountEntity> createUserAccount(
      @NotEmpty @PathVariable(name = "userExternalIdentifier") String userExternalIdentifier,
      @Valid @RequestBody UserAccountEntity userAccountEntity) {
    log.info("Creating user account");
    return Mono.fromCallable(
            () -> userService.createUserAccount(userExternalIdentifier, userAccountEntity))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userExternalIdentifier, 'USER_IDENTIFIER', 'read') or hasRole('ROLE_ADMIN')")
  @RequestMapping(
    value = "/{userExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public Mono<UserEntity> getUser(
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier) {
    log.info("Find user with userExternalIdentifier '{}'", userExternalIdentifier);
    return Mono.fromCallable(() -> userService.getUserByExternalIdentifier(userExternalIdentifier))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
  public Flux<UserEntity> getAllUsers() {
    log.info("Get all users");
    return Mono.fromCallable(userService::getAllUsers)
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany(Flux::fromIterable);
  }

  @Override
  @PreAuthorize("#eMail == authentication.principal.username or hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/find", method = RequestMethod.GET, produces = "application/json")
  public Mono<UserEntity> findUser(
      @NotEmpty @RequestParam("eMail") String eMail,
      @NotEmpty @RequestParam("accountIdentifier") String accountIdentifier) {
    log.info("Find user with eMail '{}' and accountIdentifier '{}'", eMail, accountIdentifier);
    return Mono.fromCallable(() -> userService.getUser(eMail, accountIdentifier))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  @PreAuthorize(
      "hasPermission(#userExternalIdentifier, 'USER_IDENTIFIER', 'read') or hasRole('ROLE_ADMIN')")
  @RequestMapping(
    value = "/{userExternalIdentifier}/accounts",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public Flux<UserAccountEntity> getAccountsForUser(
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier) {
    log.info("Get all accounts for user with identifier: '{}'", userExternalIdentifier);
    return Mono.fromCallable(() -> userService.getUserAccounts(userExternalIdentifier))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany(Flux::fromIterable);
  }

  @Override
  @PreAuthorize("hasPermission(#userExternalIdentifier, 'USER_IDENTIFIER', 'write')")
  @RequestMapping(
    value = "/{userExternalIdentifier}/accounts/{accountExternalIdentifier}",
    method = RequestMethod.DELETE
  )
  public Mono<Void> deleteAccountForUser(
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier,
      @NotEmpty @PathVariable("accountExternalIdentifier") String accountExternalIdentifier) {
    log.info(
        "Delete account '{}' for user with identifier: '{}'",
        accountExternalIdentifier,
        userExternalIdentifier);
    return Mono.fromCallable(
            () -> userService.deleteUserAccount(userExternalIdentifier, accountExternalIdentifier))
        .subscribeOn(Schedulers.boundedElastic());
  }
}
