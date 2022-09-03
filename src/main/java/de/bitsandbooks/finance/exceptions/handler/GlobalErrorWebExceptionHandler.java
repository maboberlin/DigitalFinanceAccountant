package de.bitsandbooks.finance.exceptions.handler;

import de.bitsandbooks.finance.model.dtos.ErrorMessageDto;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  @Value("${spring.application.name}")
  private String appName;

  public GlobalErrorWebExceptionHandler(
      GlobalErrorAttributes globalErrorAttributes,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(globalErrorAttributes, new WebProperties.Resources(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
    final Map<String, Object> errorPropertiesMap =
        getErrorAttributes(serverRequest, ErrorAttributeOptions.defaults());

    HttpStatus status = (HttpStatus) errorPropertiesMap.get(ErrorAttributesKey.STATUS.getKey());
    String message = (String) errorPropertiesMap.get(ErrorAttributesKey.MESSAGE.getKey());
    OffsetDateTime timestamp =
        (OffsetDateTime) errorPropertiesMap.get(ErrorAttributesKey.TIME.getKey());
    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(buildErrorMessageDto(status, message, timestamp)));
  }

  private ErrorMessageDto buildErrorMessageDto(
      HttpStatus status, String message, OffsetDateTime timestamp) {
    return ErrorMessageDto.builder()
        .error(status.getReasonPhrase())
        .message(message)
        .status(status.value())
        .timestamp(timestamp)
        .service(appName)
        .build();
  }
}
