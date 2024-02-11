package inst.iop.LibraryManager.authentication.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum Role implements Comparable<Role> {
  USER(Collections.emptySet(), "ROLE_USER", 0),
  MODERATOR(Set.of(
      Permission.MODERATOR_CREATE,
      Permission.MODERATOR_DELETE,
      Permission.MODERATOR_READ,
      Permission.MODERATOR_UPDATE
  ), "ROLE_MODERATOR", 1),
  ADMIN(Set.of(
      Permission.ADMIN_CREATE,
      Permission.ADMIN_DELETE,
      Permission.ADMIN_READ,
      Permission.ADMIN_UPDATE,
      Permission.MODERATOR_CREATE,
      Permission.MODERATOR_DELETE,
      Permission.MODERATOR_READ,
      Permission.MODERATOR_UPDATE
  ), "ROLE_ADMIN", 2);

  private final Set<Permission> permissions;
  private final String authority;
  private final int order;

  public List<SimpleGrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    authorities.addAll(getPermissions()
        .stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
        .toList());
    return authorities;
  }

  public static Role getRoleFromAuthority(String authority) {
    for (Role role: Role.values()) {
      if (role.authority.equalsIgnoreCase(authority)) {
        return role;
      }
    }
    return null;
  }

  public static Role getRoleFromString(String role) {
    for (Role r: Role.values()) {
      if (r.name().equalsIgnoreCase(role)) {
        return r;
      }
    }
    return null;
  }
}
