package de.bitsandbooks.finance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Slf4j
@Configuration
public class FrontendRoutingConfiguration {

  @Bean
  public RouterFunction<ServerResponse> htmlRouter(
      @Value("classpath:/static/index.html") Resource html) {
    return RouterFunctions.route(
        RequestPredicates.GET("/"),
        request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html));
  }

  @Bean
  public RouterFunction<ServerResponse> contentRouter() {
    return RouterFunctions.resources("/**", new ClassPathResource("static/"));
  }
}
