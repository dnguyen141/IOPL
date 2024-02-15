package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.PageSizeConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageSizeConstrainValidator implements ConstraintValidator<PageSizeConstrain, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PageSizeConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return isNotNullConstrain && value == null || value != null && value >= 1;
  }
}