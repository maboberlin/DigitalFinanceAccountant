package de.bitsandbooks.finance.model;

import lombok.Getter;

public enum Currency {
  EURO("EUR"),
  DOLLAR("USD"),
  CHF("CHF");

  @Getter private String identifier;

  Currency(String identifier) {
    this.identifier = identifier;
  }
}
