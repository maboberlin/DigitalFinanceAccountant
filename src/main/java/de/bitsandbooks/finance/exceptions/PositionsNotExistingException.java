package de.bitsandbooks.finance.exceptions;

public class PositionsNotExistingException extends RuntimeException {
  public PositionsNotExistingException(String msg) {
    super(msg);
  }
}
