package eu.excitementproject.eop.common.codeannotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * Indicates that a class is language dependent.
 * Any class that contains hard-coded string(s) which are language
 * dependent - should be annotated with this annotation.
 * Any class that is language dependent due to any other reason (for
 * example, it wraps a language dependent utility) should be annotated
 * as well.
 * The value should be the language on which this class depends.
 * For example
 * <pre>
 * @LanguageDependent("English")
 * </pre>
 * <pre>
 * @LanguageDependent({"English","French"})
 * </pre>
 * 
 * @author Asher Stern
 * @since Feb 8, 2012
 *
 */
@Documented
@Target(ElementType.TYPE)
public @interface LanguageDependent
{
	String[] value();
}
