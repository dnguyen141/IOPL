package inst.iop.LibraryManager.utilities.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InformationApiResponse implements ApiResponse {
  private String status;
  private int code;
  private String message;
}

