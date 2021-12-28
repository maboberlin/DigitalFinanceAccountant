package de.bitsandbooks.finance.connectors.alphavantage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlphaVantageRequestDelayServiceTest {

  private int DELAY_MS = 1000;

  @Spy
  private AlphaVantageRequestDelayService alphaVantageRequestDelayService =
      new AlphaVantageRequestDelayService(DELAY_MS);

  @Test
  public void testAll() throws InterruptedException {
    long a1 = alphaVantageRequestDelayService.getDelayRequests();
    TimeUnit.MILLISECONDS.sleep(1000);
    long a2 = alphaVantageRequestDelayService.getDelayRequests();
    long a3 = alphaVantageRequestDelayService.getDelayRequests();
    TimeUnit.MILLISECONDS.sleep(500);
    long a4 = alphaVantageRequestDelayService.getDelayRequests();
    TimeUnit.MILLISECONDS.sleep(900);
    long a5 = alphaVantageRequestDelayService.getDelayRequests();

    assertThat(a1).isEqualTo(0);
    assertThat(a2).isEqualTo(0);
    assertThat(a3).isBetween(900L, 1000L);
    assertThat(a4).isBetween(0L, 500L);
    assertThat(a5).isBetween(0L, 100L);
  }
}
