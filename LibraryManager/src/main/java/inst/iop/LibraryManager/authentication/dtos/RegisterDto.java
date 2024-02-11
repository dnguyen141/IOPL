package inst.iop.LibraryManager.authentication.dtos;

import inst.iop.LibraryManager.authentication.entities.constrains.EmailConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.NameConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.PasswordConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.RoleConstrain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

  @EmailConstrain
  private String email;

  @PasswordConstrain
  private String password;

  @PasswordConstrain(message = "The confirmed password must match the password")
  private String confirmedPassword;

  @NameConstrain
  private String firstName;

  @NameConstrain
  private String lastName;
}