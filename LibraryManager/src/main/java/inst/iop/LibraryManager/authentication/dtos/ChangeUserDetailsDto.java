package inst.iop.LibraryManager.authentication.dtos;

import inst.iop.LibraryManager.authentication.entities.constraints.NameConstraint;
import inst.iop.LibraryManager.authentication.entities.constraints.PasswordConstraint;
import inst.iop.LibraryManager.authentication.entities.constraints.RoleConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChangeUserDetailsDto {

  @PasswordConstraint(isNotNullConstrain = false)
  private String password;

  @PasswordConstraint(isNotNullConstrain = false)
  private String confirmedPassword;

  @RoleConstraint(isNotNullConstrain = false)
  private String role;

  @NameConstraint(isNotNullConstrain = false)
  private String firstName;

  @NameConstraint(isNotNullConstrain = false)
  private String lastName;
}
