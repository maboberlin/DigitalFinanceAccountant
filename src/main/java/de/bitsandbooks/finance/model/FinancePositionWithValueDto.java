package de.bitsandbooks.finance.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FinancePositionWithValueDto {
  String identifier;
  BigDecimal amount;
  ValueDto valueDto;
}
