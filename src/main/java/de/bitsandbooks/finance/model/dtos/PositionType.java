package de.bitsandbooks.finance.model.dtos;

import lombok.Getter;

public enum PositionType {
  STOCK(true),
  RESOURCE(true),
  CURRENCY(false),
  KRYPTO(true),
  BOND(false),
  REAL_ESTATE(false);

  @Getter private boolean resolve;

  PositionType(boolean resolve) {
    this.resolve = resolve;
  }
}
