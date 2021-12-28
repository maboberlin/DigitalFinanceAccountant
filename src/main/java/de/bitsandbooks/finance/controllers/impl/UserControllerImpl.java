package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.UserControllerInterface;
import de.bitsandbooks.finance.model.UserEntity;
import de.bitsandbooks.finance.services.UserService;
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
@RequestMapping("user")
@RestController
public class UserControllerImpl implements UserControllerInterface {

  @NonNull private final UserService userService;

  @Override
  @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Void createUser(@Valid @RequestBody UserEntity userEntity) {
    log.info("Creating user");
    userService.createUser(userEntity);
    return null;
  }

  @Override
  @RequestMapping(value = "/{userEMail}", method = RequestMethod.GET, produces = "application/json")
  public UserEntity getUser(@NotEmpty @PathVariable("userEMail") String userEMail) {
    log.info("Find user with id '{}'", userEMail);
    return userService.getUser(userEMail);
  }
}
