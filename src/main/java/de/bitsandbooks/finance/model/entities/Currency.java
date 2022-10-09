package de.bitsandbooks.finance.model.entities;

public enum Currency {
  USD,
  EUR,
  GBP,
  AUD,
  NZD,
  JPY,
  CAD,
  SEK,
  CHF,
  HUF,
  CNY,
  HKD,
  SGD,
  INR,
  MXN,
  PHP,
  IDR,
  THB,
  MYR,
  ZAR,
  RUB;

  public static boolean isCurrency(String identifier) {
    try {
      Currency.valueOf(identifier);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
