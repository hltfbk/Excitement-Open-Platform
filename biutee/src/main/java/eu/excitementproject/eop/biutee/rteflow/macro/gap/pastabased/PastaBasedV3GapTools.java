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
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
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
			UnigramProbabilityEstimation mleEstimation)
	{
		super(builderFactory, hypothesisStructures, hypothesisTree, classifierForSearch);
		this.mleEstimation = mleEstimation;
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
		
//		ret.put(Feature.GAP_V3_MISSING_ARGUMENT.getFeatureIndex(), (double)(-theCalculator.getCalculatedNoMatch().size()) );
//		ret.put(Feature.GAP_V3_WRONG_PREDICATE_MISSING_WORDS.getFeatureIndex(), (double)(-theCalculator.getCalculatedMatchWrongPredicateMissingWords().size()) );
//		ret.put(Feature.GAP_V3_WRONG_PREDICATE.getFeatureIndex(), (double)(-theCalculator.getCalculatedMatchWrongPredicate().size()) );
//		ret.put(Feature.GAP_V3_MISSING_NAMED_ENTITIES.getFeatureIndex(), (double)(-theCalculator.getCalculatedNoMatchNamedEntities().size()) );
//		ret.put(Feature.GAP_V3_MISSING_WORDS.getFeatureIndex(), (double)(-theCalculator.getCalculatedMatchMissingWords().size()) );
		
		updateTotallyOmittedValue(ret,theCalculator.getCalculatedTotallyOmittedHypothesisContentLemmas());
		
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
		
		return new GapDescription(sb.toString());
	}
	
	
	//////////////// PRIVATE ////////////////
	
	
	private void updateFeature(Map<Integer, Double> featureVector, Feature feature, List<PredicateAndArgument<I, S>> listMismatch, boolean contantValue) throws GapException
	{
		double addValue = 0.0;
		if ((!contantValue)&&(BiuteeConstants.USE_MLE_FOR_INSERTION_COST_AND_GAP))
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
	
	
	private void updateTotallyOmittedValue(Map<Integer, Double> featureVector, Set<String> totallyOmittedLemmas) throws GapException
	{
		double totallyOmittedValue = 0.0;
		for (String omitted : totallyOmittedLemmas)
		{
			if (BiuteeConstants.USE_MLE_FOR_INSERTION_COST_AND_GAP)
			{
				totallyOmittedValue += Math.log(mleEstimation.getEstimationFor(omitted));
			}
			else
			{
				totallyOmittedValue += (-1);
			}
		}
		if (totallyOmittedValue>0.0){throw new GapException("Bug: feature-value > 0 for feature "+Feature.GAP_V3_MISSING_WORDS_TOTALLY.name());}
		Double totallyOmittedOldValueObj = featureVector.get(Feature.GAP_V3_MISSING_WORDS_TOTALLY.getFeatureIndex());
		double totallyOmittedOldValue = ( (totallyOmittedOldValueObj!=null)?totallyOmittedOldValueObj.doubleValue():0.0 );
		featureVector.put(Feature.GAP_V3_MISSING_WORDS_TOTALLY.getFeatureIndex(), totallyOmittedOldValue+totallyOmittedValue);
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
	

	protected PastaGapFeaturesV3Calculator<I,S> constructCalculator(TreeAndParentMap<I, S> tree, Set<PredicateArgumentStructure<I, S>> textStructures) throws GapException
	{
		PastaGapFeaturesV3Calculator<I,S> ret = new PastaGapFeaturesV3Calculator<I,S>(hypothesisTree,hypothesisStructures,tree,textStructures);
		ret.calculate();
		return ret;
	}

	protected final UnigramProbabilityEstimation mleEstimation;
}
