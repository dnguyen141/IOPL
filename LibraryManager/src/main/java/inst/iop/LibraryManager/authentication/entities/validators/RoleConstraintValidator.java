package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constrains.RoleConstraint;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleConstraintValidator implements ConstraintValidator<RoleConstraint, Object> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(RoleConstraint constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return !isNotNullConstrain;
    }

    if (value instanceof String valueString) {
      return Arrays.stream(Role.values()).anyMatch(role -> role.name().equalsIgnoreCase(valueString));
    }

    return value instanceof Role;
  }
}
