package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constrains.TypeConstrain;
import inst.iop.LibraryManager.library.entities.constrains.UrlConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class TypeConstrainValidator implements ConstraintValidator<TypeConstrain, Object> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(TypeConstrain constraintAnnotation) {
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
