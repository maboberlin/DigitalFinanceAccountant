package de.bitsandbooks.finance.model.dtos;

import de.bitsandbooks.finance.connectors.helpers.RoundHelper;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
  BigDecimal price;

  public BigDecimal getPrice() {
    return RoundHelper.roundUpHalfBigDecimalDouble(this.price);
  }

  public BigDecimal getPriceNotRounded() {
    return this.price;
  }
}
