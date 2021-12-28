package de.bitsandbooks.finance.exceptions;

public class ApiException extends RuntimeException {

  public ApiException(String msg) {
    super(msg);
  }
}
