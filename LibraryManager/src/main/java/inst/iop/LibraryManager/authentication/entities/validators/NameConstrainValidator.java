package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constrains.NameConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameConstrainValidator implements ConstraintValidator<NameConstrain, String> {

  private boolean isNotNullConstrain;
  private int min;
  private int max;

  @Override
  public void initialize(NameConstrain constraintAnnotation) {
    isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
    min = constraintAnnotation.min();
    max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && value.length() >= min && value.length() <= max;
  }
}