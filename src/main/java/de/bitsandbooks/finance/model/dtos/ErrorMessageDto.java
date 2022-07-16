package de.bitsandbooks.finance.model.dtos;

import java.time.OffsetDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorMessageDto {
  private OffsetDateTime timestamp;
  private Integer status;
  private String error;
  private String message;
  private String service;
  // TODO add path
}
