package imagingbook.common.util.choice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an annotation to mark nested (member) subclasses within a class or interface
 * to be eligible as choices to be instantiated.
 * @see Choices
 * @author WB
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DeclareChoice {
	public String value() default "";
}
