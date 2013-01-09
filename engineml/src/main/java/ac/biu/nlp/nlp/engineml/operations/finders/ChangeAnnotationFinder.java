package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.representation.AdditionalInformationServices;
import ac.biu.nlp.nlp.engineml.representation.AdditionalNodeInformation;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfoGetFields;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.annotations.PredTruth;
import ac.biu.nlp.nlp.engineml.rteflow.macro.Feature;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.AdvancedEqualities;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;


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

	@Override
	public void find()
	{
		specs = new LinkedHashSet<SubstituteNodeSpecification>();
		ValueSetMap<ExtendedNode, ExtendedNode> matchIgnoreAnnotationHypothesisToText = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		for (ExtendedNode hypothesisNode : AbstractNodeUtils.treeToSet(hypothesis.getTree()))
		{
			for (ExtendedNode textNode : AbstractNodeUtils.treeToSet(text.getTree()))
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
