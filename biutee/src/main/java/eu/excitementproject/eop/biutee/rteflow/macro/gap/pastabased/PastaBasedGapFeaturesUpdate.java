package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapFeaturesUpdate;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapHeuristicMeasure;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.lap.biu.en.pasta.PredicateArgumentStructureBuilderFactory;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentStructureBuilder;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2013
 *
 * @param <I>
 * @param <S>
 */
@NotThreadSafe
public class PastaBasedGapFeaturesUpdate<I extends Info, S extends AbstractNode<I, S>> implements GapFeaturesUpdate<I, S>, GapHeuristicMeasure<I,S>, GapDescription<I,S>
{
	public PastaBasedGapFeaturesUpdate(
			PredicateArgumentStructureBuilderFactory<I, S> builderFactory,
			Set<PredicateArgumentStructure<I, S>> hypothesisStructures,
			TreeAndParentMap<I, S> hypothesisTree,
			LinearClassifier classifierForSearch)
	{
		super();
		this.builderFactory = builderFactory;
		this.hypothesisStructures = hypothesisStructures;
		this.hypothesisTree = hypothesisTree;
		this.classifierForSearch = classifierForSearch;
	}



	@Override
	public synchronized Map<Integer, Double> updateForGap(TreeAndParentMap<I, S> tree,
			Map<Integer, Double> featureVector, GapEnvironment<I, S> environment) throws GapException
	{
		Map<Integer, Double> ret = new LinkedHashMap<>();
		ret.putAll(featureVector);
		
		PastaGapFeaturesCalculator<I, S> theCalculator = createAndGetCalculator(tree);
		ret.put(Feature.GAP_MISSING_PREDICATES.getFeatureIndex(), (double)(-theCalculator.getMissingPredicates().size()));
		ret.put(Feature.GAP_ARGUMENT_HEAD_NOT_CONNECTED.getFeatureIndex(), (double)(-theCalculator.getArgumentHeadNotConnected().size()));
		ret.put(Feature.GAP_ARGUMENT_HEAD_MISSING.getFeatureIndex(), (double)(-theCalculator.getMissingArgument().size()));
		ret.put(Feature.GAP_ARGUMENT_NODE_NOT_CONNECTED.getFeatureIndex(), (double)(-theCalculator.getLemmaNotInArgument().size()));
		ret.put(Feature.GAP_ARGUMENT_NODE_MISSING.getFeatureIndex(), (double)(-theCalculator.getMissingLemmaOfArgument().size()));
		
		return ret;
	}
	
	

	@Override
	public synchronized double measure(TreeAndParentMap<I, S> tree, Map<Integer, Double> featureVector, GapEnvironment<I, S> environment) throws GapException
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
	public synchronized String describeGap(TreeAndParentMap<I, S> tree, GapEnvironment<I, S> environment) throws GapException
	{
		PastaGapFeaturesCalculator<I, S> theCalculator = createAndGetCalculator(tree);
		StringBuilder sb = new StringBuilder();
		sb.append("Missing predicates = ").append(theCalculator.getMissingPredicates().size());
		// TODO complete description
		return sb.toString();
	}

	
	private synchronized PastaGapFeaturesCalculator<I, S> createAndGetCalculator(TreeAndParentMap<I, S> tree) throws GapException
	{
		if (calculator!=null)
		{
			if (lastTreeIsTheGivenOne(tree))
			{
				return calculator;
			}
		}

		try
		{
			lastTree = null;
			PredicateArgumentStructureBuilder<I, S> builder = builderFactory.createBuilder(tree);
			builder.build();
			calculator = new PastaGapFeaturesCalculator<I,S>(hypothesisTree,hypothesisStructures,tree,builder.getPredicateArgumentStructures());
			calculator.calculate();
			lastTree = tree.getTree();
			return calculator;
		}
		catch (PredicateArgumentIdentificationException e)
		{
			throw new GapException("Failed to build predicate argument structure for the given tree.",e);
		}
	}
	
	private synchronized boolean lastTreeIsTheGivenOne(TreeAndParentMap<I, S> tree)
	{
		if (tree.getTree()==lastTree)
		{
			// extra check
			if (AbstractNodeUtils.treeSize(tree.getTree())==AbstractNodeUtils.treeSize(lastTree))
			{
				return true;
			}
		}
		return false;
	}


	// input
	private final PredicateArgumentStructureBuilderFactory<I, S> builderFactory;
	private final Set<PredicateArgumentStructure<I, S>> hypothesisStructures;
	private final TreeAndParentMap<I, S> hypothesisTree;
	private final LinearClassifier classifierForSearch;
	
	// internals
	private S lastTree = null;
	private PastaGapFeaturesCalculator<I, S> calculator = null;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PastaBasedGapFeaturesUpdate.class);
}
