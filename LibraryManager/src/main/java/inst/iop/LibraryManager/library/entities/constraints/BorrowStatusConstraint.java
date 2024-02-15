package inst.iop.LibraryManager.library.entities.constraints;

import inst.iop.LibraryManager.library.entities.validators.BorrowStatusConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = BorrowStatusConstraintValidator.class)
public @interface BorrowStatusConstraint {
  String message() default "Borrow status must either be Requested, Issued, Returned, Overdue or Lost";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;
}