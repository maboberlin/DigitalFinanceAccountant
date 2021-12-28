
package de.bitsandbooks.finance.connectors.yahoo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class Quote {

  private List<Double> close;

  private List<Double> high;

  private List<Double> low;

  private List<Double> open;

  private List<Long> volume;
}
