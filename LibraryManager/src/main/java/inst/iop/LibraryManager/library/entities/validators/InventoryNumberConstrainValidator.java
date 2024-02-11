package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.InventoryNumberConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class InventoryNumberConstrainValidator implements ConstraintValidator<InventoryNumberConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(InventoryNumberConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String INVENTORY_NUMBER_REGEX = "^\\d{3,}\\sCSST\\s[A-Z]{2}\\s\\d{4}$";
    return !isNotNullConstrain && value == null || value != null &&
        Pattern.compile(INVENTORY_NUMBER_REGEX).matcher(value).matches();
  }
}
