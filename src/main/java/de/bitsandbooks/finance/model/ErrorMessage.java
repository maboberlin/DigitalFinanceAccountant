package de.bitsandbooks.finance.model;

import java.time.OffsetDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorMessage {
  private OffsetDateTime timestamp;
  private Integer status;
  private String error;
  private String message;
  private String service;
  // TODO add path
}
