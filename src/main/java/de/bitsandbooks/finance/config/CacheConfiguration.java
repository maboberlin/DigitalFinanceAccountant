package de.bitsandbooks.finance.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@ConditionalOnProperty(name = "dfa.cache.enabled", havingValue = "true")
@EnableCaching
@Configuration
@Slf4j
@RequiredArgsConstructor
public class CacheConfiguration {

  public static final String CURRENCY_EXCHANGE_CACHE = "currencyExchange";
  public static final String QUOTE_CACHE = "quoteCache";
  public static final String CONNECTOR_CACHE = "connectorCache";

  @NonNull private CacheManager cacheManager;

  @SuppressWarnings("ConstantConditions")
  public void evictCache(String name) {
    cacheManager.getCache(name).clear();
  }

  @Scheduled(cron = "${dfa.cache.currency.eviciton}")
  public void evictCurrencyCaches() {
    log.info("Evicting all currency caches");
    evictCache(CURRENCY_EXCHANGE_CACHE);
  }

  @Scheduled(cron = "${dfa.cache.quote.eviciton}")
  public void evictQuoteCaches() {
    log.info("Evicting all position caches");
    evictCache(QUOTE_CACHE);
  }

  @Scheduled(cron = "${dfa.cache.connector.eviciton}")
  public void evictConnectorCaches() {
    log.info("Evicting all connector caches");
    evictCache(CONNECTOR_CACHE);
  }
}
