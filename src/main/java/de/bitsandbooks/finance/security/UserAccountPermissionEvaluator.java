package de.bitsandbooks.finance.security;

import static org.apache.commons.lang3.ObjectUtils.allNotNull;

import java.io.Serializable;
import java.util.Set;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class UserAccountPermissionEvaluator implements PermissionEvaluator {

  public static final String EXTERNAL_ACCOUNT_IDENTIFIER = "EXTERNAL_ACCOUNT_IDENTIFIER";
  public static final String USER_IDENTIFIER = "USER_IDENTIFIER";

  @Override
  public boolean hasPermission(
      Authentication authentication, Object externalAccountIdentifier, Object permission) {
    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Serializable targetId, String targetType, Object permission) {
    if (!allNotNull(authentication, authentication.getPrincipal(), targetId, targetType, permission)
        || !(authentication.getPrincipal() instanceof UserDetailsImpl)
        || !(targetId instanceof String)
        || !(targetType instanceof String)
        || !(permission instanceof String)) {
      return false;
    }
    switch (targetType) {
      case EXTERNAL_ACCOUNT_IDENTIFIER:
        return hasAuthorizedAccount(
            (UserDetailsImpl) authentication.getPrincipal(), (String) targetId);
      case USER_IDENTIFIER:
        return isAuthorizedUser((UserDetailsImpl) authentication.getPrincipal(), (String) targetId);
      default:
        return false;
    }
  }

  private boolean isAuthorizedUser(UserDetailsImpl principal, String externalUserId) {
    return externalUserId.equals(principal.getExternalIdentifier());
  }

  private boolean hasAuthorizedAccount(
      UserDetailsImpl principal, String externalAccountIdentifier) {
    Set<String> accountExternalIDSet = principal.getAccountExternalIDSet();
    return accountExternalIDSet.contains(externalAccountIdentifier);
  }
}
