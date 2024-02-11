package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.AuthorsConstrain;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class BorrowStatusConstrainValidator implements ConstraintValidator<AuthorsConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(AuthorsConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Optional<BorrowStatus> bs = BorrowStatus.getBorrowStatusFromString(value);
    return !isNotNullConstrain && value == null || value != null && bs.isPresent();
  }
}
