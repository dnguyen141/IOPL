package inst.iop.LibraryManager.utilities;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

public class BindingResultHandler {
  public static Map<String, Object> handleBindingResult(BindingResult bindingResult) {
    Map<String, Object> violations = new HashMap<>();

    if (bindingResult.hasErrors()) {
      for (ObjectError error : bindingResult.getAllErrors()) {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        violations.put(fieldName, errorMessage);
      }
    }

    return violations;
  }
}
