package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constraints.PasswordConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<PasswordConstraint, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PasswordConstraint constraintAnnotation) {
    isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z]).{6,20}$";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(PASSWORD_REGEX).matcher(value).matches();
  }
}
