package de.bitsandbooks.finance.controllers.impl;

import de.bitsandbooks.finance.controllers.AuthenticationControllerInterface;
import de.bitsandbooks.finance.model.dtos.AuthenticationRequestDto;
import de.bitsandbooks.finance.model.entities.UserEntity;
import de.bitsandbooks.finance.security.CustomReactiveAuthenticationManager;
import de.bitsandbooks.finance.services.UserService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Validated
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
        .getAuthentication(authRequest.getEmail(), authRequest.getPassword())
        .map(
            jwtResponse -> {
              HttpHeaders httpHeaders = new HttpHeaders();
              httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponse.getAccessToken());
              return new ResponseEntity<>(jwtResponse, httpHeaders, HttpStatus.OK);
            });
  }

  @GetMapping("/refresh")
  public Mono<ResponseEntity> refresh(Mono<Principal> principal) {
    return principal
        .flatMap(
            principal1 -> this.authenticationManager.updateAuthentication(principal1.getName()))
        .map(
            jwtResponse -> {
              HttpHeaders httpHeaders = new HttpHeaders();
              httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponse.getAccessToken());
              return new ResponseEntity<>(jwtResponse, httpHeaders, HttpStatus.OK);
            });
  }

  @PostMapping("/signUp")
  public Mono<ResponseEntity> signUp(@Valid @RequestBody UserEntity userEntity) {
    return Mono.fromCallable(() -> userService.createUser(userEntity))
        .subscribeOn(Schedulers.boundedElastic())
        .map(ResponseEntity::ok);
  }
}
