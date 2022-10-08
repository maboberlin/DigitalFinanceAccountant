package de.bitsandbooks.finance.model.dtos;

import de.bitsandbooks.finance.connectors.helpers.RoundHelper;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValueDto {
  String currency;
  BigDecimal value;

  public BigDecimal getValue() {
    return RoundHelper.roundUpHalfBigDecimalDouble(this.value);
  }

  public BigDecimal getValueUnrounded() {
    return this.value;
  }
}
