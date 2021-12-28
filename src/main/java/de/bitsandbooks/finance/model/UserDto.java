package de.bitsandbooks.finance.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  @NotEmpty private String foreName;

  @NotEmpty private String surName;

  @Email private String mailAddress;
}
