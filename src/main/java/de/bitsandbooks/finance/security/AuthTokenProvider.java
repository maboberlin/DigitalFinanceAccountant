package de.bitsandbooks.finance.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthTokenProvider {

  private static final String AUTHORITIES_KEY = "roles";
  private static final String ACCOUNTS_KEY = "accounts";
  private static final String IDENTIFIER_KEY = "identifier";

  @Value("${dfa.jwtSecret}")
  private String jwtSecret;

  @Value("${dfa.jwtExpirationMs}")
  private long jwtExpirationMs;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    String secret = Base64.getEncoder().encodeToString(this.jwtSecret.getBytes());
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String createToken(Authentication authentication) {
    String username = authentication.getName();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    if (!UserDetailsImpl.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
      throw new IllegalArgumentException(
          "Authentication principal is not of type 'UserDetailsImpl'");
    }
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
    Collection<String> accounts = principal.getAccountExternalIDSet();
    Claims claims = Jwts.claims().setSubject(username);
    if (!authorities.isEmpty()) {
      claims.put(
          AUTHORITIES_KEY,
          authorities
              .stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(",")));
    }
    if (!accounts.isEmpty()) {
      claims.put(ACCOUNTS_KEY, accounts);
    }

    claims.put(IDENTIFIER_KEY, principal.getExternalIdentifier());

    Date now = new Date();
    Date validity = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(this.secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();

    String identifier = (String) claims.get(IDENTIFIER_KEY);

    Collection<String> accounts = (Collection<String>) claims.get(ACCOUNTS_KEY);
    Set<String> accountIDList = accounts != null ? new HashSet<>(accounts) : Collections.emptySet();

    Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
    Collection<? extends GrantedAuthority> authorities =
        authoritiesClaim == null
            ? AuthorityUtils.NO_AUTHORITIES
            : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

    UserDetails principal =
        UserDetailsImpl.createWithAuthorities(
            identifier, claims.getSubject(), "", accountIDList, authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims =
          Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
      log.info("expiration date: {}", claims.getBody().getExpiration());
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.info("Invalid JWT token: {}", e.getMessage());
      log.trace("Invalid JWT token trace.", e);
    }
    return false;
  }
}
