package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.PageNumberConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageNumberConstrainValidator implements ConstraintValidator<PageNumberConstrain, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PageNumberConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return isNotNullConstrain && value == null || value != null && value >= 0;
  }
}