package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constrains.EmailConstrain;
import inst.iop.LibraryManager.authentication.entities.constrains.PasswordConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordConstrainValidator implements ConstraintValidator<PasswordConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PasswordConstrain constraintAnnotation) {
    isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z]).{6,20}$";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(PASSWORD_REGEX).matcher(value).matches();
  }
}
