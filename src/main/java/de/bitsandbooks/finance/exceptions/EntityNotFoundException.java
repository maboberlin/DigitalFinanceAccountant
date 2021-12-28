package de.bitsandbooks.finance.exceptions;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {

  @Getter private String typeName;

  public EntityNotFoundException(String typeName, String msg) {
    super(msg);
    this.typeName = typeName;
  }
}
