package inst.iop.LibraryManager.authentication.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static inst.iop.LibraryManager.authentication.entities.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
  USER(Collections.emptySet()),
  ADMIN(Set.of(
      ADMIN_CREATE,
      ADMIN_DELETE,
      ADMIN_READ,
      ADMIN_UPDATE,
      MODERATOR_CREATE,
      MODERATOR_DELETE,
      MODERATOR_READ,
      MODERATOR_UPDATE
  )),
  MODERATOR(Set.of(
      MODERATOR_CREATE,
      MODERATOR_DELETE,
      MODERATOR_READ,
      MODERATOR_UPDATE
  ));

  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
        .stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
        .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }
}
