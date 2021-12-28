package de.bitsandbooks.finance.exceptions;

import lombok.Getter;

public class MoreThanOneEntityFoundException extends RuntimeException {

  @Getter private String typeName;

  public MoreThanOneEntityFoundException(String typeName, String msg) {
    super(msg);
    this.typeName = typeName;
  }
}
