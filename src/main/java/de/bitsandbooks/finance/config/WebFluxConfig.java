package de.bitsandbooks.finance.config;

import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFluxConfig {

  @Bean
  public WebFluxProperties webFluxProperties() {
    return new WebFluxProperties();
  }
}
