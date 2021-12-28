package de.bitsandbooks.finance.connectors.alphavantage;

import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlphaVantageRequestDelayService {

  private final int delayRequests;

  private final AtomicLong lastAccessTime;

  public AlphaVantageRequestDelayService(
      @Value("${dfa.alphavantage.delay-requests}") int delayRequests) {
    this.delayRequests = delayRequests;
    lastAccessTime = new AtomicLong(0L);
  }

  public long getDelayRequests() {
    // TODO work with count latch
    long currentTime = System.currentTimeMillis();
    long diffSinceLastAccess = currentTime - lastAccessTime.getAndSet(currentTime);
    long delay = Math.max(delayRequests - diffSinceLastAccess, 0L);
    log.info("Delay alphavantage request by: '{}'", delay);
    return delay;
  }

  public void resetTimer() {
    lastAccessTime.set(System.currentTimeMillis());
  }
}
