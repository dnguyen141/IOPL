package inst.iop.LibraryManager.authentication.dtos;

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

