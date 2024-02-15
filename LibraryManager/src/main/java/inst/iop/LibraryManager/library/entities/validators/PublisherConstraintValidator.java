package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.PublisherConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PublisherConstrainValidator implements ConstraintValidator<PublisherConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PublisherConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && !value.isEmpty() && value.length() <= 100;
  }
}
