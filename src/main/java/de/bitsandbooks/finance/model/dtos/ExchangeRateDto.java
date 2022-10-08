package de.bitsandbooks.finance.model.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
  String currency;
  BigDecimal exchangeRate;

  public BigDecimal getExchangeRate() {
    return this.exchangeRate;
  }
}
