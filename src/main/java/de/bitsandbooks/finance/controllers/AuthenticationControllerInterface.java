package de.bitsandbooks.finance.controllers;

import de.bitsandbooks.finance.model.dtos.AuthenticationRequestDto;
import de.bitsandbooks.finance.model.entities.UserEntity;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthenticationControllerInterface {

  Mono<ResponseEntity> signIn(@Valid AuthenticationRequestDto authRequest);

  Mono<ResponseEntity> signUp(@Valid UserEntity authRequest);
}
