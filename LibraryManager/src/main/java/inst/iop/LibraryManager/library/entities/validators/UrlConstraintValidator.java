package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.UrlConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UrlConstraintValidator implements ConstraintValidator<UrlConstraint, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(UrlConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String COVER_IMAGE_URI_REGEX = "^(http|https)://.*";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(COVER_IMAGE_URI_REGEX).matcher(value).matches();
  }
}
