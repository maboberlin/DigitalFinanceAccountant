package de.bitsandbooks.finance.controllers.helpers;

import de.bitsandbooks.finance.exceptions.ConnectorException;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.exceptions.MoreThanOneEntityFoundException;
import de.bitsandbooks.finance.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ErrorResponseMessageService extends ResponseEntityExceptionHandler {

  @Value("${spring.application.name}")
  private String appName;

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleEntityNotFoundException(
      EntityNotFoundException ex, WebRequest request) {
    String msg = String.format("%s not found. %s", ex.getTypeName(), ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MoreThanOneEntityFoundException.class)
  public ResponseEntity<ErrorMessage> handleMoreThanOneEntityFoundException(
      MoreThanOneEntityFoundException ex, WebRequest request) {
    String msg = String.format("%s more than one found. %s", ex.getTypeName(), ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ConnectorException.class)
  public ResponseEntity<ErrorMessage> handleConnectorException(
      ConnectorException ex, WebRequest request) {
    String msg =
        String.format(
            "Exception occurred while connecting to external source with connector '%s': %s",
            ex.getConnectorType(), ex.getMessage());
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex, WebRequest request) {
    String msg = ex.getMessage();
    log.error(msg, ex);
    return buildErrorResponse(msg, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ResponseEntity<ErrorMessage> errorMessageResponseEntity =
        buildErrorResponse(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorMessageResponseEntity.getBody(), status);
  }

  private ResponseEntity<ErrorMessage> buildErrorResponse(String msg, HttpStatus status) {
    String path =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest()
            .getRequestURI();
    ErrorMessage errorMessage =
        ErrorMessage.builder()
            .error(status.getReasonPhrase())
            .message(msg)
            .status(status.value())
            .timestamp(OffsetDateTime.now())
            .path(path)
            .service(appName)
            .build();
    return new ResponseEntity<>(errorMessage, status);
  }
}
