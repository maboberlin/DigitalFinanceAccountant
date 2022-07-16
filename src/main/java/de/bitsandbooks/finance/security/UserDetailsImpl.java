package de.bitsandbooks.finance.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bitsandbooks.finance.model.entities.Role;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1L;

  private String id;

  private String email;

  @JsonIgnore private String password;

  private Collection<? extends GrantedAuthority> authorities;

  private UserDetailsImpl() {}

  public static UserDetails create(String id, String email, String password, Set<Role> roles) {
    UserDetailsImpl userDetails = new UserDetailsImpl();
    userDetails.setId(id);
    userDetails.setEmail(email);
    userDetails.setPassword(password);
    userDetails.setAuthorities(collectAuthorities(roles));
    return userDetails;
  }

  private static Set<SimpleGrantedAuthority> collectAuthorities(Set<Role> roles) {
    return roles
        .stream()
        .map(role -> new SimpleGrantedAuthority(role.name().toUpperCase()))
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
