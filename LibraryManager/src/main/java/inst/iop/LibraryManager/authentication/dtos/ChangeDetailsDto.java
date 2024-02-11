package inst.iop.LibraryManager.authentication.dtos;

import inst.iop.LibraryManager.authentication.entities.constrains.NameConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.PasswordConstrain;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDetailsDto {

  @NameConstrain(isNotNullConstrain = false)
  private String firstName;

  @NameConstrain(isNotNullConstrain = false)
  private String lastName;

  @PasswordConstrain(isNotNullConstrain = false)
  private String password;

  @PasswordConstrain(isNotNullConstrain = false, message = "The confirmed password must match the password")
  private String confirmedPassword;
}

