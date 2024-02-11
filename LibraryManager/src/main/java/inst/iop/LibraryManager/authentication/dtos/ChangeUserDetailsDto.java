package inst.iop.LibraryManager.authentication.dtos;

import inst.iop.LibraryManager.authentication.entities.constrains.NameConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.PasswordConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.RoleConstrain;
import lombok.*;

@Data
@AllArgsConstructor
public class ChangeUserDetailsDto {

  private String email;

  @PasswordConstrain(isNotNullConstrain = false)
  private String password;

  @PasswordConstrain(isNotNullConstrain = false, message = "The confirmed password must match the password")
  private String confirmedPassword;

  @RoleConstrain(isNotNullConstrain = false)
  private String role;

  @NameConstrain(isNotNullConstrain = false)
  private String firstName;

  @NameConstrain(isNotNullConstrain = false)
  private String lastName;
}
