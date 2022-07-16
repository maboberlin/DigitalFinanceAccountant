package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.AuthenticationControllerInterface;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.exceptions.UserAlreadyExists;
import de.bitsandbooks.finance.model.dtos.AuthenticationRequestDto;
import de.bitsandbooks.finance.model.entities.Role;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.security.AuthTokenProvider;
import de.bitsandbooks.finance.services.UserService;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationControllerInterface {

  private final AuthTokenProvider authTokenProvider;

  private final ReactiveAuthenticationManager authenticationManager;

  private final UserService userService;

  @PostMapping("/signIn")
  public Mono<ResponseEntity> signIn(
      @Valid @RequestBody Mono<AuthenticationRequestDto> authRequest) {
    return authRequest
        .flatMap(
            login ->
                this.authenticationManager
                    .authenticate(
                        new UsernamePasswordAuthenticationToken(
                            login.getEmail(), login.getPassword()))
                    .map(this.authTokenProvider::createToken))
        .map(
            jwt -> {
              HttpHeaders httpHeaders = new HttpHeaders();
              httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
              var tokenBody = Map.of("accessToken", jwt);
              return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
            });
  }

  @PostMapping("/signUp")
  public Mono<ResponseEntity> signUp(@Valid @RequestBody UserEntity userEntity) {
    try {
      String eMail = userEntity.getMailAddress();
      userService.getUserByEmailAddress(eMail);
      String msg = String.format("User with id '%s' already exists", userEntity.getMailAddress());
      throw new UserAlreadyExists(msg);
    } catch (EntityNotFoundException e) {
      log.info("Creating user with id '{}'", userEntity.getMailAddress());
      userEntity.setRoles(Set.of(Role.ROLE_USER));
      UserEntity user = userService.createUser(userEntity);
      return Mono.just(ResponseEntity.ok(user));
    }
  }
}
