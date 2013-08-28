package eu.excitementproject.eop.biutee.utilities;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.transformations.utilities.MeanAndStandardDeviation;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class ReasonableGuessCreator
{
	public ReasonableGuessCreator(FeatureVectorStructureOrganizer featureVectorStructure)
	{
		super();
		this.featureVectorStructure = featureVectorStructure;
	}

	public void create() throws TeEngineMlException, ClassifierException
	{
		if (featureVectorStructure==null)
			throw new TeEngineMlException("Null featureVectorStructure");
		// TODO
		// This function contains many hard-coded values.
		
		double standardDeviation=0.1;
		
		// Here, two maps are created: one is prior of negative, and one is prior of positive.
		// The mean value for each feature is a value that we guess for the feature value
		// for positive / negative instances.
		// Note that it is a guessed value for the feature value, not the feature weight.
		// So, if a transformation is supposed to occur less in positive than in negative,
		// then the prior mean in the negative map should be larger than the prior mean in the positive map.
		
		Map<Integer, MeanAndStandardDeviation> priorNegative = new LinkedHashMap<Integer, MeanAndStandardDeviation>();
		priorNegative.put(Feature.INSERT_NAMED_ENTITY.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		priorNegative.put(Feature.INSERT_CONTENT_VERB.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		priorNegative.put(Feature.INSERT_CONTENT_WORD.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		priorNegative.put(Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD.getFeatureIndex(), new MeanAndStandardDeviation(-5, standardDeviation));
		priorNegative.put(Feature.INSERT_EMPTY_WORD.getFeatureIndex(), new MeanAndStandardDeviation(-5, standardDeviation));
		priorNegative.put(Feature.INSERT_NAMED_ENTITY_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		priorNegative.put(Feature.INSERT_CONTENT_VERB_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-6, standardDeviation));
		priorNegative.put(Feature.INSERT_CONTENT_WORD_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-6, standardDeviation));
		priorNegative.put(Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-3, standardDeviation));
		priorNegative.put(Feature.MOVE_ONLY_CHANGE_RELATION_STRONG.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorNegative.put(Feature.MOVE_INTRODUCE_SURFACE_RELATION.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorNegative.put(Feature.MOVE_NODE_CHANGE_CONTEXT.getFeatureIndex(), new MeanAndStandardDeviation(-4, standardDeviation));
		priorNegative.put(Feature.MOVE_NODE_SAME_CONTEXT.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorNegative.put(Feature.MOVE_TO_ROOT_IF_PT_PLUS_OR_MINUS.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorNegative.put(Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-3, standardDeviation));
		priorNegative.put(Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS_NAMED_ENTITY.getFeatureIndex(), new MeanAndStandardDeviation(-3, standardDeviation));
		priorNegative.put(Feature.SUBSTITUTION_MULTI_WORD_REMOVE_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-3, standardDeviation));
		priorNegative.put(Feature.SUBSTITUTION_FLIP_POS.getFeatureIndex(), new MeanAndStandardDeviation(-5, standardDeviation));
		priorNegative.put(Feature.SUBSTITUTION_PARSER_ANTECEDENT.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorNegative.put(Feature.SUBSTITUTION_COREFERENCE.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorNegative.put(Feature.IS_A_COREFERENCE.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorNegative.put(Feature.CHANGE_PREDICATE_TRUTH.getFeatureIndex(), new MeanAndStandardDeviation(-15, standardDeviation));
		
		priorNegative.put(Feature.GAP_V3_MISSING_ARGUMENT.getFeatureIndex(), new MeanAndStandardDeviation(-14, standardDeviation));
		priorNegative.put(Feature.GAP_V3_MISSING_NAMED_ENTITIES.getFeatureIndex(), new MeanAndStandardDeviation(-20, standardDeviation));
		priorNegative.put(Feature.GAP_V3_WRONG_PREDICATE_MISSING_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-10, standardDeviation));
		priorNegative.put(Feature.GAP_V3_WRONG_PREDICATE.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		priorNegative.put(Feature.GAP_V3_MISSING_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-5, standardDeviation));
		priorNegative.put(Feature.GAP_V3_MISSING_WORDS_TOTALLY_NON_PREDICATES.getFeatureIndex(), new MeanAndStandardDeviation(-10, standardDeviation));
		priorNegative.put(Feature.GAP_V3_MISSING_WORDS_TOTALLY_PREDICATES.getFeatureIndex(), new MeanAndStandardDeviation(-10, standardDeviation));
		priorNegative.put(Feature.GAP_V3_PREDICATE_NO_MATCH.getFeatureIndex(), new MeanAndStandardDeviation(-10, standardDeviation));
		
		priorNegative.put(Feature.GAP_V2_MISSING_PREDICATES.getFeatureIndex(), new MeanAndStandardDeviation(-14, standardDeviation));
		priorNegative.put(Feature.GAP_V2_ARGUMENT_HEAD_NOT_CONNECTED.getFeatureIndex(), new MeanAndStandardDeviation(-5, standardDeviation));
		priorNegative.put(Feature.GAP_V2_ARGUMENT_HEAD_MISSING.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		priorNegative.put(Feature.GAP_V2_ARGUMENT_NODE_NOT_CONNECTED.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorNegative.put(Feature.GAP_V2_ARGUMENT_NODE_MISSING.getFeatureIndex(), new MeanAndStandardDeviation(-3, standardDeviation));
		
		priorNegative.put(Feature.GAP_V1_COUNT_MISSING_NODES.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
		

		Map<Integer, MeanAndStandardDeviation> priorPositive = new LinkedHashMap<Integer, MeanAndStandardDeviation>();
		priorPositive.put(Feature.INSERT_NAMED_ENTITY.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.INSERT_CONTENT_VERB.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.INSERT_CONTENT_WORD.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorPositive.put(Feature.INSERT_EMPTY_WORD.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorPositive.put(Feature.INSERT_NAMED_ENTITY_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorPositive.put(Feature.INSERT_CONTENT_VERB_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.INSERT_CONTENT_WORD_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorPositive.put(Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD_EXIST_IN_PAIR.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.MOVE_ONLY_CHANGE_RELATION_STRONG.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.MOVE_INTRODUCE_SURFACE_RELATION.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.MOVE_NODE_CHANGE_CONTEXT.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.MOVE_NODE_SAME_CONTEXT.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorPositive.put(Feature.MOVE_TO_ROOT_IF_PT_PLUS_OR_MINUS.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS_NAMED_ENTITY.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.SUBSTITUTION_MULTI_WORD_REMOVE_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.SUBSTITUTION_FLIP_POS.getFeatureIndex(), new MeanAndStandardDeviation(-2, standardDeviation));
		priorPositive.put(Feature.SUBSTITUTION_PARSER_ANTECEDENT.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.SUBSTITUTION_COREFERENCE.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.IS_A_COREFERENCE.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.CHANGE_PREDICATE_TRUTH.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		
		priorPositive.put(Feature.GAP_V3_MISSING_ARGUMENT.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_MISSING_NAMED_ENTITIES.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_WRONG_PREDICATE_MISSING_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_WRONG_PREDICATE.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_MISSING_WORDS.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_MISSING_WORDS_TOTALLY_NON_PREDICATES.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_MISSING_WORDS_TOTALLY_PREDICATES.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V3_PREDICATE_NO_MATCH.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		
		priorPositive.put(Feature.GAP_V2_MISSING_PREDICATES.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V2_ARGUMENT_HEAD_NOT_CONNECTED.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V2_ARGUMENT_HEAD_MISSING.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V2_ARGUMENT_NODE_NOT_CONNECTED.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		priorPositive.put(Feature.GAP_V2_ARGUMENT_NODE_MISSING.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));
		
		priorPositive.put(Feature.GAP_V1_COUNT_MISSING_NODES.getFeatureIndex(), new MeanAndStandardDeviation(-1, standardDeviation));

		
		
		
//		for (Feature gapFeture : Feature.getGapFeatures())
//		{
//			priorNegative.put(gapFeture.getFeatureIndex(), new MeanAndStandardDeviation(-7, standardDeviation));
//			priorPositive.put(gapFeture.getFeatureIndex(), new MeanAndStandardDeviation(0, standardDeviation));
//		}
		
		for (Feature feature : Feature.values())
		{
			if (!feature.isGlobal())
			{
				if (
						(!priorNegative.containsKey(feature.getFeatureIndex()))||
						(!priorPositive.containsKey(feature.getFeatureIndex()))
						)
				{
					throw new TeEngineMlException("Bug: the feature "+feature.name()+" is not included in prior.");
				}
			}
		}
		
		ReasonableGuessGenerator generator = 
			new ReasonableGuessGenerator(priorNegative,priorPositive,featureVectorStructure,
					BiuteeConstants.NUMBER_OF_SAMPLES_BY_WHICH_REASONABLE_GUESS_IS_TRAINED/(1+1),
					BiuteeConstants.NUMBER_OF_SAMPLES_BY_WHICH_REASONABLE_GUESS_IS_TRAINED/(1+1));
		classifier = generator.createClassifierByPrior();
		
		classifier.setFeaturesNames(featureVectorStructure.createMapOfFeatureNames());
		
		if (logger.isInfoEnabled())
		{
			logger.info("Reasonable-guess classifier description:\n"+classifier.descriptionOfTraining());
		}
	}
	
	
	
	public LinearTrainableStorableClassifier getClassifier() throws TeEngineMlException
	{
		if (null==classifier) throw new TeEngineMlException("Caller\'s bug. Classifier has not yet benn created.");
		return classifier;
	}





	private final FeatureVectorStructureOrganizer featureVectorStructure;
	private LinearTrainableStorableClassifier classifier;
	
	private static final Logger logger = Logger.getLogger(ReasonableGuessCreator.class);
}
