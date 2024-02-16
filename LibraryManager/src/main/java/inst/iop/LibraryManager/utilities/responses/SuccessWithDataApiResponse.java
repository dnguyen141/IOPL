package inst.iop.LibraryManager.utilities.responses;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SuccessWithDataApiResponse implements ApiResponse {
  private String status;
  private int code;
  private String message;
  private Map<String, Object> data;

  SuccessWithDataApiResponse(int code, String message, Map<String, Object> data) {
    this("success", code, message, data);
  }
}
