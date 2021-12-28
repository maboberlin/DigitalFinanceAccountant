
package de.bitsandbooks.finance.connectors.yahoo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class Chart {

  private Object error;

  private List<Result> result;
}
