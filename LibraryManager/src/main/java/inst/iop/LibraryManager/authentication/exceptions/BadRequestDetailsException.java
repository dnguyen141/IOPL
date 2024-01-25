package inst.iop.LibraryManager.authentication.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@AllArgsConstructor
public class BadRequestDetailsException extends Throwable {
  private final Map<String, Object> violations;
}
