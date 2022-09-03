package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.AuthenticationControllerInterface;
import de.bitsandbooks.finance.model.dtos.AuthenticationRequestDto;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.security.CustomReactiveAuthenticationManager;
import de.bitsandbooks.finance.services.UserService;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationControllerInterface {

  private final CustomReactiveAuthenticationManager authenticationManager;

  private final UserService userService;

  @PostMapping("/signIn")
  public Mono<ResponseEntity> signIn(@Valid @RequestBody AuthenticationRequestDto authRequest) {
    return this.authenticationManager
        .getJwtByAuthentication(authRequest.getEmail(), authRequest.getPassword())
        .map(
            jwt -> {
              HttpHeaders httpHeaders = new HttpHeaders();
              httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
              Map<String, String> accessToken = Map.of("accessToken", jwt);
              return new ResponseEntity<>(accessToken, httpHeaders, HttpStatus.OK);
            });
  }

  @PostMapping("/signUp")
  public Mono<ResponseEntity> signUp(@Valid @RequestBody UserEntity userEntity) {
    return Mono.fromCallable(() -> userService.createUser(userEntity))
        .subscribeOn(Schedulers.boundedElastic())
        .map(ResponseEntity::ok);
  }
}
