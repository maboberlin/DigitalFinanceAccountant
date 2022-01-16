package de.bitsandbooks.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
public class DigitalFinanceAccountantApplication {

  public static void main(String[] args) {
    SpringApplication.run(DigitalFinanceAccountantApplication.class, args);
  }
}
