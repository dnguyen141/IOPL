package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.constraints.FieldConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldConstraintValidator implements ConstraintValidator<FieldConstraint, Object> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(FieldConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return !isNotNullConstrain;
    }

    if (value instanceof String valueString) {
      return !valueString.isEmpty() && valueString.length() <= 100;
    }

    if (value instanceof BookField bf) {
      return !bf.getName().isEmpty() && bf.getName().length() <= 100;
    }

    return false;
  }
}
