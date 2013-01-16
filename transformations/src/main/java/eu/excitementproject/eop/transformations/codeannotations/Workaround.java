package eu.excitementproject.eop.transformations.codeannotations;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.Constants.Workarounds;


/**
 * Indicates a workaround.
 * Any method that implements a workaround (a way to by pass a bug, though it is
 * not fixed) should be annotated with this annotation.
 * 
 * <P>
 * How to write a workaround:
 * <OL>
 * <LI>The method with the work-around code should be annotated with {@link Workaround}</LI>
 * <LI>A constant-flag that decides whether the workaround will be run, or not (which
 * means that the bug will make its damages) should be defined in {@link Workarounds} (nested class of {@link Constants})</LI>
 * <LI>A warning (or error) should be printed by the logger (logger.warn("..."))</LI>
 * </OL>
 * 
 * @author Asher Stern
 * @since Mar 21, 2012
 *
 */
@Documented
@Target({ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.TYPE})
public @interface Workaround
{
	/**
	 * An optional brief explanation what exactly the workaround is.
	 * @return an optional brief explanation what exactly the workaround is.
	 */
	String value() default "";

}
