package de.bitsandbooks.finance.controllers.impl;

import static reactor.core.publisher.Mono.just;

import de.bitsandbooks.finance.controllers.UserControllerInterface;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.services.UserService;
import java.util.List;
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
    value = "/{userExternalIdentifier}/account",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<UserAccountEntity> createUserAccount(
      @NotEmpty @PathVariable(name = "userExternalIdentifier") String userExternalIdentifier,
      @Valid @RequestBody UserAccountEntity userAccountEntity) {
    log.info("Creating user account");
    UserAccountEntity userAccount =
        userService.createUserAccount(userExternalIdentifier, userAccountEntity);
    return just(userAccount);
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
    UserEntity userByExternalIdentifier =
        userService.getUserByExternalIdentifier(userExternalIdentifier);
    return just(userByExternalIdentifier);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
  public Flux<UserEntity> getAllUsers() {
    log.info("Get all users");
    List<UserEntity> allUsers = userService.getAllUsers();
    return Flux.fromIterable(allUsers);
  }

  @Override
  @PreAuthorize("#eMail == authentication.principal.username or hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/find", method = RequestMethod.GET, produces = "application/json")
  public Mono<UserEntity> findUser(
      @NotEmpty @RequestParam("eMail") String eMail,
      @NotEmpty @RequestParam("accountIdentifier") String accountIdentifier) {
    log.info("Find user with eMail '{}' and accountIdentifier '{}'", eMail, accountIdentifier);
    UserEntity user = userService.getUser(eMail, accountIdentifier);
    return Mono.just(user);
  }
}
