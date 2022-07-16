package de.bitsandbooks.finance.controllers.impl;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/user")
@RestController
public class UserControllerImpl implements UserControllerInterface {

  @NonNull private final UserService userService;

  @Override
  @RequestMapping(
    value = "/{userIdentifier}/account",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  @ResponseStatus(HttpStatus.CREATED)
  public UserAccountEntity createUserAccount(
      @NotEmpty @PathVariable(name = "userIdentifier") String userExternalIdentifier,
      @Valid @RequestBody UserAccountEntity userAccountEntity) {
    log.info("Creating user account");
    return userService.createUserAccount(userExternalIdentifier, userAccountEntity);
  }

  @Override
  @RequestMapping(
    value = "/{userExternalIdentifier}",
    method = RequestMethod.GET,
    produces = "application/json"
  )
  public UserEntity getUser(
      @NotEmpty @PathVariable("userExternalIdentifier") String userExternalIdentifier) {
    log.info("Find user with userExternalIdentifier '{}'", userExternalIdentifier);
    return userService.getUserByExternalIdentifier(userExternalIdentifier);
  }

  @Override
  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
  public List<UserEntity> getAllUsers() {
    log.info("Get all users");
    return userService.getAllUsers();
  }

  @Override
  @RequestMapping(value = "/find", method = RequestMethod.GET, produces = "application/json")
  public UserEntity findUser(
      @NotEmpty @RequestParam("eMail") String eMail,
      @NotEmpty @RequestParam("accountIdentifier") String accountIdentifier) {
    log.info("Find user with eMail '{}' and accountIdentifier '{}'", eMail, accountIdentifier);
    return userService.getUser(eMail, accountIdentifier);
  }
}
