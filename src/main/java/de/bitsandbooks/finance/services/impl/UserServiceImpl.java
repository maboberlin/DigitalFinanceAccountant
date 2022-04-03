package de.bitsandbooks.finance.services.impl;

import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.model.UserAccountEntity;
import de.bitsandbooks.finance.repositories.UserAccountRepository;
import de.bitsandbooks.finance.services.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {

  private final UserAccountRepository userAccountRepository;

  @Override
  public UserAccountEntity createUser(UserAccountEntity userAccountEntity) {
    UserAccountEntity result = userAccountRepository.save(userAccountEntity);
    log.info("Created user entity with id '{}'", userAccountEntity.getId());
    return result;
  }

  @Override
  public UserAccountEntity getUser(String userExternalIdentifier) {
    return userAccountRepository
        .findByExternalIdentifier(userExternalIdentifier)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserAccountEntity.class.getName(),
                    String.format(
                        "Couldn't find user with externalIdentifier '%s'",
                        userExternalIdentifier)));
  }

  @Override
  public List<UserAccountEntity> getAllUsers() {
    List<UserAccountEntity> result = new ArrayList<>();
    userAccountRepository.findAll().forEach(result::add);
    return result;
  }

  @Override
  public UserAccountEntity getUser(String eMail, String accountIdentifier) {
    return userAccountRepository
        .findByMailAddressAndAccountIdentifier(eMail, accountIdentifier)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    UserAccountEntity.class.getName(),
                    String.format(
                        "Couldn't find user with eMail '%s' and accountIdentifier '%s'",
                        eMail, accountIdentifier)));
  }
}
