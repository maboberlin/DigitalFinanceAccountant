package de.bitsandbooks.finance.security;

import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public class CustomReactiveAuthenticationManager
    extends UserDetailsRepositoryReactiveAuthenticationManager {

  private AuthTokenProvider authTokenProvider;

  public CustomReactiveAuthenticationManager(
      ReactiveUserDetailsService userDetailsService, AuthTokenProvider authTokenProvider) {
    super(userDetailsService);
    this.authTokenProvider = authTokenProvider;
  }

  public Mono<String> getJwtByAuthentication(String username, String password) {
    return this.authenticate(new UsernamePasswordAuthenticationToken(username, password))
        .map(this.authTokenProvider::createToken);
  }
}
