/**
 * 
 */
package eu.excitementproject.eop.transformations.utilities.view;
import java.util.List;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

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
