package de.bitsandbooks.finance.security;

import de.bitsandbooks.finance.model.dtos.JWTResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public class CustomReactiveAuthenticationManager
    extends UserDetailsRepositoryReactiveAuthenticationManager {

  private AuthTokenProvider authTokenProvider;

  private ReactiveUserDetailsService reactiveUserDetailsService;

  public CustomReactiveAuthenticationManager(
      ReactiveUserDetailsService userDetailsService, AuthTokenProvider authTokenProvider) {
    super(userDetailsService);
    this.authTokenProvider = authTokenProvider;
    this.reactiveUserDetailsService = userDetailsService;
  }

  public Mono<JWTResponse> getAuthentication(String username, String password) {
    return this.authenticate(new UsernamePasswordAuthenticationToken(username, password))
        .map(
            authentication -> {
              String token = this.authTokenProvider.createToken(authentication);
              UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
              List<String> authorities =
                  userDetails
                      .getAuthorities()
                      .stream()
                      .map(GrantedAuthority::getAuthority)
                      .collect(Collectors.toList());
              return new JWTResponse(
                  token,
                  userDetails.getExternalIdentifier(),
                  userDetails.getForename(),
                  userDetails.getSurname(),
                  userDetails.getEmail(),
                  authorities);
            });
  }

  public Mono<JWTResponse> updateAuthentication(String name) {
    return this.reactiveUserDetailsService
        .findByUsername(name)
        .map(
            userDetails -> {
              if (!UserDetailsImpl.class.isAssignableFrom(userDetails.getClass())) {
                throw new IllegalArgumentException("UserDetails is not of type 'UserDetailsImpl'");
              }
              UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
              String token = this.authTokenProvider.createToken((UserDetailsImpl) userDetails);
              List<String> authorities =
                  userDetailsImpl
                      .getAuthorities()
                      .stream()
                      .map(GrantedAuthority::getAuthority)
                      .collect(Collectors.toList());
              return new JWTResponse(
                  token,
                  userDetailsImpl.getExternalIdentifier(),
                  userDetailsImpl.getForename(),
                  userDetailsImpl.getSurname(),
                  userDetailsImpl.getEmail(),
                  authorities);
            });
  }
}
