package inst.iop.LibraryManager.authentication.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InformationResponse {
  private String type;
  private int status;
  private String message;
}

