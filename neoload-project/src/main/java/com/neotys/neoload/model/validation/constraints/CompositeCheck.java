package com.neotys.neoload.model.validation.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.neotys.neoload.model.validation.constraintvalidators.DurationValidator;
import com.neotys.neoload.model.validation.constraintvalidators.StartAfterValidator;
import com.neotys.neoload.model.validation.constraintvalidators.StopAfterValidator;

@Target( { TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {DurationValidator.class, StartAfterValidator.class, StopAfterValidator.class})
public @interface CompositeCheck { //GreaterThanOrEqualOne
	String message() default "";
	 
	Class<?>[] groups() default {};
	 
	Class<? extends Payload>[] payload() default {};
}
