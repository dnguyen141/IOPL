package inst.iop.LibraryManager.utilities.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorWithViolationsApiResponse implements ApiResponse {
  private String status;
  private int code;
  private String message;
  private Map<String, Object> violations;

  ErrorWithViolationsApiResponse(int code, String message, Map<String, Object> data) {
    this("error", code, message, data);
  }
}
