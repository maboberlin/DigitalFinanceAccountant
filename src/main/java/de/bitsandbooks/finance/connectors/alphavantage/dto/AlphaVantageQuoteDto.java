package de.bitsandbooks.finance.connectors.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphaVantageQuoteDto implements AlphavantageDto {

  // TODO validate pojo

  @JsonProperty("Error Message")
  private String errorMessage;

  @JsonProperty("Global Quote")
  private GlobalQuote globalQuote;

  @Setter
  @Getter
  public static class GlobalQuote {

    @JsonProperty("01. symbol")
    private String _01Symbol;

    @JsonProperty("02. open")
    private String _02Open;

    @JsonProperty("03. high")
    private String _03High;

    @JsonProperty("04. low")
    private String _04Low;

    @JsonProperty("05. price")
    private String _05Price;

    @JsonProperty("06. volume")
    private String _06Volume;

    @JsonProperty("07. latest trading day")
    private String _07LatestTradingDay;

    @JsonProperty("08. previous close")
    private String _08PreviousClose;

    @JsonProperty("09. change")
    private String _09Change;

    @JsonProperty("10. change percent")
    private String _10ChangePercent;
  }
}
