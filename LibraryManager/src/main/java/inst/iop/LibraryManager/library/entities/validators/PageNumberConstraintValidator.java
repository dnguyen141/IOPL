package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.PageNumberConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageNumberConstraintValidator implements ConstraintValidator<PageNumberConstraint, Integer> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PageNumberConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return isNotNullConstrain && value == null || value != null && value >= 0;
  }
}