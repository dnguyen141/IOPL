package inst.iop.LibraryManager.authentication.models.responses;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class InformationWithDetailsResponse {
  private String type;
  private int status;
  private String message;
  private Map<String, Object> details;
}
