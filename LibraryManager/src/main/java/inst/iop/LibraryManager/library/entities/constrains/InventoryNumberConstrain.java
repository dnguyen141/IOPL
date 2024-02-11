package inst.iop.LibraryManager.library.entities.constrains;

import inst.iop.LibraryManager.library.entities.validators.InventoryNumberConstrainValidator;
import inst.iop.LibraryManager.library.entities.validators.IsbnConstrainValidator;
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
@Constraint(validatedBy = InventoryNumberConstrainValidator.class)
public @interface InventoryNumberConstrain {
  String message() default "Inventory number must match the format \"{book_id} CSST " +
      "{book_type_first_2_letters_capitalized} {year_of_publish}\"";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default false;
}