package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.AuthorsConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class AuthorsConstrainValidator implements ConstraintValidator<AuthorsConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(AuthorsConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String AUTHORS_REGEX = "^[^,]+(?:,\\s*[^,]+)*{1,100}$";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(AUTHORS_REGEX).matcher(value).matches();
  }
}
