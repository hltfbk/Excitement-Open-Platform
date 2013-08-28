package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.en.pasta.PredicateArgumentStructureBuilderFactory;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * 
 * @author Asher Stern
 * @since Aug 20, 2013
 *
 * @param <I>
 * @param <S>
 */
@NotThreadSafe
public class PastaBasedV3GapTools<I extends Info, S extends AbstractNode<I, S>> extends AbstractPastaBasedGapTools<I,S, PastaGapFeaturesV3Calculator<I,S>>
{
	public PastaBasedV3GapTools(
			PredicateArgumentStructureBuilderFactory<I, S> builderFactory,
			Set<PredicateArgumentStructure<I, S>> hypothesisStructures,
			TreeAndParentMap<I, S> hypothesisTree,
			LinearClassifier classifierForSearch,
			UnigramProbabilityEstimation mleEstimation,
			ImmutableSet<String> stopWords)
	{
		super(builderFactory, hypothesisStructures, hypothesisTree, classifierForSearch);
		this.mleEstimation = mleEstimation;
		this.stopWords = stopWords;
	}

	
	
	@Override
	public synchronized Map<Integer, Double> updateForGap(TreeAndParentMap<I, S> tree,
			Map<Integer, Double> featureVector, GapEnvironment<I, S> environment) throws GapException
	{
		Map<Integer, Double> ret = new LinkedHashMap<>();
		ret.putAll(featureVector);
		
		PastaGapFeaturesV3Calculator<I, S> theCalculator = createAndGetCalculator(tree);
		
		updateFeature(ret, Feature.GAP_V3_MISSING_ARGUMENT, theCalculator.getCalculatedNoMatch(),false);
		updateFeature(ret, Feature.GAP_V3_MISSING_NAMED_ENTITIES, theCalculator.getCalculatedNoMatchNamedEntities(),false);
		updateFeature(ret, Feature.GAP_V3_WRONG_PREDICATE_MISSING_WORDS, theCalculator.getCalculatedMatchWrongPredicateMissingWords(),true);
		updateFeature(ret, Feature.GAP_V3_WRONG_PREDICATE, theCalculator.getCalculatedMatchWrongPredicate(),true);
		updateFeature(ret, Feature.GAP_V3_MISSING_WORDS, theCalculator.getCalculatedMatchMissingWords(),true);

		updateTotallyOmittedValue(ret,theCalculator.getCalculatedTotallyOmittedHypothesisContentLemmasNonPredicates(),Feature.GAP_V3_MISSING_WORDS_TOTALLY_NON_PREDICATES);
		updateTotallyOmittedValue(ret,theCalculator.getCalculatedTotallyOmittedHypothesisContentLemmasPredicates(),Feature.GAP_V3_MISSING_WORDS_TOTALLY_PREDICATES);
		
		double predicateNoMatchValue = valueForPredicateNoMatch(theCalculator.getCalculatedPredicatesNoMatch());
		ret.put(Feature.GAP_V3_PREDICATE_NO_MATCH.getFeatureIndex(),predicateNoMatchValue);
		
		return ret;
	}
	


	@Override
	public synchronized GapDescription describeGap(TreeAndParentMap<I, S> tree,
			GapEnvironment<I, S> environment) throws GapException
	{
		PastaGapFeaturesV3Calculator<I, S> theCalculator = createAndGetCalculator(tree);
		StringBuilder sb = new StringBuilder();
		sb.append(stringOfListPaa("named-entities no match",theCalculator.getCalculatedNoMatchNamedEntities()));
		sb.append(stringOfListPaa("no match",theCalculator.getCalculatedNoMatch()));
		sb.append(stringOfListPaa("wrong-predicate & missing-words",theCalculator.getCalculatedMatchWrongPredicateMissingWords()));
		sb.append(stringOfListPaa("wrong-predicate",theCalculator.getCalculatedMatchWrongPredicate()));
		sb.append(stringOfListPaa("missing-words",theCalculator.getCalculatedMatchMissingWords()));
		
		sb.append(totallyOmittedString("Totally omitted lemmas (non-predicates): ",theCalculator.getCalculatedTotallyOmittedHypothesisContentLemmasNonPredicates()));
		sb.append(totallyOmittedString("Totally omitted lemmas (predicates): ",theCalculator.getCalculatedTotallyOmittedHypothesisContentLemmasPredicates()));
		
		sb.append(printNoMatchPredicates(theCalculator.getCalculatedPredicatesNoMatch()));
		
		return new GapDescription(sb.toString());
	}
	
	
	//////////////// PROTECTED & PRIVATE ////////////////
	
