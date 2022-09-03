package de.bitsandbooks.finance.exceptions.handler;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ExceptionRule<T extends Throwable> {

  private Class<T> exceptionClass;

  private HttpStatus httpStatus;

  private Function<T, String> messageProvider;
}
