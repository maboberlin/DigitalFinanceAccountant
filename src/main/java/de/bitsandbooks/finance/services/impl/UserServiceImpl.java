package de.bitsandbooks.finance.services.impl;

import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.exceptions.UserAlreadyExistsException;
import de.bitsandbooks.finance.model.entities.Role;
import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.repositories.UserRepository;
import de.bitsandbooks.finance.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final UserAccountRepository userAccountRepository;

  private final PasswordEncoder encoder;

  @Override
  public UserEntity createUser(UserEntity userEntity) {
    try {
      String eMail = userEntity.getMailAddress();
      this.getUserByEmailAddress(eMail);
      String msg = String.format("User with id '%s' already exists", userEntity.getMailAddress());
      throw new UserAlreadyExistsException(msg);
    } catch (EntityNotFoundException e) {
      log.info("Creating user with id '{}'", userEntity.getMailAddress());
      userEntity.setRoles(Set.of(Role.ROLE_USER));
      encodePassword(userEntity);
      UserEntity result = userRepository.save(userEntity);
      log.info("Created user entity with id '{}'", userEntity.getId());
      return result;
    }
  }

  @Override
  public UserAccountEntity createUserAccount(
      String userExternalIdentifier, UserAccountEntity userAccountEntity) {
    UserEntity userEntity = getUserByExternalIdentifier(userExternalIdentifier);
    userEntity.getUserAccountEntityList().add(userAccountEntity);
    userAccountEntity.setUser(userEntity);
    UserAccountEntity result = userAccountRepository.save(userAccountEntity);
    log.info("Created user account entity with id '{}'", userAccountEntity.getId());
    return result;
  }

  @Override
  public UserEntity getUserByExternalIdentifier(String userExternalIdentifier) {
    return userRepository
        .findByExternalIdentifier(userExternalIdentifier)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserEntity.class.getName(),
                    String.format(
                        "Couldn't find user with externalIdentifier '%s'",
                        userExternalIdentifier)));
  }

  @Override
  public UserEntity getUserByEmailAddress(String emailAddress) {
    return userRepository
        .findByMailAddress(emailAddress)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserEntity.class.getName(),
                    String.format("Couldn't find user with emailAddress '%s'", emailAddress)));
  }

  @Override
  public List<UserEntity> getAllUsers() {
    List<UserEntity> result = new ArrayList<>();
    userRepository.findAll().forEach(result::add);
    return result;
  }

  @Override
  public UserEntity getUser(String eMail, String accountIdentifier) {
    return userRepository
        .findByMailAddress(eMail)
        .map(
            userEntity -> {
              List<UserAccountEntity> accountEntityList =
                  userEntity
                      .getUserAccountEntityList()
                      .stream()
                      .filter(
                          userAccountEntity ->
                              userAccountEntity.getAccountIdentifier().equals(accountIdentifier))
                      .collect(Collectors.toList());
              if (accountEntityList.isEmpty()) {
                return null;
              } else {
                userEntity.setUserAccountEntityList(accountEntityList);
                return userEntity;
              }
            })
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserEntity.class.getName(),
                    String.format(
                        "Couldn't find user with eMail '%s' and accountIdentifier '%s'",
                        eMail, accountIdentifier)));
  }

  private void encodePassword(UserEntity userEntity) {
    String password = userEntity.getPassword();
    String passwordEncode = encoder.encode(password);
    userEntity.setPassword(passwordEncode);
  }
}