	protected PastaGapFeaturesV3Calculator<I,S> constructCalculator(TreeAndParentMap<I, S> tree, Set<PredicateArgumentStructure<I, S>> textStructures) throws GapException
	{
		PastaGapFeaturesV3Calculator<I,S> ret = new PastaGapFeaturesV3Calculator<I,S>(hypothesisTree,hypothesisStructures,tree,textStructures, stopWords);
		ret.calculate();
		return ret;
	}

	
	private void updateFeature(Map<Integer, Double> featureVector, Feature feature, List<PredicateAndArgument<I, S>> listMismatch, boolean contantValue) throws GapException
	{
		double addValue = 0.0;
		if (is((!contantValue)&&(BiuteeConstants.USE_MLE_FOR_ARGUMENTS_GAP)))
		{
			addValue = 0.0;
			for (PredicateAndArgument<I, S> mismatch : listMismatch)
			{
				String lemmaMismatch = InfoGetFields.getLemma(mismatch.getArgument().getArgument().getSemanticHead().getInfo());
				addValue += Math.log(mleEstimation.getEstimationFor(lemmaMismatch));
			}
		}
		else
		{
			addValue = (double)(-listMismatch.size());
		}
		
		if (addValue>0.0) {throw new GapException("Bug: feature-value > 0");}
		
		double currentValue = 0.0;
		Double existingValue = featureVector.get(feature.getFeatureIndex());
		if (existingValue!=null)
		{
			currentValue = existingValue.doubleValue();
		}
		
		featureVector.put(feature.getFeatureIndex(), currentValue+addValue);
	}
	
	
	private void updateTotallyOmittedValue(Map<Integer, Double> featureVector, Set<String> totallyOmittedLemmas, Feature feature) throws GapException
	{
		double totallyOmittedValue = 0.0;
		for (String omitted : totallyOmittedLemmas)
		{
			if (BiuteeConstants.USE_MLE_FOR_GAP)
			{
				totallyOmittedValue += Math.log(mleEstimation.getEstimationFor(omitted));
			}
			else
			{
				totallyOmittedValue += (-1);
			}
		}
		if (totallyOmittedValue>0.0){throw new GapException("Bug: feature-value > 0 for feature "+feature.name());}
		Double totallyOmittedOldValueObj = featureVector.get(feature.getFeatureIndex());
		double totallyOmittedOldValue = ( (totallyOmittedOldValueObj!=null)?totallyOmittedOldValueObj.doubleValue():0.0 );
		featureVector.put(feature.getFeatureIndex(), totallyOmittedOldValue+totallyOmittedValue);
	}
	
	private double valueForPredicateNoMatch(List<FlaggedPredicateArgumentStructure<I, S>> predicates) throws GapException
	{
		double value = 0.0; 
		if (BiuteeConstants.USE_MLE_FOR_GAP)
		{
			for (FlaggedPredicateArgumentStructure<I,S> predicate : predicates)
			{
				String lemma = InfoGetFields.getLemma(predicate.getPredicateArgumentStructure().getPredicate().getHead().getInfo());
				value += Math.log(mleEstimation.getEstimationFor(lemma));
			}
		}
		else
		{
			value = (double)(-predicates.size());
		}
		
		if (value>0.0) throw new GapException("Bug: feature-value for no-match predicate was calculated to a positive value.");
		
		return value;
	}
	
	private String totallyOmittedString(String prefix, Set<String> words)
	{
		if (null==words) return "";
		if (words.size()==0) return "";
		return prefix+StringUtil.joinIterableToString(words, ", ",true)+"\n";
	}
	
	private String stringOfListPaa(String prefix, Iterable<PredicateAndArgument<I, S>> paas)
	{
		if (paas.iterator().hasNext())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(prefix).append(": ");
			for (PredicateAndArgument<I, S> paa : paas)
			{
				sb.append(paaString(paa)).append(", ");
			}
			sb.append("\n");
			return sb.toString();
		}
		else
		{
			return "";
		}
	}
	
	private String paaString(PredicateAndArgument<I, S> paa)
	{
		return InfoGetFields.getLemma(paa.getArgument().getArgument().getSemanticHead().getInfo())+
				"/["+
				InfoGetFields.getLemma(paa.getPredicate().getPredicate().getHead().getInfo())+"]";
	}
	
	private String printNoMatchPredicates(List<FlaggedPredicateArgumentStructure<I, S>> predicates)
	{
		String truthValueMisMatch = listOfPredicates(predicates,true);
		String notExistMisMatch = listOfPredicates(predicates,false);
		StringBuilder sb = new StringBuilder();
		if (truthValueMisMatch.length()>0)
		{
			sb.append("Truth-value mismatch predicates: ").append(truthValueMisMatch).append("\n");
		}
		if (notExistMisMatch.length()>0)
		{
			sb.append("No match (not exist) predicates: ").append(notExistMisMatch).append("\n");
		}
		return sb.toString();
	}
	
	private String listOfPredicates(List<FlaggedPredicateArgumentStructure<I, S>> predicates, boolean onlyFor)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (FlaggedPredicateArgumentStructure<I, S> predicate : predicates)
		{
			if (predicate.isFlag()==onlyFor)
			{
				if (firstIteration){firstIteration=false;}
				else {sb.append(", ");}
				sb.append(InfoGetFields.getLemma(predicate.getPredicateArgumentStructure().getPredicate().getHead().getInfo()));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns b - used to prevent compilation warning caused by constants.
	 * @param b what will be returned.
	 * @return <code>b</code> the given paramter.
	 */
	private static final boolean is(final boolean b)
	{
		return b;
	}
	


	protected final UnigramProbabilityEstimation mleEstimation;
	private final ImmutableSet<String> stopWords;
}
