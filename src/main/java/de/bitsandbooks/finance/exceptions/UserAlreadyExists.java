package de.bitsandbooks.finance.exceptions;

public class UserAlreadyExists extends RuntimeException {
  public UserAlreadyExists(String msg) {
    super(msg);
  }
}
