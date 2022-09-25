package de.bitsandbooks.finance.security;

import de.bitsandbooks.finance.model.entities.UserAccountEntity;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.services.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

  @Autowired private UserService userService;

  @Override
  public Mono<UserDetails> findByUsername(String emailAddress) {
    return Mono.fromCallable(() -> userService.getUserByEmailAddress(emailAddress))
        .subscribeOn(Schedulers.boundedElastic())
        .map(this::mapToUserDetails);
  }

  private UserDetails mapToUserDetails(UserEntity userEntity) {
    Set<String> accountExternalIdentifierSet =
        userEntity
            .getUserAccountEntityList()
            .stream()
            .map(UserAccountEntity::getExternalIdentifier)
            .collect(Collectors.toSet());
    return UserDetailsImpl.create(
        userEntity.getExternalIdentifier(),
        userEntity.getMailAddress(),
        userEntity.getForeName(),
        userEntity.getSurName(),
        userEntity.getPassword(),
        accountExternalIdentifierSet,
        userEntity.getRoles());
  }
}
