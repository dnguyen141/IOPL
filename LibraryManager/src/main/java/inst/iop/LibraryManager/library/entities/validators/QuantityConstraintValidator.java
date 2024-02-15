package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.QuantityConstrain;
import inst.iop.LibraryManager.library.entities.constrains.YearConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class QuantityConstrainValidator implements ConstraintValidator<QuantityConstrain, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(QuantityConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && value >= 1 && value <= 99;
  }
}