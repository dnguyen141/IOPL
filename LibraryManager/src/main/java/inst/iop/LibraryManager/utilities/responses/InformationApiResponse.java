package inst.iop.LibraryManager.utilities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessApiResponse implements ApiResponse {
  private String status;
  private int code;
  private String message;
}

