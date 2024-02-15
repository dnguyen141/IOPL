package inst.iop.LibraryManager.library.entities.validators;

import inst.iop.LibraryManager.library.entities.constrains.IsbnConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IsbnConstrainValidator implements ConstraintValidator<IsbnConstrain, String> {

  private boolean isNotNullConstrain;

  @Override
  public void initialize(IsbnConstrain constraintAnnotation) {
    this.isNotNullConstrain = constraintAnnotation.isNotNullConstrain();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String ISBN_REGEX = "^(?:ISBN(?:-1[03])?:?●)?(?=[0-9X]{10}$|(?=(?:[0-9]+[-●]){3})" +
        "[-●0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[-●]){4})[-●0-9]{17}$)" +
        "(?:97[89][-●]?)?[0-9]{1,5}[-●]?[0-9]+[-●]?[0-9]+[-●]?[0-9X]$";
    return !isNotNullConstrain && value == null || value != null
        && Pattern.compile(ISBN_REGEX).matcher(value).matches();
  }
}
