package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constrains.FieldConstrain;
import inst.iop.LibraryManager.library.entities.constrains.TypeConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class FieldConstrainValidator implements ConstraintValidator<FieldConstrain, Object> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(FieldConstrain constraintAnnotation) {
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
