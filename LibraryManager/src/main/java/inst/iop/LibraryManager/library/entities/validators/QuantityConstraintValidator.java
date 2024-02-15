package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.QuantityConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuantityConstraintValidator implements ConstraintValidator<QuantityConstraint, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(QuantityConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && value >= 1 && value <= 99;
  }
}