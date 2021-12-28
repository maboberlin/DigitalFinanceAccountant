package de.bitsandbooks.finance.model;

import de.bitsandbooks.finance.connectors.helpers.RoundHelper;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValueDto {
  Currency currency;
  BigDecimal value;

  public BigDecimal getValue() {
    return RoundHelper.roundUpHalfBigDecimalDouble(this.value);
  }
}
