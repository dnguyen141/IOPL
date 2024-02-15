package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.AuthorsConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class AuthorsConstraintValidator implements ConstraintValidator<AuthorsConstraint, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(AuthorsConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String AUTHORS_REGEX = "^[^,]+(?:,\\s*[^,]+)*{1,100}$";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(AUTHORS_REGEX).matcher(value).matches();
  }
}
