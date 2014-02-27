/**
 * 
 */
package eu.excitementproject.eop.transformations.utilities.view;
import java.util.List;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;

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
