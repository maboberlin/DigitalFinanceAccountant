package de.bitsandbooks.finance.connectors.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphaVantageCurrencyDto implements AlphavantageDto {

  @JsonProperty("Error Message")
  private String errorMessage;

  @NotNull
  @JsonProperty("Realtime Currency Exchange Rate")
  private CurrencyData currencyData;

  @Setter
  @Getter
  public static class CurrencyData {

    @JsonProperty("1. From_Currency Code")
    private String _1FromCurrency;

    @JsonProperty("2. From_Currency Name")
    private String _2FromCurrencyName;

    @JsonProperty("3. To_Currency Code")
    private String _3ToCurrency;

    @JsonProperty("4. To_Currency Name")
    private String _4ToCurrencyName;

    @NotEmpty
    @JsonProperty("5. Exchange Rate")
    private BigDecimal _5ExchangeRate;

    @JsonProperty("6. Last Refreshed")
    private String _6LastRefreshed;

    @JsonProperty("7. Time Zone")
    private String _7TimeZone;

    @JsonProperty("8. Bid Price")
    private BigDecimal _8BidPrice;

    @JsonProperty("9. Ask Price")
    private BigDecimal _9AskPrice;
  }
}
