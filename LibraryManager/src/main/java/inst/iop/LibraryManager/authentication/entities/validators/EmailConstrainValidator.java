package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constrains.EmailConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailConstrainValidator implements ConstraintValidator<EmailConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(EmailConstrain constraintAnnotation) {
    isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(EMAIL_REGEX).matcher(value).matches();
  }
}
