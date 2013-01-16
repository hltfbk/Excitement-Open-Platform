package eu.excitementproject.eop.common.codeannotations;

import java.lang.annotation.Documented;

/**
 * Indicates that a class is depends on a specific parser(s).
 * Any class that contains hard-coded string(s) which are parser
 * dependent - should be annotated with this annotation.
 * Any class that is parser dependent due to any other reason should be annotated
 * as well.
 * The value should be the parser-name on which this class depends.
 * For example
 * <pre>
 * @ParserSpecific("minipar")
 * </pre>
 * <pre>
 * @ParserSpecific({"minipar","easyfirst"})
 * </pre>
 * 
 * @author Asher Stern
 * @since Feb 13, 2012
 *
 */
@Documented
public @interface ParserSpecific
{
	String[] value();
}
