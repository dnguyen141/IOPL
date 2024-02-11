package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.FieldConstrain;
import inst.iop.LibraryManager.library.entities.constrains.TitleConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TitleConstrainValidator implements ConstraintValidator<TitleConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(TitleConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && !value.isEmpty() && value.length() <= 100;
  }
}
