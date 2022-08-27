package de.bitsandbooks.finance.config;

import de.bitsandbooks.finance.security.AuthTokenFilter;
import de.bitsandbooks.finance.security.UserAccountPermissionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Autowired private ApplicationContext applicationContext;

  @Autowired private ReactiveUserDetailsService userDetailsService;

  @Autowired private AuthTokenFilter authTokenFilter;

  @Bean
  public ReactiveAuthenticationManager authenticationManager() {
    UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
        new UserDetailsRepositoryReactiveAuthenticationManager(this.userDetailsService);
    authenticationManager.setPasswordEncoder(passwordEncoder());
    return authenticationManager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    DefaultMethodSecurityExpressionHandler defaultWebSecurityExpressionHandler =
        this.applicationContext.getBean(DefaultMethodSecurityExpressionHandler.class);
    defaultWebSecurityExpressionHandler.setPermissionEvaluator(
        new UserAccountPermissionEvaluator());
    return http.cors()
        .and()
        .csrf()
        .disable()
        .authenticationManager(authenticationManager())
        .exceptionHandling()
        .authenticationEntryPoint(
            (exchange, e) -> {
              log.info("Authentication error: Unauthorized[401]: " + e.getMessage());
              return Mono.fromRunnable(
                  () -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
            })
        .accessDeniedHandler(
            (exchange, e) -> {
              log.info("Authentication error: Access Denied[401]: " + e.getMessage());
              return Mono.fromRunnable(
                  () -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
            })
        .and()
        .formLogin()
        .disable()
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .addFilterBefore(this.authTokenFilter, SecurityWebFiltersOrder.HTTP_BASIC)
        .authorizeExchange()
        .pathMatchers("/api/auth/**")
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .build();
  }
}
