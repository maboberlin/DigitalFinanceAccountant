package de.bitsandbooks.finance.connectors.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class RoundHelper {

  private static final DecimalFormat DF = new DecimalFormat("#.##");

  static {
    DF.setRoundingMode(RoundingMode.HALF_UP);
  }

  public static Double roundUpHalfDouble(Double value) {
    return value == null ? null : new BigDecimal(DF.format(value)).doubleValue();
  }

  public static BigDecimal roundUpHalfBigDecimalDouble(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
