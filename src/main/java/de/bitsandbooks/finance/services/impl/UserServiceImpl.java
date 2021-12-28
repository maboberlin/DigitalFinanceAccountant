package de.bitsandbooks.finance.services.impl;

import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.UserEntity;
import de.bitsandbooks.finance.repositories.UserRepository;
import de.bitsandbooks.finance.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public void createUser(UserEntity userEntity) {
    userRepository.save(userEntity);
    log.info("Created user entity with id '{}'", userEntity.getId());
  }

  @Override
  public UserEntity getUser(String mailAddress) {
    return userRepository
        .findByMailAddress(mailAddress)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserEntity.class.getName(),
                    String.format("Couldn't find user with eMail '%s'", mailAddress)));
  }
}
