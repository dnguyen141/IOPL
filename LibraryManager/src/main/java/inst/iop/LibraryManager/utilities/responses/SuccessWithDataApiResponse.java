package inst.iop.LibraryManager.utilities.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Builder
@AllArgsConstructor
public class SuccessWithDataResponse implements ApiResponse {
  private String status;
  private int code;
  private String message;
  private Map<String, Object> data;

  @Override
  public SuccessWithDataResponse build() {
    return new SuccessWithDataResponse(status, code, message, data);
  }
}
