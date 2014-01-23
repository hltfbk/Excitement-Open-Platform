package eu.excitementproject.eop.biutee.rteflow.macro.gap.baseline;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * 
 * @author Asher Stern
 * @since Sep 1, 2013
 *
 * @param <I>
 * @param <ExtendedNode>
 */
@NotThreadSafe
public class GapBaselineV1Tools implements GapFeaturesUpdate<ExtendedInfo, ExtendedNode>, GapHeuristicMeasure<ExtendedInfo, ExtendedNode>, GapDescriptionGenerator<ExtendedInfo, ExtendedNode>
{
	public GapBaselineV1Tools(TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			LinearClassifier classifierForSearch,
			UnigramProbabilityEstimation mleEstimation,
			ImmutableSet<String> stopWords,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		super();
		this.hypothesisTree = hypothesisTree;
		this.classifierForSearch = classifierForSearch;
		this.mleEstimation = mleEstimation;
		this.stopWords = stopWords;
		this.alignmentCriteria = alignmentCriteria;
	}

	@Override
	public GapDescription describeGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
			GapEnvironment<ExtendedInfo, ExtendedNode> environment) throws GapException
	{
		GapBaselineV1Calculator calculator = getCalculator(tree,environment);
		String description =
				strListNodes("missing named entities: ",calculator.getUncoveredNodesNamedEntities(),false)+
				strListNodes("missing nodes: ",calculator.getUncoveredNodesNotNamedEntities(),false)+
				strListNodes("missing non-content words: ",calculator.getUncoveredNodesNonContentWords(),false)+
				strListNodes("missing edges: ",calculator.getUncoveredEdges(),true);
		
		return new GapDescription(description);
	}

	@Override
	public double measure(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
			Map<Integer, Double> featureVector, GapEnvironment<ExtendedInfo, ExtendedNode> environment)
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
	public Map<Integer, Double> updateForGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
			Map<Integer, Double> featureVector, GapEnvironment<ExtendedInfo, ExtendedNode> environment)
			throws GapException
	{
		GapBaselineV1Calculator calculator = getCalculator(tree,environment);
		Map<Integer, Double> newFeatureVector = new LinkedHashMap<>();
		newFeatureVector.putAll(featureVector);
		newFeatureVector.put(Feature.GAP_BASELINE_V1_MISSING_NODE.getFeatureIndex(),
				featureValueMissingNodes(calculator.getUncoveredNodesNotNamedEntities()));
		newFeatureVector.put(Feature.GAP_BASELINE_V1_MISSING_NODE_NON_CONTENT_WORD.getFeatureIndex(),
				featureValueMissingNodes(calculator.getUncoveredNodesNonContentWords()));
		newFeatureVector.put(Feature.GAP_BASELINE_V1_MISSING_NODE_NAMED_ENTITY.getFeatureIndex(),
				featureValueMissingNodes(calculator.getUncoveredNodesNamedEntities()));
		newFeatureVector.put(Feature.GAP_BASELINE_V1_MISSING_EDGE.getFeatureIndex(),
				(double)(-calculator.getUncoveredEdges().size()) );
		
		return newFeatureVector;
	}
	
	private double featureValueMissingNodes(List<ExtendedNode> nodes) throws GapException
	{
		double ret = 0.0;
		if (BiuteeConstants.USE_MLE_FOR_GAP)
		{
			for (ExtendedNode node : nodes)
			{
				String lemma = InfoGetFields.getLemma(node.getInfo());
				ret += Math.log(mleEstimation.getEstimationFor(lemma));
			}
		}
		else
		{
			ret = (double)(-nodes.size());
		}
		if (ret>0.0) {throw new GapException("Bug or corrupted Unigram-MLE: invalid feature value. Feature value is higher than zero.");}
		return ret;
	}
	
	
	private synchronized GapBaselineV1Calculator getCalculator(TreeAndParentMap<ExtendedInfo, ExtendedNode> givenTree, GapEnvironment<ExtendedInfo, ExtendedNode> environment)
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
			
			lastCalculator = new GapBaselineV1Calculator(givenTree, hypothesisTree, environment, alignmentCriteria);
			lastCalculator.calculate();
			lastTree = tree;
			
			return lastCalculator;
		}
	}
	
	private String strListNodes(String prefix, List<ExtendedNode> nodes, boolean edge)
	{
		String strOfNodes = strListNodes(nodes,edge);
		if (strOfNodes.length()>0)
		{
			return prefix+strOfNodes+"\n";
		}
		else return "";
	}
	
	private String strListNodes(List<ExtendedNode> nodes, boolean edge)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (ExtendedNode node : nodes)
		{
			if (firstIteration){firstIteration=false;}
			else {sb.append(", ");}
			sb.append(InfoGetFields.getLemma(node.getInfo()));
			if (edge)
			{
				ExtendedNode parent = hypothesisTree.getParentMap().get(node);
				if (parent!=null)
				{
					sb.append("<").append(InfoGetFields.getLemma(parent.getInfo()));
				}
			}
		}
		return sb.toString();
	}

	
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree;
	private final LinearClassifier classifierForSearch;
	private final UnigramProbabilityEstimation mleEstimation;
	@SuppressWarnings("unused")
	private final ImmutableSet<String> stopWords;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	
	
	private ExtendedNode lastTree = null;
	private GapBaselineV1Calculator lastCalculator = null;

}
