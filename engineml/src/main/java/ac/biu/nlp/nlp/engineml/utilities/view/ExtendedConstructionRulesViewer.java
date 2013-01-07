/**
 * 
 */
package ac.biu.nlp.nlp.engineml.utilities.view;

import java.util.List;

import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.representation.ExtendedConstructionNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
/**
 * @author Amnon Lotan
 * @since 04/06/2011
 * 
 */
public class ExtendedConstructionRulesViewer extends RulesViewer<ExtendedInfo, ExtendedConstructionNode>
{
	/**
	 * Ctor
	 * @param rules
	 */
	public ExtendedConstructionRulesViewer(List<RuleWithConfidenceAndDescription<ExtendedInfo, ExtendedConstructionNode>> rules) {
		super(rules, new ExtendedIdLemmaPosRelNodeString());
	}

}
