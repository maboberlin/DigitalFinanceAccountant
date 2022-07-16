package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.dtos.AuthenticationRequestDto;
import de.bitsandbooks.finance.model.entities.UserEntity;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthenticationControllerInterface {

  Mono<ResponseEntity> signIn(Mono<AuthenticationRequestDto> authRequest);

  Mono<ResponseEntity> signUp(UserEntity authRequest);
}
