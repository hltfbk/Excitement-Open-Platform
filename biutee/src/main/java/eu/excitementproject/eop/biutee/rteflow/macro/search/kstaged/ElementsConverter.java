package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTreeElement;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;


/**
 * 
 * Gets a {@link LocalCreativeTreeElement} and produces
 * a {@link KStagedElement}.
 * 
 * @author Asher Stern
 * @since Oct 2, 2011
 *
 */
public class ElementsConverter
{
	public static KStagedElement create(
			LocalCreativeTreeElement element,
			String originalSentence,
			SingleTreeEvaluations evaluationsOfOriginalTree,
			double costOfOriginalTree,
			OperationsEnvironment environment
			) throws TreeAndParentMapException
	{
		TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap = 
			new TreeAndParentMap<ExtendedInfo, ExtendedNode>(element.getTree());
		
		// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(textTreeAndParentMap,environment.getHypothesis(),environment.getHypothesisLemmasLowerCase(),environment.getHypothesisNumberOfNodes());
		SingleTreeEvaluations evaluations = new AlignmentCalculator(environment.getAlignmentCriteria(), textTreeAndParentMap, environment.getHypothesis()).getEvaluations(environment.getHypothesisLemmasLowerCase(), environment.getHypothesisNumberOfNodes());
					
					
		KStagedElement createdElement = new KStagedElement(
				element.getTree(),
				element.getHistory(),
				element.getFeatureVector(),
				originalSentence,
				element.getCost(),
				evaluations,
				element.getGlobalIteration(),
				evaluationsOfOriginalTree,
				costOfOriginalTree
				);
		
		return createdElement;
	}

}
