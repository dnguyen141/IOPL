package inst.iop.LibraryManager.authentication.entities.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
  ADMIN_CREATE("admin:create"),
  ADMIN_DELETE("admin:delete"),
  ADMIN_READ("admin:read"),
  ADMIN_UPDATE("admin:update"),
  MODERATOR_CREATE("moderator:create"),
  MODERATOR_DELETE("moderator:delete"),
  MODERATOR_READ("moderator:read"),
  MODERATOR_UPDATE("moderator:update");

  private final String permission;
}
