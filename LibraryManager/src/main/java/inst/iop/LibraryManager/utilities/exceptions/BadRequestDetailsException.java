package inst.iop.LibraryManager.utilities.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class BadRequestDetailsException extends RuntimeException {
  private final String message;
  private final Map<String, String> violations;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Error: " + message + " - ");
    int index = 0;
    for (Map.Entry<String, String> entry : violations.entrySet()) {
      sb.append(entry.getKey()).append(": ").append(entry.getValue());
      if (index < violations.size() - 1) {
        sb.append("; ");
        index++;
      } else {
        sb.append(".");
      }
    }
    return sb.toString();
  }
}
