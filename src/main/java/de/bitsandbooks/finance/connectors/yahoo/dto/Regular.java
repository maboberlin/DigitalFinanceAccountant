
package de.bitsandbooks.finance.connectors.yahoo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class Regular {

  private Long end;

  private Long gmtoffset;

  private Long start;

  private String timezone;
}
