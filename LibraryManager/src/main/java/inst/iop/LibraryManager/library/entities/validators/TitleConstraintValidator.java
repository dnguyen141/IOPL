package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.TitleConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TitleConstraintValidator implements ConstraintValidator<TitleConstraint, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(TitleConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && !value.isEmpty() && value.length() <= 100;
  }
}
