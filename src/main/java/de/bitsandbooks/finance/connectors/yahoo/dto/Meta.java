
package de.bitsandbooks.finance.connectors.yahoo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class Meta {

  private Double chartPreviousClose;

  private String currency;

  private CurrentTradingPeriod currentTradingPeriod;

  private String dataGranularity;

  private String exchangeName;

  private String exchangeTimezoneName;

  private Long firstTradeDate;

  private Long gmtoffset;

  private String instrumentType;

  private Double previousClose;

  private Long priceHint;

  private String range;

  private Double regularMarketPrice;

  private Long regularMarketTime;

  private Long scale;

  private String symbol;

  private String timezone;

  private List<List<TradingPeriod>> tradingPeriods;

  private List<String> validRanges;
}
