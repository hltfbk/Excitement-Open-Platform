package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.lap.biu.en.pasta.PredicateArgumentStructureBuilderFactory;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2013
 *
 * @param <I>
 * @param <S>
 */
@NotThreadSafe
public class PastaBasedV2GapTools<I extends Info, S extends AbstractNode<I, S>> extends AbstractPastaBasedGapTools<I,S, PastaGapFeaturesV2Calculator<I, S>>
{
	public PastaBasedV2GapTools(
			PredicateArgumentStructureBuilderFactory<I, S> builderFactory,
			Set<PredicateArgumentStructure<I, S>> hypothesisStructures,
			TreeAndParentMap<I, S> hypothesisTree,
			LinearClassifier classifierForSearch)
	{
		super(builderFactory,hypothesisStructures,hypothesisTree,classifierForSearch);
	}



	@Override
	public synchronized Map<Integer, Double> updateForGap(TreeAndParentMap<I, S> tree,
			Map<Integer, Double> featureVector, GapEnvironment<I, S> environment) throws GapException
	{
		Map<Integer, Double> ret = new LinkedHashMap<>();
		ret.putAll(featureVector);
		
		PastaGapFeaturesV2Calculator<I, S> theCalculator = createAndGetCalculator(tree,environment);
		ret.put(Feature.GAP_V2_MISSING_PREDICATES.getFeatureIndex(), (double)(-theCalculator.getMissingPredicates().size()));
		ret.put(Feature.GAP_V2_ARGUMENT_HEAD_NOT_CONNECTED.getFeatureIndex(), (double)(-theCalculator.getArgumentHeadNotConnected().size()));
		ret.put(Feature.GAP_V2_ARGUMENT_HEAD_MISSING.getFeatureIndex(), (double)(-theCalculator.getMissingArgument().size()));
		ret.put(Feature.GAP_V2_ARGUMENT_NODE_NOT_CONNECTED.getFeatureIndex(), (double)(-theCalculator.getLemmaNotInArgument().size()));
		ret.put(Feature.GAP_V2_ARGUMENT_NODE_MISSING.getFeatureIndex(), (double)(-theCalculator.getMissingLemmaOfArgument().size()));
		
		return ret;
	}
	

	
	@Override
	public synchronized GapDescription describeGap(TreeAndParentMap<I, S> tree, GapEnvironment<I, S> environment) throws GapException
	{
		PastaGapFeaturesV2Calculator<I, S> theCalculator = createAndGetCalculator(tree,environment);
		StringBuilder sb = new StringBuilder();
		sb.append("Missing predicates:\n");
		for (PredicateArgumentStructure<I, S> predicate : theCalculator.getMissingPredicates())
		{
			sb.append(" ").append(lemma(predicate.getPredicate().getHead())).append(",");
		}
		sb.append("\n");
		sb.append("Arguments not connected:\n");
		for (PredicateAndArgument<I, S> argument : theCalculator.getArgumentHeadNotConnected())
		{
			sb.append(" ").append(lemma(argument.getArgument().getArgument().getSemanticHead()));
			sb.append(" (for argument ").append(lemma(argument.getPredicate().getPredicate().getHead()));
			sb.append("),");
		}
		sb.append("\n");
		sb.append("Missing arguments:\n");
		for (PredicateAndArgument<I, S> argument : theCalculator.getMissingArgument())
		{
			sb.append(" ").append(lemma(argument.getArgument().getArgument().getSemanticHead()));
			sb.append(" (for argument ").append(lemma(argument.getPredicate().getPredicate().getHead()));
			sb.append("),");
		}
		sb.append("\n");
		sb.append("Lemmas not in argument:\n");
		for (PredicateAndArgumentAndNode<I, S> node : theCalculator.getLemmaNotInArgument())
		{
			sb.append(" ").append(lemma(node.getNode()));
			sb.append(" (for argument ").append(lemma(node.getArgument().getArgument().getSemanticHead()));
			sb.append("),");
		}
		sb.append("\n");
		sb.append("Missing lemmas:\n");
		for (PredicateAndArgumentAndNode<I, S> node : theCalculator.getMissingLemmaOfArgument())
		{
			sb.append(" ").append(lemma(node.getNode()));
			sb.append(" (for argument ").append(lemma(node.getArgument().getArgument().getSemanticHead()));
			sb.append("),");
		}
		sb.append("\n");
		
		return new GapDescription(sb.toString());
	}
	
	private String lemma(S node)
	{
		return InfoGetFields.getLemma(node.getInfo());
	}
	

	@Override
	protected PastaGapFeaturesV2Calculator<I, S> constructCalculator(TreeAndParentMap<I, S> tree, Set<PredicateArgumentStructure<I, S>> textStructures, List<Set<PredicateArgumentStructure<I, S>>> surroundingStructures, Set<String> wholeTextLemmas ) throws GapException
	{
		PastaGapFeaturesV2Calculator<I, S> ret = new PastaGapFeaturesV2Calculator<I,S>(hypothesisTree,hypothesisStructures,tree,textStructures);
		ret.calculate();
		return ret;
	}

	
//	private synchronized PastaGapFeaturesV2Calculator<I, S> createAndGetCalculator(TreeAndParentMap<I, S> tree) throws GapException
//	{
//		if (calculator!=null)
//		{
//			if (lastTreeIsTheGivenOne(tree))
//			{
//				return calculator;
//			}
//		}
//
//		try
//		{
//			lastTree = null;
//			PredicateArgumentStructureBuilder<I, S> builder = builderFactory.createBuilder(tree);
//			builder.build();
//			calculator = new PastaGapFeaturesV2Calculator<I,S>(hypothesisTree,hypothesisStructures,tree,builder.getPredicateArgumentStructures());
//			calculator.calculate();
//			lastTree = tree.getTree();
//			return calculator;
//		}
//		catch (PredicateArgumentIdentificationException e)
//		{
//			throw new GapException("Failed to build predicate argument structure for the given tree.",e);
//		}
//	}
	


	
	

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PastaBasedV2GapTools.class);
}
