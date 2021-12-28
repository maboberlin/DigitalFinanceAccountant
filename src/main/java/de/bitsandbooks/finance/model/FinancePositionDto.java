package de.bitsandbooks.finance.model;

import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FinancePositionDto {

  @NotEmpty String identifier;

  @NotEmpty String name;

  @NotNull BigDecimal amount;
}
