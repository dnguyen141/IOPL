package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constraints.TypeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeConstraintValidator implements ConstraintValidator<TypeConstraint, Object> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(TypeConstraint constraintAnnotation) {
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

    if (value instanceof BookType bt) {
      return !bt.getName().isEmpty() && bt.getName().length() <= 100;
    }

    return false;
  }
}
