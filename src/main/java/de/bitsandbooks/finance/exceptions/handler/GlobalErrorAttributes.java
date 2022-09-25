package de.bitsandbooks.finance.exceptions.handler;

import static java.lang.String.format;

import de.bitsandbooks.finance.exceptions.ConnectorException;
import de.bitsandbooks.finance.exceptions.EntityNotFoundException;
import de.bitsandbooks.finance.exceptions.MoreThanOneEntityFoundException;
import de.bitsandbooks.finance.exceptions.PositionsNotExistingException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

  private final List<ExceptionRule<? extends Throwable>> exceptionsRules =
      List.of(
          new ExceptionRule<>(
              EntityNotFoundException.class,
              HttpStatus.NOT_FOUND,
              (ex) -> format("%s not found. %s", ex.getTypeName(), ex.getMessage())),
          new ExceptionRule<>(
              MoreThanOneEntityFoundException.class,
              HttpStatus.CONFLICT,
              (ex) -> format("%s more than one found. %s", ex.getTypeName(), ex.getMessage())),
          new ExceptionRule<>(
              ConnectorException.class,
              HttpStatus.INTERNAL_SERVER_ERROR,
              (ex) ->
                  format(
                      "Exception occurred while connecting to external source with connector '%s': %s",
                      ex.getConnectorType(), ex.getMessage())),
          new ExceptionRule<>(
              PositionsNotExistingException.class,
              HttpStatus.BAD_REQUEST,
              (ex) ->
                  format(
                      "Exception occurred because of position could not be found by any connector: %s",
                      ex.getMessage())),
          new ExceptionRule<>(
              MethodArgumentNotValidException.class,
              HttpStatus.BAD_REQUEST,
              MethodArgumentNotValidException::getMessage));

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {
    Throwable error = getError(request);

    final OffsetDateTime timestamp = OffsetDateTime.now();
    Optional<ExceptionRule<? extends Throwable>> exceptionRuleOptional =
        exceptionsRules
            .stream()
            .filter(Objects::nonNull)
            .filter(
                exceptionRule ->
                    exceptionRule.getExceptionClass().isAssignableFrom(error.getClass()))
            .findFirst();

    Map<String, Object> stringObjectMap =
        exceptionRuleOptional
            .<Map<String, Object>>map(
                exceptionRule ->
                    Map.of(
                        ErrorAttributesKey.STATUS.getKey(),
                        exceptionRule.getHttpStatus(),
                        ErrorAttributesKey.MESSAGE.getKey(),
                        ((ExceptionRule<Throwable>) exceptionRule)
                            .getMessageProvider()
                            .apply(error),
                        ErrorAttributesKey.TIME.getKey(),
                        timestamp))
            .orElseGet(
                () ->
                    Map.of(
                        ErrorAttributesKey.STATUS.getKey(),
                        determineHttpStatus(error),
                        ErrorAttributesKey.MESSAGE.getKey(),
                        error.getMessage(),
                        ErrorAttributesKey.TIME.getKey(),
                        timestamp));

    log.error((String) stringObjectMap.get(ErrorAttributesKey.MESSAGE.getKey()), error);

    return stringObjectMap;
  }

  private HttpStatus determineHttpStatus(Throwable error) {
    if (ResponseStatusException.class.isAssignableFrom(error.getClass())) {
      return ((ResponseStatusException) error).getStatus();
    } else {
      return MergedAnnotations.from(
              error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
          .get(ResponseStatus.class)
          .getValue(ErrorAttributesKey.STATUS.getKey(), HttpStatus.class)
          .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
