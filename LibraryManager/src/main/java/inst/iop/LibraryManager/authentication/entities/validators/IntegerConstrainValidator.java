package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constrains.IntegerConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IntegerConstrainValidator implements ConstraintValidator<IntegerConstrain, Integer> {

  private boolean isNotNullConstrain;
  private int min;
  private int max;

  @Override
  public void initialize(IntegerConstrain constraintAnnotation) {
    isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
    min = constraintAnnotation.min();
    max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && value >= min && value <= max;
  }
}