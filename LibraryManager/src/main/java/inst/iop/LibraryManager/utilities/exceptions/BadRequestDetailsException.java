package inst.iop.LibraryManager.utilities.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class BadRequestDetailsException extends RuntimeException {
  private final Map<String, Object> violations;
}
