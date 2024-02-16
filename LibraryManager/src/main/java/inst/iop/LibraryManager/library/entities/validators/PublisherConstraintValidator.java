package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constraints.PublisherConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PublisherConstraintValidator implements ConstraintValidator<PublisherConstraint, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(PublisherConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !isNotNullConstrain && value == null || value != null && !value.isEmpty() && value.length() <= 100;
  }
}
