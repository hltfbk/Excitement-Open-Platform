/**
 * 
 */
package ac.biu.nlp.nlp.engineml.utilities.view;
import java.util.List;

import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
/**
 * @author Amnon Lotan
 * @since 04/06/2011
 * 
 */
public class ExtendedRulesViewer extends RulesViewer<ExtendedInfo, ExtendedNode>
{
	/**
	 * Ctor
	 * @param rules
	 */
	public ExtendedRulesViewer(List<RuleWithConfidenceAndDescription<ExtendedInfo, ExtendedNode>> rules) {
		super(rules, new ExtendedIdLemmaPosRelNodeString());
	}

}
