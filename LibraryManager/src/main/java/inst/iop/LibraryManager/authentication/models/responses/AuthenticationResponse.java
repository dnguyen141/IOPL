package inst.iop.LibraryManager.authentication.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
  private String access_token;
  private String refresh_token;
}
