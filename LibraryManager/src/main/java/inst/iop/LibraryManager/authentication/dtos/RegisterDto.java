package inst.iop.LibraryManager.authentication.dtos;

import inst.iop.LibraryManager.authentication.entities.constraints.EmailConstraint;
import inst.iop.LibraryManager.authentication.entities.constraints.NameConstraint;
import inst.iop.LibraryManager.authentication.entities.constraints.PasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

  @EmailConstraint
  private String email;

  @PasswordConstraint
  private String password;

  @PasswordConstraint
  private String confirmedPassword;

  @NameConstraint
  private String firstName;

  @NameConstraint
  private String lastName;
}