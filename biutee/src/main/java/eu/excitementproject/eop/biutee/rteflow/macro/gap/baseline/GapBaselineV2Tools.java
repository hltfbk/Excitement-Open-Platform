package eu.excitementproject.eop.biutee.rteflow.macro.gap.baseline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescriptionGenerator;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapFeaturesUpdate;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapHeuristicMeasure;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2013
 *
 */
public class GapBaselineV2Tools implements GapFeaturesUpdate<ExtendedInfo, ExtendedNode>, GapHeuristicMeasure<ExtendedInfo, ExtendedNode>, GapDescriptionGenerator<ExtendedInfo, ExtendedNode>
{
	public GapBaselineV2Tools(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			LinearClassifier classifierForSearch,
			UnigramProbabilityEstimation mleEstimation,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		super();
		this.hypothesisTree = hypothesisTree;
		this.classifierForSearch = classifierForSearch;
		this.mleEstimation = mleEstimation;
		this.alignmentCriteria = alignmentCriteria;
	}

	@Override
	public GapDescription describeGap(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
			GapEnvironment<ExtendedInfo, ExtendedNode> environment)
			throws GapException
	{
		GapBaselineV2Calculator calculator = getCalculator(tree,environment);
		
		String ret = describe("missing lemmas: ", calculator.getMissingLemmas())+
				describe("missing nodes: ", convertNodesToStrings(calculator.getMissingNodes()) )+
				describe("missing relations: ", convertRelationsToStrings(calculator.getMissingRelations()) );

		return new GapDescription(ret);
	}

	@Override
	public double measure(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
			Map<Integer, Double> featureVector,
			GapEnvironment<ExtendedInfo, ExtendedNode> environment)
			throws GapException
	{
		try
		{
			double costWithoutGap = -classifierForSearch.getProduct(featureVector);
			Map<Integer, Double> featureVectorWithGap = updateForGap(tree,featureVector,environment);
			double costWithGap = -classifierForSearch.getProduct(featureVectorWithGap);
			
			double ret = costWithGap-costWithoutGap;
			if (ret<0) throw new GapException("gap measure is negative: "+String.format("%-4.4f", ret));
			//logger.info("gap measure = "+String.format("%-6.6f", ret));
			return ret;
		}
		catch(ClassifierException e){throw new GapException("Failed to calculate gap measure, due to a problem in the classifier.",e);}
	}

	@Override
	public Map<Integer, Double> updateForGap(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
			Map<Integer, Double> featureVector,
			GapEnvironment<ExtendedInfo, ExtendedNode> environment)
			throws GapException
	{
		GapBaselineV2Calculator calculator = getCalculator(tree,environment);
		Map<Integer, Double> ret = new LinkedHashMap<>();
		ret.putAll(featureVector);
		ret.put(Feature.GAP_BASELINE_V2_MISSING_LEMMA.getFeatureIndex(),
				featureValueForStrings(calculator.getMissingLemmas(),false)
				);
		ret.put(Feature.GAP_BASELINE_V2_MISSING_NODE.getFeatureIndex(),
				featureValueForStrings(convertNodesToStrings(calculator.getMissingNodes()),true)
				);
		ret.put(Feature.GAP_BASELINE_V2_MISSING_RELATION.getFeatureIndex(),
				featureValueForStrings(convertNodesToStrings(calculator.getMissingRelations()),true)
				);
		
		return ret;
	}
	
	
	
	
	private synchronized GapBaselineV2Calculator getCalculator(TreeAndParentMap<ExtendedInfo, ExtendedNode> givenTree, GapEnvironment<ExtendedInfo, ExtendedNode> environment)
	{
		ExtendedNode tree = givenTree.getTree();
		if ( (lastTree==tree) && (lastCalculator!=null) )
		{
			return lastCalculator;
		}
		else
		{
			lastTree = null;
			lastCalculator = null;
			
			lastCalculator = new GapBaselineV2Calculator(givenTree, hypothesisTree, environment, alignmentCriteria, environment.getHypothesisLemmasLowerCase());
			lastCalculator.calculate();
			lastTree = tree;
			
			return lastCalculator;
		}
	}
	
	private List<String> convertRelationsToStrings(Set<ExtendedNode> nodes)
	{
		List<String> ret = new ArrayList<>(nodes.size());
		for (ExtendedNode node : nodes)
		{
			ExtendedNode parent = hypothesisTree.getParentMap().get(node);
			String parentString = "";
			if (parent!=null)
			{
				parentString = "<"+InfoGetFields.getLemma(parent.getInfo());
			}
			ret.add(InfoGetFields.getLemma(node.getInfo())+parentString);
		}
		return ret;
	}
	
	private List<String> convertNodesToStrings(Set<ExtendedNode> nodes)
	{
		if (nodes==null) {return null;}
		
		List<String> ret = new ArrayList<>(nodes.size());
		for (ExtendedNode node : nodes)
		{
			ret.add(InfoGetFields.getLemma(node.getInfo()));
		}
		return ret;
	}
	
	private double featureValueForStrings(Collection<? extends String> strings, boolean countOnly) throws GapException
	{
		double ret = 0.0;
		if ( (BiuteeConstants.USE_MLE_FOR_GAP) && (!countOnly) )
		{
			for (String str : strings)
			{
				ret += Math.log(mleEstimation.getEstimationFor(str));
			}
		}
		else
		{
			ret = (double)(-strings.size());
		}
		if (ret>0.0) throw new GapException("bug");
		return ret;
	}
	
	private String describe(String prefix, Collection<? extends String> strings)
	{
		if (strings==null) return "";
		if (strings.size()==0) return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		boolean firstIteration = true;
		for (String str : strings)
		{
			if (firstIteration) {firstIteration=false;}
			else {sb.append(", ");}
			sb.append(str);
		}
		sb.append("\n");
		return sb.toString();
	}

	
	
	
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree;
	private final LinearClassifier classifierForSearch;
	private final UnigramProbabilityEstimation mleEstimation;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	
	private ExtendedNode lastTree = null;
	private GapBaselineV2Calculator lastCalculator = null;

}
