package inst.iop.LibraryManager.authentication.dtos;

import inst.iop.LibraryManager.authentication.entities.constrains.NameConstraint;
import inst.iop.LibraryManager.authentication.entities.constrains.PasswordConstraint;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDetailsDto {

  @NameConstraint(isNotNullConstrain = false)
  private String firstName;

  @NameConstraint(isNotNullConstrain = false)
  private String lastName;

  @PasswordConstraint(isNotNullConstrain = false)
  private String password;

  @PasswordConstraint(isNotNullConstrain = false)
  private String confirmedPassword;
}

