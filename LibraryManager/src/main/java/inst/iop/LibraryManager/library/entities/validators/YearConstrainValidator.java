package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.YearConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class YearConstrainValidator implements ConstraintValidator<YearConstrain, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(YearConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    int currentYear = Year.now().getValue();
    return isNotNullConstrain && value == null || value != null && value >= 1900 && value <= currentYear;
  }
}