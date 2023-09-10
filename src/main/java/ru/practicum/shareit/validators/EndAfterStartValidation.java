package ru.practicum.shareit.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndAfterStartValidator.class)
public @interface EndAfterStartValidation {
    String message() default "Время начала не может быть позже время окончания или null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}