package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.IsbnConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IsbnConstraintValidator implements ConstraintValidator<IsbnConstraint, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(IsbnConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String ISBN_REGEX = "^\\d{10}(\\d{3})?$";
    return !isNotNullConstrain && value == null || value != null
        && Pattern.compile(ISBN_REGEX).matcher(value.replaceAll("-", "")).matches();
  }
}
