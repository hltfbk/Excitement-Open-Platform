/**
 * 
 */
package eu.excitementproject.eop.common.component.lexicalknowledge;
import java.io.Serializable;


/**
 * Implementations of this interface hold the additional information pertinent to a {@link LexicalRule} of that implementation, but not covered by the fields of
 * {@link LexicalRule}, for instance the synsets of a wordnet rule, or the extraction type of a wikipedia rule.
 * <p>
 * <b>Implementations must be immutable, and implement <code>equals()</code> and <code>hasCode()</code>!</b>
 * @author Amnon Lotan
 * @since 06/05/2011
 * 
 */
public interface RuleInfo extends Serializable
{
}
