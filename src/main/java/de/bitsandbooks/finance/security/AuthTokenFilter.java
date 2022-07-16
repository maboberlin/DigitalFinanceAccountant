package de.bitsandbooks.finance.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter implements WebFilter {

  public static final String HEADER_PREFIX = "Bearer ";

  private final AuthTokenProvider tokenProvider;

  @Override
  public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
    String token = resolveToken(serverWebExchange.getRequest());
    if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
      Authentication authentication = this.tokenProvider.getAuthentication(token);
      return webFilterChain
          .filter(serverWebExchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
    return webFilterChain.filter(serverWebExchange);
  }

  private String resolveToken(ServerHttpRequest request) {
    String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
