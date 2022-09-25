package de.bitsandbooks.finance.model.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JWTResponse {
  private String accessToken;
  private String id;
  private String forename;
  private String surname;
  private String email;
  private List<String> roles;
}
