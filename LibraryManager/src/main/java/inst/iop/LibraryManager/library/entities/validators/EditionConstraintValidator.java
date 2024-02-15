package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.EditionConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EditionConstraintValidator implements ConstraintValidator<EditionConstraint, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(EditionConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && value >= 1 && value <= 100;
  }
}