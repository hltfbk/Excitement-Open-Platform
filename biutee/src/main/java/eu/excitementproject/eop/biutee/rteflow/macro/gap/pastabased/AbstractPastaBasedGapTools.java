package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescriptionGenerator;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapFeaturesUpdate;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapHeuristicMeasure;
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
 * @since Aug 20, 2013
 *
 * @param <I>
 * @param <S>
 */
public abstract class AbstractPastaBasedGapTools<I extends Info, S extends AbstractNode<I, S>, C> implements GapFeaturesUpdate<I, S>, GapHeuristicMeasure<I,S>, GapDescriptionGenerator<I,S>
{
	public AbstractPastaBasedGapTools(
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
	
	
	protected abstract C constructCalculator(TreeAndParentMap<I, S> tree, Set<PredicateArgumentStructure<I, S>> textStructures) throws GapException;

	
	protected synchronized boolean lastTreeIsTheGivenOne(TreeAndParentMap<I, S> tree)
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
	
	protected synchronized C createAndGetCalculator(TreeAndParentMap<I, S> tree) throws GapException
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
			calculator = constructCalculator(tree,builder.getPredicateArgumentStructures());
			//calculator.calculate(); -- called in constructCalculator
			lastTree = tree.getTree();
			return calculator;
		}
		catch (PredicateArgumentIdentificationException e)
		{
			throw new GapException("Failed to build predicate argument structure for the given tree.",e);
		}
	}
	



	
	// input
	protected final PredicateArgumentStructureBuilderFactory<I, S> builderFactory;
	protected final Set<PredicateArgumentStructure<I, S>> hypothesisStructures;
	protected final TreeAndParentMap<I, S> hypothesisTree;
	protected final LinearClassifier classifierForSearch;
	
	// internals
	protected S lastTree = null;
	private C calculator = null;

}
