package de.bitsandbooks.finance.connectors.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphaVantageTimeSeriesDailyAdjustedDto implements AlphavantageDto {

  // TODO validate pojo

  @JsonProperty("Error Message")
  private String errorMessage;

  @JsonProperty("Meta Data")
  private MetaData metaData;

  @JsonProperty("Time Series (Daily)")
  private Map<String, DailyData> dailyDataMap;

  @Setter
  @Getter
  public static class MetaData {
    @JsonProperty("1. Information")
    public String _1Information;

    @JsonProperty("2. Symbol")
    public String _2Symbol;

    @JsonProperty("3. Last Refreshed")
    public String _3LastRefreshed;

    @JsonProperty("4. Output Size")
    public String _4OutputSize;

    @JsonProperty("5. Time Zone")
    public String _5TimeZone;
  }

  @Setter
  @Getter
  public static class DailyData {
    @JsonProperty("1. open")
    public String _1Open;

    @JsonProperty("2. high")
    public String _2High;

    @JsonProperty("3. low")
    public String _3Low;

    @JsonProperty("4. close")
    public String _4Close;

    @JsonProperty("5. adjusted close")
    public String _5AdjustedClose;

    @JsonProperty("6. volume")
    public String _6Volume;
  }
}
