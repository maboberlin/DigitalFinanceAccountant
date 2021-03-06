package de.bitsandbooks.finance.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValueWithTypeDto {
  ValueDto valueDto;
  PositionType positionType;
}
