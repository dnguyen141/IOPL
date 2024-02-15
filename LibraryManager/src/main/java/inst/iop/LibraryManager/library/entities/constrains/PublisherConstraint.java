package inst.iop.LibraryManager.library.entities.constrains;

import inst.iop.LibraryManager.library.entities.validators.IsbnConstrainValidator;
import inst.iop.LibraryManager.library.entities.validators.PublisherConstrainValidator;
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
@Constraint(validatedBy = PublisherConstrainValidator.class)
public @interface PublisherConstrain {
  String message() default "Book publisher should have at least 1 character and at most 100 character(s)";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;
}