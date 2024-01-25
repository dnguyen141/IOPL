package inst.iop.LibraryManager.authentication.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest implements AuthenticationRequestInterface {
  private String email;
  private String password;
  private String confirmedPassword;
  private String role;
  private String firstName;
  private String lastName;
}