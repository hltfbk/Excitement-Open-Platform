package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecification;
//import eu.excitementproject.eop.transformations.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
//import eu.excitementproject.eop.transformations.rteflow.macro.Feature;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;


/**
 * Currently refers only to predicate-truth.
 * Note that {@link Constants#REQUIRE_PREDICATE_TRUTH_EQUALITY} refers only to predicate-truth,
 * as well as the {@linkplain Feature} {@link Feature#CHANGE_PREDICATE_TRUTH}.
 * <BR>
 * <B>Note:</B> The private method {@link #setAnnotationOfHypothesis(AdditionalNodeInformation, AdditionalNodeInformation)}
 * has an assumption that only predicate-truth is something that can be captured by this finder.
 * Thus, if in the future it will change - some changes in this class as well as
 * in the appropriate subclass of {@link GenerationOperation} and the appropriate subclass of {@link FeatureVectorUpdater}
 * should be performed <B>carefully</B>.
 * 
 * @author Asher Stern
 * @since Oct 30, 2011
 *
 */
public class ChangeAnnotationFinder implements Finder<SubstituteNodeSpecification>
{
	public static final String CHANGE_ANNOTATIONS_SPECIFICATION_NAME = "Change Annotation";
	
	public ChangeAnnotationFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
	{
		super();
		this.text = text;
		this.hypothesis = hypothesis;
	}
	
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}


	@Override
	public void find()
	{
		specs = new LinkedHashSet<SubstituteNodeSpecification>();
		ValueSetMap<ExtendedNode, ExtendedNode> matchIgnoreAnnotationHypothesisToText = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		for (ExtendedNode hypothesisNode : TreeIterator.iterableTree(hypothesis.getTree()))
		{
			for (ExtendedNode textNode : TreeIterator.iterableTree(text.getTree()))
			{
				if (AdvancedEqualities.nodesSimilarIgnoreAnnotations(textNode.getInfo(), hypothesisNode.getInfo()))
				{
					if (!AdvancedEqualities.nodesAnnotationMatch(textNode.getInfo(),hypothesisNode.getInfo()))
					{
						matchIgnoreAnnotationHypothesisToText.put(hypothesisNode,textNode);
					}
				}
				
			}
		}
		
		for (ExtendedNode hypothesisNode : matchIgnoreAnnotationHypothesisToText.keySet())
		{
			for (ExtendedNode textNode : matchIgnoreAnnotationHypothesisToText.get(hypothesisNode))
			{
				String textPT = ExtendedInfoGetFields.getPredTruth(textNode.getInfo());
				String hypothesisPT = ExtendedInfoGetFields.getPredTruth(hypothesisNode.getInfo());
				SubstituteNodeSpecification spec = new SubstituteNodeSpecification(textNode,textNode.getInfo().getNodeInfo(),setAnnotationOfHypothesis(textNode.getInfo().getAdditionalNodeInformation(),hypothesisNode.getInfo().getAdditionalNodeInformation()),CHANGE_ANNOTATIONS_SPECIFICATION_NAME);
				spec.addDescription("Change predicate truth ["+textPT+"->"+hypothesisPT+"] of node \""+textNode.getInfo().getId()+": "+InfoGetFields.getLemma(textNode.getInfo())+"\"");
				specs.add(spec);
			}
		}
	}
	
	
	@Override
	public Set<SubstituteNodeSpecification> getSpecs()
	{
		return specs;
	}

	/**
	 * Returns the {@link AdditionalNodeInformation} of the text, but with the "predicate-truth" value of the hypothesis.
	 * 
	 * @param textInfo
	 * @param hypothesisInfo
	 * @return
	 */
	private AdditionalNodeInformation setAnnotationOfHypothesis(AdditionalNodeInformation textInfo, AdditionalNodeInformation hypothesisInfo)
	{
		AdditionalNodeInformation ret = null;
		PredTruth hypothesisPredTruth = null;
		if (hypothesisInfo!=null)
			hypothesisPredTruth = hypothesisInfo.getPredTruth();

		if (textInfo!=null)
		{
			ret = AdditionalInformationServices.setPredTruth(textInfo, hypothesisPredTruth);
		}
		else
		{
			ret = AdditionalInformationServices.emptyInformation();
			AdditionalInformationServices.setPredTruth(ret, hypothesisPredTruth);
		}
		
		return ret;
	}
	
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> text;
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis;
	
	private Set<SubstituteNodeSpecification> specs;
}
