package ist.meic.pa;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface KeywordArgs {
	/** This anotation receives a string representing the keyword arguments for a given constructor.
	 */
	String value();
}
