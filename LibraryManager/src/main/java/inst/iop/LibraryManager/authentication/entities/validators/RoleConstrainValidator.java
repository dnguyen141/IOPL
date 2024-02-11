package inst.iop.LibraryManager.authentication.entities.validators;

import inst.iop.LibraryManager.authentication.entities.constrains.RoleConstrain;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.library.entities.BookType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleConstrainValidator implements ConstraintValidator<RoleConstrain, Object> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(RoleConstrain constraintAnnotation) {
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
