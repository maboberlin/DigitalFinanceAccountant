package de.bitsandbooks.finance.security;

import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.services.UserService;
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
        .map(this::mapToUserDetails)
        .subscribeOn(Schedulers.boundedElastic());
  }

  private UserDetails mapToUserDetails(UserEntity userEntity) {
    return UserDetailsImpl.create(
        userEntity.getId(),
        userEntity.getMailAddress(),
        userEntity.getPassword(),
        userEntity.getRoles());
  }
}
