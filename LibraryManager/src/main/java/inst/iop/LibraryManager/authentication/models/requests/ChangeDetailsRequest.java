package inst.iop.LibraryManager.authentication.models.requests;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDetailsRequest implements AuthenticationRequestInterface {
  private String firstName;
  private String lastName;
  private String password;
  private String confirmedPassword;
}

