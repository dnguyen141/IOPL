package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.PageSizeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageSizeConstraintValidator implements ConstraintValidator<PageSizeConstraint, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PageSizeConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return isNotNullConstrain && value == null || value != null && value >= 1;
  }
}