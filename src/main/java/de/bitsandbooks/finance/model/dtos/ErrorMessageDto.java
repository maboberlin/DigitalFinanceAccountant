package de.bitsandbooks.finance.model.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorMessageDto {
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSx")
  private OffsetDateTime timestamp;

  private Integer status;
  private String error;
  private String message;
  private String service;
  // TODO add path
}
