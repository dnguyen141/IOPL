package inst.iop.LibraryManager.authentication.models.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserDetailsRequest implements AuthenticationRequestInterface {
  private String email;
  private String password;
  private String confirmedPassword;
  private String role;
  private String firstName;
  private String lastName;
}
