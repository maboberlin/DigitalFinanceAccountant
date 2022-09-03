package de.bitsandbooks.finance.exceptions.handler;

import lombok.Getter;

@Getter
public enum ErrorAttributesKey {
  STATUS("status"),
  MESSAGE("message"),
  TIME("timestamp");

  private final String key;

  ErrorAttributesKey(String key) {
    this.key = key;
  }
}
