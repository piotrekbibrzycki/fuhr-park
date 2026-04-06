package com.example.fuhrpark.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureOrTodayValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface FutureOrToday {
    String message() default "Data nie może być wcześniejsza niż dzisiejsza";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
