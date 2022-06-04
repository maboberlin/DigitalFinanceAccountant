package de.bitsandbooks.finance.controllers.helpers;

import de.bitsandbooks.finance.exceptions.ConnectorException;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.exceptions.MoreThanOneEntityFoundException;
import de.bitsandbooks.finance.exceptions.PositionsNotExistingException;
import de.bitsandbooks.finance.model.ErrorMessage;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(-2)
@RestControllerAdvice
public class ErrorResponseMessageService {

  @Value("${spring.application.name}")
  private String appName;

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
    String msg = String.format("%s not found. %s", ex.getTypeName(), ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MoreThanOneEntityFoundException.class)
  public ResponseEntity<ErrorMessage> handleMoreThanOneEntityFoundException(
      MoreThanOneEntityFoundException ex) {
    String msg = String.format("%s more than one found. %s", ex.getTypeName(), ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ConnectorException.class)
  public ResponseEntity<ErrorMessage> handleConnectorException(ConnectorException ex) {
    String msg =
        String.format(
            "Exception occurred while connecting to external source with connector '%s': %s",
            ex.getConnectorType(), ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(PositionsNotExistingException.class)
  public ResponseEntity<ErrorMessage> handlePositionsNotExistingException(
      PositionsNotExistingException ex) {
    String msg =
        String.format(
            "Exception occurred because of position could not be found by any connector: %s",
            ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    ResponseEntity<ErrorMessage> errorMessageResponseEntity =
        buildErrorResponse(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorMessageResponseEntity.getBody(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex) {
    String msg = ex.getMessage();
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorMessage> buildErrorResponse(String msg, HttpStatus status) {
    ErrorMessage errorMessage =
        ErrorMessage.builder()
            .error(status.getReasonPhrase())
            .message(msg)
            .status(status.value())
            .timestamp(OffsetDateTime.now())
            .service(appName)
            .build();
    return new ResponseEntity<>(errorMessage, status);
  }
}
