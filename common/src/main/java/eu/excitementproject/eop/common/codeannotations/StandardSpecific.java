package eu.excitementproject.eop.common.codeannotations;

import java.lang.annotation.Documented;

/**
 * Indicates dependency on a particular standard.
 * For example, Penn part-of-speech, Stanford-dependencies.
 * <P>
 * When setting the argument, please use lower-case letters only, and use hyphens (-) instead of spaces.
 * Annotation should look like:
 * <pre>
 * @StandardSpecific("stanford-dependencies")
 * </pre>
 * <BR>
 * <pre>
 * @StandardSpecific("penn-part-of-speech")
 * </pre>
 * 
 * @author Asher Stern
 * @since October 9 2012
 *
 */
@Documented
public @interface StandardSpecific
{
	String[] value();
}
