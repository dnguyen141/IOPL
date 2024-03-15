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
public class UpdateUserDetailsDto {

  @PasswordConstraint
  private String password;

  @PasswordConstraint
  private String confirmedPassword;

  @RoleConstraint
  private String role;

  @NameConstraint
  private String firstName;

  @NameConstraint
  private String lastName;
}
