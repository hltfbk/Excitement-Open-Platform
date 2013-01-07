package ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.SingleTreeEvaluations;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative.LocalCreativeTreeElement;
import ac.biu.nlp.nlp.engineml.rteflow.micro.OperationsEnvironment;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;


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
