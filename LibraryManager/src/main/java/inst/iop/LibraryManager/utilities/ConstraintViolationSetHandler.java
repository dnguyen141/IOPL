package inst.iop.LibraryManager.utilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Path.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConstraintViolationSetHandler {

  public static <T> Map<String, String> convertConstrainViolationSetToMap(
      Set<ConstraintViolation<T>> constraintViolations) {
    Map<String, String> violations = new HashMap<>();

    for (ConstraintViolation<T> violation : constraintViolations) {
      String field = extractFieldNameFromViolation(violation);
      String message = violation.getMessage();
      violations.put(field, message);
    }

    return violations;
  }

  private static <T> String extractFieldNameFromViolation(ConstraintViolation<T> violation) {
    String fieldName = null;
    for (Node node : violation.getPropertyPath()) {
      fieldName = node.getName();
    }
    return fieldName;
  }
}
