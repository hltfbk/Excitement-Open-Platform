package eu.excitementproject.eop.biutee.rteflow.systems;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TEST_SERIALIZED_SAMPLES_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.LogisticRegressionClassifier;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.ParametersExpanderClassifier;
import eu.excitementproject.eop.biutee.classifiers.scaling.LinearScalingTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.GenericPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairProcessResult;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairResult;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTESerializedPairsReader;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ResultWithScores;
import eu.excitementproject.eop.biutee.script.RuleBasesAndPluginsContainer;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.ReasonableGuessCreator;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamples;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamplesUtils;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;


/**
 * 
 * A collection of utilities used by RTE systems.
 * 
 * TODO (comment by Asher Stern)
 * This class looks like the back-yard of the system. Should be reorganized!
 * 
 * 
 * @author Asher Stern
 * @since Feb 18, 2011
 *
 */
public class RTESystemsUtils
{
	
	public static TrainableClassifier getDeepRealClassifier(TrainableClassifier classifier)
	{
		// TODO get rid of this RTTI

		logger.info("getDeepRealClassifier");
		TrainableClassifier ret = classifier;
		do
		{
			classifier = ret;
			if (classifier instanceof LinearScalingTrainableStorableClassifier)
			{
				logger.info("classifier instanceof ScalingClassifier");
				ret = ((LinearScalingTrainableStorableClassifier)classifier).getRealClassifier();
			}
			else if (classifier instanceof ParametersExpanderClassifier)
			{
				logger.info("classifier instanceof ParametersExpanderClassifier");
				ret = ((ParametersExpanderClassifier)classifier).getRealClassifier();
			}
		}while(ret != classifier);
		
		return ret;
	}

	public static void normalizeClassifierForSearch(TrainableClassifier classifier) throws TeEngineMlException, ClassifierException
	{
		normalizeClassifierForSearch(classifier,false);
	}
	public static void normalizeClassifierForSearch(TrainableClassifier classifier, boolean ignoreConstantsFlag) throws TeEngineMlException, ClassifierException
	{
		if ( (!BiuteeConstants.RESTRICT_SEARCH_CLASSIFIER_DURING_TRAINING) || (ignoreConstantsFlag) )
		{
			logger.info("Normaliaing classifier for search");
			TrainableClassifier innerClassifier = getDeepRealClassifier(classifier);

			// TODO get rid of this RTTI
			if (innerClassifier instanceof LogisticRegressionClassifier)
			{
				LogisticRegressionClassifier lrClassifier = (LogisticRegressionClassifier) innerClassifier;
				lrClassifier.setToZeroNegativeParametersBut(new LinkedHashSet<Integer>());
				lrClassifier.increaseAllButConstantByBut(BiuteeConstants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER, new LinkedHashSet<Integer>());


				// These features do not represent operations
				//			Set<Feature> doNotChangeFeatures = Feature.getGlobalFeatures();
				//			Set<Integer> doNotChange = new HashSet<Integer>();
				//			for (Feature feature : doNotChangeFeatures)
				//			{
				//				doNotChange.add(feature.getFeatureIndex());
				//			}
				//			doNotChange.add(Feature.HYPOTHESIS_MAIN_PREDICATE_IS_VERB.getFeatureIndex());
				//			doNotChange.add(Feature.HYPOTHESIS_MAIN_PREDICATE_IS_NOT_VERB.getFeatureIndex());
				//			doNotChange.add(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex());
				//			lrClassifier.setToZeroNegativeParametersBut(doNotChange);
				//			
				//			lrClassifier.increaseAllButConstantByBut(Constants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER, doNotChange);
			}
			else
				throw new TeEngineMlException("Inner classifier is not LogisticRegressionClassifier");
		}
		else
		{
			logger.info("The system does not make noramlization to classifier for search, since it is already restricted.");
		}
	}

	public static LinearTrainableStorableClassifier reasonableGuessClassifier(FeatureVectorStructureOrganizer featureVectorStructure) throws ClassifierException, OperationException, TeEngineMlException
	{
		ReasonableGuessCreator reasonableGuessCreator = new ReasonableGuessCreator(featureVectorStructure);
		reasonableGuessCreator.create();
		return reasonableGuessCreator.getClassifier();
	}
	
	@Deprecated
	public static LinearTrainableStorableClassifier reasonableGuessClassifierOld(RuleBasesAndPluginsContainer<?,?> ruleBasesNames) throws ClassifierException, OperationException, TeEngineMlException
	{
		LogisticRegressionClassifier ret = new LogisticRegressionClassifier();
		Map<Integer,Double> nonConstantparameters = new LinkedHashMap<Integer, Double>();
		for (Feature feature : Feature.values())
		{
			nonConstantparameters.put(feature.getFeatureIndex(),0.0);
		}
//		nonConstantparameters.put(Feature.HYPOTHESIS_MAIN_PREDICATE_IS_VERB.getFeatureIndex(),0.0);
//		nonConstantparameters.put(Feature.HYPOTHESIS_MAIN_PREDICATE_IS_NOT_VERB.getFeatureIndex(),0.0);
//		nonConstantparameters.put(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex(),0.0);
		nonConstantparameters.put(Feature.INSERT_NAMED_ENTITY.getFeatureIndex(),16.0);
//		nonConstantparameters.put(Feature.INSERT_NUMBER.getFeatureIndex(),16.0);
		nonConstantparameters.put(Feature.INSERT_CONTENT_VERB.getFeatureIndex(),16.0);
		nonConstantparameters.put(Feature.INSERT_CONTENT_WORD.getFeatureIndex(),12.0);
		nonConstantparameters.put(Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD.getFeatureIndex(),3.0);
		nonConstantparameters.put(Feature.INSERT_EMPTY_WORD.getFeatureIndex(),0.0);
		nonConstantparameters.put(Feature.INSERT_NAMED_ENTITY_EXIST_IN_PAIR.getFeatureIndex(),12.0);
//		nonConstantparameters.put(Feature.INSERT_NUMBER_EXIST_IN_PAIR.getFeatureIndex(),12.0);
		nonConstantparameters.put(Feature.INSERT_CONTENT_VERB_EXIST_IN_PAIR.getFeatureIndex(),12.0);
		nonConstantparameters.put(Feature.INSERT_CONTENT_WORD_EXIST_IN_PAIR.getFeatureIndex(),8.0);
//		nonConstantparameters.put(Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD_EXIST_IN_PAIR.getFeatureIndex(),2.0);
//		nonConstantparameters.put(Feature.MOVE_CROSS_CONTENT_VERB.getFeatureIndex(),3.0);
		nonConstantparameters.put(Feature.MOVE_ONLY_CHANGE_RELATION_STRONG.getFeatureIndex(),2.0);
//		nonConstantparameters.put(Feature.MOVE_ONLY_CHANGE_RELATION_WEAK.getFeatureIndex(),1.0);
//		nonConstantparameters.put(Feature.MOVE_IN_VERB_TREE_CHANGE_RELATION_STRONG.getFeatureIndex(),2.0);
		nonConstantparameters.put(Feature.MOVE_INTRODUCE_SURFACE_RELATION.getFeatureIndex(),0.0);
//		nonConstantparameters.put(Feature.MOVE_CONNECT_TO_EMPTY_NODE.getFeatureIndex(),0.0);
		nonConstantparameters.put(Feature.MOVE_NODE_CHANGE_CONTEXT.getFeatureIndex(),2.0);
		nonConstantparameters.put(Feature.MOVE_NODE_SAME_CONTEXT.getFeatureIndex(),1.0);
		nonConstantparameters.put(Feature.SUBSTITUTION_MULTI_WORD_REMOVE_WORDS.getFeatureIndex(),0.0);
		nonConstantparameters.put(Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS.getFeatureIndex(),12.0);
		nonConstantparameters.put(Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS_NAMED_ENTITY.getFeatureIndex(),12.0);
		nonConstantparameters.put(Feature.SUBSTITUTION_FLIP_POS.getFeatureIndex(),1.0);
		nonConstantparameters.put(Feature.SUBSTITUTION_PARSER_ANTECEDENT.getFeatureIndex(),0.0);
		nonConstantparameters.put(Feature.SUBSTITUTION_COREFERENCE.getFeatureIndex(),0.0);

		int start = Feature.values()[0].getFeatureIndex();
		for (Feature feature : Feature.values())
		{
			if (feature.getFeatureIndex()>start)
				start = feature.getFeatureIndex();
		}
		start += 1;
		for (int index=0;index<ruleBasesNames.getRuleBasesNames().size();++index)
		{
			nonConstantparameters.put(start+index, 0.0);
		}
		
		



		//double constantFeatureParameter = 62.0;
		double constantFeatureParameter = 0.0;

		ret.setReasonableGuess(constantFeatureParameter, nonConstantparameters);
		ret.setFeaturesNames( ClassifierUtils.extendFeatureNames(Feature.toMapOfNames(), ruleBasesNames.getRuleBasesNames()));
		
		normalizeClassifierForSearch(ret);
		
		if (logger.isInfoEnabled())
			logger.info("RTEPairsTrainerUtils: Initial classifier description:\n"+ret.descriptionOfTraining());
		
		return ret;
		
	}
	
//	public static LinearClassifier reasonableGuessClassifier(ConfigurationFile configurationFile, PluginRegistry pluginRegistry) throws ClassifierException, OperationException, TeEngineMlException
//	{
//		LinearClassifier ret = null;
//		OperationsScript<?, ?> script = new ScriptFactory(configurationFile,pluginRegistry).getDefaultScript();
//		script.init();
//		try
//		{
//			ret = reasonableGuessClassifier(script.getRuleBasesNames());
//		}
//		finally
//		{
//			script.cleanUp();
//		}
//		return ret;
//	}
	
	

//	public static Classifier newClassiferForPredictionsForTest(ConfigurationFile configurationFile, LinkedHashSet<String> ruleBasesNames) throws ConfigurationException, FileNotFoundException, TeEngineMlException, IOException, ClassNotFoundException, ClassifierException
//	{
//		ConfigurationParams testParams = configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
//		return newClassiferForPredictionsForTest(testParams,ruleBasesNames);
//	}

	public static Classifier createOrLoadClassiferForPredictionsForTest(ConfigurationParams testParams, FeatureVectorStructureOrganizer featureVectorStructure) throws ConfigurationException, FileNotFoundException, TeEngineMlException, IOException, ClassNotFoundException, ClassifierException
	{
		if (testParams.containsKey(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL))
		{
			File modelFile = testParams.getFile(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
			logger.info("Loading classifier for predictions for test from file: "+modelFile.getPath());
			Classifier ret =  SafeClassifiersIO.load(featureVectorStructure, modelFile);
			ret.setFeaturesNames(featureVectorStructure.createMapOfFeatureNames());
			return ret;
		}
		else
		{
			try
			{
				return newClassiferForPredictionsForTest(testParams,featureVectorStructure);
			}
			catch(ConfigurationException e)
			{
				throw new ConfigurationException("Could not find parameter \""+ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL+
						"\", so tried other parameters, but failed. Please see also nested exception",e);

			}
		}
	}
	
	public static Classifier newClassiferForPredictionsForTest(ConfigurationParams testParams, FeatureVectorStructureOrganizer featureVectorStructure) throws ConfigurationException, FileNotFoundException, TeEngineMlException, IOException, ClassNotFoundException, ClassifierException
	{
		File samplesSerFile = testParams.getFile(RTE_TEST_SERIALIZED_SAMPLES_NAME);
		SafeSamples safeSamples = SafeSamplesUtils.load(samplesSerFile, featureVectorStructure);
		TrainableClassifier ret = new ClassifierFactory().getDefaultClassifier();
		ret.train(safeSamples.getSamples());
		
		//ret.setFeaturesNames(featureVectorStructure.createMapOfFeatureNames());
		ret.setFeaturesNames(featureVectorStructure.createMapOfFeatureNames());
		return ret;
	}

	
//	public static LinearClassifier searchClassifierForTest(ConfigurationFile configurationFile, LinkedHashSet<String> ruleBasesNames) throws ConfigurationException, OperationException, ClassifierException, IOException, ClassNotFoundException, TeEngineMlException
//	{
//		ConfigurationParams testParams = configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
//		return searchClassifierForTest(configurationFile,testParams,ruleBasesNames);
//	}
	
	public static LinearClassifier createOrLoadSearchClassifierForTest(ConfigurationParams testParams, FeatureVectorStructureOrganizer featureVectorStructure) throws OperationException, ClassifierException, ClassNotFoundException, TeEngineMlException, ConfigurationException, IOException
	{
		if (testParams.containsKey(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL))
		{
			File modelFile = testParams.getFile(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL);
			logger.info("Loading classifier for search for test from file: "+modelFile.getPath());
			LinearClassifier ret =  SafeClassifiersIO.loadLinearClassifier(featureVectorStructure, modelFile);
			ret.setFeaturesNames(featureVectorStructure.createMapOfFeatureNames());
			return ret;
		}
		else
		{
			try
			{
				return searchClassifierForTest(testParams,featureVectorStructure);
			}
			catch(ConfigurationException e)
			{
				throw new ConfigurationException("Could not find parameter \""+ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL+
						"\", so tried other parameters, but failed. Please see also nested exception",e);
			}
		}
		
	}
	
	public static LinearClassifier searchClassifierForTest(ConfigurationParams testParams, FeatureVectorStructureOrganizer featureVectorStructure) throws ConfigurationException, OperationException, ClassifierException, IOException, ClassNotFoundException, TeEngineMlException
	{
		LinearTrainableStorableClassifier ret = null;
		boolean useReasonableGuess = false;
		if (testParams.containsKey(RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS))
		{
			useReasonableGuess = testParams.getBoolean(RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS);
		}

		if (useReasonableGuess)
		{
			ret = reasonableGuessClassifier(featureVectorStructure);
		}
		else
		{
			ret = new ClassifierFactory().getDefaultClassifierForSearch();
			File fileSerializedSamples = testParams.getFile(RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER);
			SafeSamples safeSamples = SafeSamplesUtils.load(fileSerializedSamples, featureVectorStructure);
			ret.train(safeSamples.getSamples());
			normalizeClassifierForSearch(ret);
		}
		// ret.setFeaturesNames( ClassifierUtils.extendFeatureNames(Feature.toMapOfNames(), ruleBasesNames));
		ret.setFeaturesNames(featureVectorStructure.createMapOfFeatureNames());
		
		return ret;
	}
	
	
	public static List<PairData> readPairsData(ConfigurationParams trainParams) throws ConfigurationException, FileNotFoundException, IOException, ClassNotFoundException
	{
		//ConfigurationParams trainParams = configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		String serializationFileName = trainParams.get(RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME);
		return readSingleFilePairsData(serializationFileName);
	}
	
	public static List<PairData> readSingleFilePairsData(String serializationFileName) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		RTESerializedPairsReader reader = new RTESerializedPairsReader(serializationFileName);
		reader.read();
		List<PairData> pairsData = reader.getPairsData();
		return pairsData;
	}

	
//	public static String getLemmatizerRulesFileName(ConfigurationFile configurationFile) throws ConfigurationException
//	{
//		ConfigurationParams prototype1Params = configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
//		return prototype1Params.getFile(RTE_ENGINE_GATE_LEMMATIZER_RULES_FILE).getAbsolutePath();
//	}
	
//	protected Map<PairData,Boolean> currentClassification(
//			Map<TreeAndFeatureVector, PairData> bestTreeMapToPairData,
//			Classifier classifier) throws ClassifierException, TeEngineMlException
//	{
//		Map<PairData,Boolean> ret = new LinkedHashMap<PairData, Boolean>();
//		for (TreeAndFeatureVector tree : bestTreeMapToPairData.keySet())
//		{
//			boolean booleanClassification = ClassifierUtils.classifierResultToBoolean(classifier.classify(tree.getFeatureVector()));
//			if (ret.containsKey(bestTreeMapToPairData.get(tree))) throw new TeEngineMlException("Same pair exists twice.");
//			ret.put(bestTreeMapToPairData.get(tree), booleanClassification);
//		}
//		return ret;
//	}
	
	// Used by training
	public static LinearTrainableStorableClassifier createClassifierForSearch(FeatureVectorStructureOrganizer featureVectorStructure, Vector<LabeledSample> samples) throws ClassifierException, TeEngineMlException
	{
		LinearTrainableStorableClassifier classifierForSearch = new ClassifierFactory().getDefaultClassifierForSearch();
		classifierForSearch.setFeaturesNames( featureVectorStructure.createMapOfFeatureNames() );
		classifierForSearch.train(samples);
		RTESystemsUtils.normalizeClassifierForSearch(classifierForSearch);
		logger.info("training classifierForSearch done.");
		logger.info("classifierForSearch description:\n"+classifierForSearch.descriptionOfTraining());
		
		return classifierForSearch;
	}
	
	// Used by training
	public static TrainableStorableClassifier createClassifierForPredictions(FeatureVectorStructureOrganizer featureVectorStructure, Vector<LabeledSample> samples) throws ClassifierException, TeEngineMlException
	{
		TrainableStorableClassifier classifierForPredictions = new ClassifierFactory().getDefaultClassifier();
		classifierForPredictions.setFeaturesNames( featureVectorStructure.createMapOfFeatureNames() );
		classifierForPredictions.train(samples);
		logger.info("training classifierForPredictions done.");
		logger.info("classifierForPredictions description:\n"+classifierForPredictions.descriptionOfTraining());
		
		return classifierForPredictions;

	}
	
	
	public static LinearTrainableStorableClassifier createF1Classifier(FeatureVectorStructureOrganizer featureVectorStructure, Vector<LabeledSample> samples) throws ClassifierException, TeEngineMlException
	{
		LinearTrainableStorableClassifier classifier = new ClassifierFactory().getF1Classifier();
		classifier.setFeaturesNames( featureVectorStructure.createMapOfFeatureNames() );
		
		logger.info("Training classifier...");
		classifier.train(samples);
		logger.info("Training classifier done.");

		return classifier;
	}
	
	
	

	public static void printIterationSummary(LinkedHashMap<ExtendedPairData, PairProcessResult> results, Vector<LabeledSample> samples, TrainableClassifier classifier) throws ClassifierException
	{
		StringBuffer samplesSb = new StringBuffer();
		samplesSb.append("Samples:\n");
		for (ExtendedPairData pair : results.keySet())
		{
			PairProcessResult result = results.get(pair);
			LabeledSample sample = result.getLabeledSample();
			Integer id = result.getPairData().getPair().getId();
			if (pair.getDatasetName()!=null)
			{
				samplesSb.append(pair.getDatasetName()).append(" - ");
			}
			samplesSb.append(String.format("%-3d", id));
			samplesSb.append(": ");
			samplesSb.append(ClassifierUtils.printLabeledSample(sample));
			samplesSb.append("\n");
		}
		logger.info(samplesSb.toString());
		samplesSb=null;
		
		logger.info("Printing samples as SVM LIGHT format:");
		ClassifierUtils.printSamplesAsSvmLightInput(samples, logger);
		
		logger.info(StringUtil.generateStringOfCharacter('-', 100));
		Vector<LabeledSample> normalizedSamples = new Vector<LabeledSample>(samples.size());
		for (LabeledSample sample : samples)
		{
			normalizedSamples.add(classifier.getNormalizedSample(sample));
		}
		logger.info("Printing normalized samples as SVM LIGHT format:");
		ClassifierUtils.printSamplesAsSvmLightInput(normalizedSamples, logger);
		

		
		logger.info("Avarage values of features:\n"+ClassifierUtils.printAvarages(samples));
		logger.info("\n");
		
		
		logger.info("Trees history:");
		for (ExtendedPairData pair : results.keySet())
		{
			PairProcessResult result = results.get(pair);
			LabeledSample sample = result.getLabeledSample();
			Integer id = result.getPairData().getPair().getId();
			boolean realClassification = result.getPairData().getPair().getBooleanClassificationType().booleanValue();
			
			double classificationValue = classifier.classify(result.getFeatureVector());
			boolean classifierClassification = ClassifierUtils.classifierResultToBoolean(classificationValue);
			String correct = (realClassification==classifierClassification)?"V":"X";
			logger.info(id+":"+correct+". Real: "+realClassification+". Classifier: "+classifierClassification+String.format(" %-2.6f", classificationValue));
			String sentence = result.getOriginalSentence();
			String hypothesis = result.getPairData().getPair().getHypothesis();
			logger.info("sentence: "+sentence);
			logger.info("hypothesis: "+hypothesis);
			logger.info(ClassifierUtils.printLabeledSample(sample));
			for (Specification specification : result.getHistory().getSpecifications())
			{
				logger.info(specification.toString());
			}
			logger.info("\n");
		}
		
		logger.info("(Again) classifier description:\n"+classifier.descriptionOfTraining());
	}
	
//	public void printIterationSummary(Vector<LabeledSample> samples,
//			Vector<TreeAndFeatureVector> bestTrees,
//			Map<TreeAndFeatureVector, TreeHistory> bestTreesHistoryMap,
//			Map<TreeAndFeatureVector, String> bestTreesmapTreeToSentence,
//			Map<TreeAndFeatureVector, PairData> bestTreeMapToPairData,
//			Classifier classifier) throws ClassifierException, TeEngineMlException
//	{
//		StringBuffer samplesSb = new StringBuffer();
//		samplesSb.append("Samples:\n");
//		Iterator<TreeAndFeatureVector> bestTreesIterator = bestTrees.iterator();
//		for (LabeledSample sample : samples)
//		{
//			Integer id = bestTreeMapToPairData.get(bestTreesIterator.next()).getPair().getId();
//			samplesSb.append(String.format("%-3d", id));
//			samplesSb.append(": ");
//			samplesSb.append(ClassifierUtils.printLabeledSample(sample));
//			samplesSb.append("\n");
//		}
//		logger.info(samplesSb.toString());
//		samplesSb=null;
//		
//		logger.info("Printing samples as SVM LIGHT format:");
//		ClassifierUtils.printSamplesAsSvmLightInput(samples, logger);
//		
//		logger.info(StringUtil.generateStringOfCharacter('-', 100));
//		Vector<LabeledSample> normalizedSamples = new Vector<LabeledSample>(samples.size());
//		for (LabeledSample sample : samples)
//		{
//			normalizedSamples.add(classifier.getNormalizedSample(sample));
//		}
//		logger.info("Printing normalized samples as SVM LIGHT format:");
//		ClassifierUtils.printSamplesAsSvmLightInput(normalizedSamples, logger);
//		
//
//		
//		logger.info("Avarage values of features:\n"+ClassifierUtils.printAvarages(samples));
//		
//		Map<PairData,Boolean> classificationByClassifier = currentClassification(bestTreeMapToPairData,classifier);
//		
//		
//		logger.info("\n");
//		
//		
//		logger.info("Trees history:");
//		Iterator<LabeledSample> samplesIterator = samples.iterator();
//		for (TreeAndFeatureVector tree : bestTrees)
//		{
//			LabeledSample sample = samplesIterator.next();
//			Integer id = bestTreeMapToPairData.get(tree).getPair().getId();
//			boolean realClassification = bestTreeMapToPairData.get(tree).getPair().getBooleanClassificationType();
//			boolean classifierClassification = classificationByClassifier.get(bestTreeMapToPairData.get(tree));
//			double classificationValue = classifier.classify(tree.getFeatureVector());
//			String correct = (realClassification==classifierClassification)?"V":"X";
//			logger.info(id+":"+correct+". Real: "+realClassification+". Classifier: "+classifierClassification+String.format(" %-2.6f", classificationValue));
//			String sentence = bestTreesmapTreeToSentence.get(tree);
//			String hypothesis = bestTreeMapToPairData.get(tree).getPair().getHypothesis();
//			logger.info("sentence: "+sentence);
//			logger.info("hypothesis: "+hypothesis);
//			logger.info(ClassifierUtils.printLabeledSample(sample));
//			for (Specification specification : bestTreesHistoryMap.get(tree).getSpecifications())
//			{
//				logger.info(specification.toString());
//			}
//			logger.info("\n");
//		}
//		
//		logger.info("(Again) classifier description:\n"+classifier.descriptionOfTraining());
//	}
	
	public static void saveSamplesInSerFile(Vector<LabeledSample> samples, int mainLoopIterationIndex, String pathToStoreSamples, FeatureVectorStructureOrganizer  featureVectorStructure) throws TeEngineMlException, FileNotFoundException, IOException
	{
		File samplesSerFile = null;
		if (null == pathToStoreSamples)
		{
			samplesSerFile = new File(BiuteeConstants.LABELED_SAMPLES_FILE_PREFIX+String.valueOf(mainLoopIterationIndex)+BiuteeConstants.LABELED_SAMPLES_FILE_POSTFIX);
		}
		else
		{
			samplesSerFile = new File(pathToStoreSamples+String.valueOf(mainLoopIterationIndex)+BiuteeConstants.LABELED_SAMPLES_FILE_POSTFIX);
		}
			
		logger.info("Writing samples to: "+samplesSerFile.getAbsolutePath());
		SafeSamplesUtils.store(samplesSerFile, SafeSamplesUtils.create(samples, featureVectorStructure));
		ExperimentManager.getInstance().register(samplesSerFile);
	}

//	public static void saveSamplesInSerFile(Vector<LabeledSample> samples, int mainLoopIterationIndex) throws IOException
//	{
//		File samplesSerFile = new File(Constants.LABELED_SAMPLES_FILE_PREFIX+String.valueOf(mainLoopIterationIndex)+Constants.LABELED_SAMPLES_FILE_POSTFIX);
//		logger.info("Writing samples to: "+samplesSerFile.getAbsolutePath());
//		ObjectOutputStream samplesSerStream = new ObjectOutputStream(new FileOutputStream(samplesSerFile));
//		try
//		{
//			samplesSerStream.writeObject(samples);
//		}
//		finally
//		{
//			if (samplesSerStream!=null)
//				samplesSerStream.close();
//		}
//
//	}

	
	public static <T extends GenericPairData<?, ?>> List<T>[] splitPairsForThreads(List<T> pairsData, int numberOfThreads)
	{
		int numberOfPairsPerThread = pairsData.size()/numberOfThreads;
		if (0>=numberOfPairsPerThread) {numberOfPairsPerThread=1;}
		@SuppressWarnings("unchecked")
		List<T>[] ret = (List<T>[]) new List[numberOfThreads];
		for (int index=0;index<ret.length;++index)
		{
			ret[index] = new LinkedList<T>();
		}
		
		int indexPairs=0;
		int indexThreads = 0;
		for (T pairData : pairsData)
		{
			
			if ( (indexPairs!=0)&&(0==(indexPairs%numberOfPairsPerThread)))
			{
				if (indexThreads<(numberOfThreads-1))
					++indexThreads;
			}
			ret[indexThreads].add(pairData);
			
			++indexPairs;
		}
		
		
		return ret;
	}
	
//	public static UnigramProbabilityEstimation getUnigramProbabilityEstimation(ConfigurationFile configurationFile) throws TeEngineMlException
//	{
//		UnigramProbabilityEstimation ret = null;
//		try
//		{
//			ConfigurationParams enginemlParams = configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
//			ret = getUnigramProbabilityEstimation(enginemlParams);
//		}
//		catch (ConfigurationException e)
//		{
//			throw new TeEngineMlException("Could not load UnigramProbabilityEstimation",e);
//		}
//		
//		
//		return ret;
//	}
	
	public static UnigramProbabilityEstimation getUnigramProbabilityEstimation(ConfigurationParams enginemlParams) throws TeEngineMlException
	{
		return SystemUtils.getUnigramProbabilityEstimation(enginemlParams);
	}

	
	
	
	public static void printAccuraciesOfClassifiers(ConfigurationFile configurationFile, Vector<LabeledSample> samples, FeatureVectorStructureOrganizer featureVectorStructure) throws ConfigurationException, FileNotFoundException, IOException, ClassNotFoundException, ClassifierException, TeEngineMlException
	{
		logger.info(StringUtil.generateStringOfCharacter('=', 45));
		logger.info("printAccuraciesOfClassifiers");
		ConfigurationParams testParams = configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		File serFile = testParams.getFile(RTE_TEST_SERIALIZED_SAMPLES_NAME);
		SafeSamples safeSamplesTraining = SafeSamplesUtils.load(serFile, featureVectorStructure);
		Vector<LabeledSample> trainingSamples = safeSamplesTraining.getSamples();

		logger.info("LogisticRegressionClassifier...");
		LogisticRegressionClassifier lrClassifier = new LogisticRegressionClassifier(ClassifierFactory.LOGISTIC_REGRESSION_LEARNING_RATE, 0.0);
		lrClassifier.train(trainingSamples);
		logger.info(String.format("Accuracy = %.5f",ClassifierUtils.accuracyOf(lrClassifier, samples)));

		logger.info("LogisticRegressionClassifier with set to zero negative weights...");
		lrClassifier = new LogisticRegressionClassifier(ClassifierFactory.LOGISTIC_REGRESSION_LEARNING_RATE, 0.0);
		lrClassifier.train(trainingSamples);
		lrClassifier.setToZeroNegativeParametersBut(new LinkedHashSet<Integer>());
		logger.info(String.format("Accuracy = %.5f",ClassifierUtils.accuracyOf(lrClassifier, samples)));

		logger.info("ScalingClassifier wrapping LogisticRegressionClassifier...");
		lrClassifier = new LogisticRegressionClassifier(ClassifierFactory.LOGISTIC_REGRESSION_LEARNING_RATE, 0.0);
		LinearScalingTrainableStorableClassifier scClassifier = new  LinearScalingTrainableStorableClassifier(lrClassifier);
		scClassifier.train(trainingSamples);
		logger.info(String.format("Accuracy = %.5f",ClassifierUtils.accuracyOf(scClassifier, samples)));

		logger.info("ScalingClassifier wrapping LogisticRegressionClassifier with set to zero negative weights...");
		lrClassifier = new LogisticRegressionClassifier(ClassifierFactory.LOGISTIC_REGRESSION_LEARNING_RATE, 0.0);
		scClassifier = new LinearScalingTrainableStorableClassifier(lrClassifier);
		scClassifier.train(trainingSamples);
		lrClassifier.setToZeroNegativeParametersBut(new LinkedHashSet<Integer>());
		logger.info(String.format("Accuracy = %.5f",ClassifierUtils.accuracyOf(scClassifier, samples)));

		logger.info("ScalingClassifier wrapping LogisticRegressionClassifier with searhc normalization...");
		lrClassifier = new LogisticRegressionClassifier(ClassifierFactory.LOGISTIC_REGRESSION_LEARNING_RATE, 0.0);
		scClassifier = new LinearScalingTrainableStorableClassifier(lrClassifier);
		scClassifier.train(trainingSamples);
		normalizeClassifierForSearch(scClassifier,true);
		logger.info(String.format("Accuracy = %.5f",ClassifierUtils.accuracyOf(scClassifier, samples)));


		logger.info("Classifier for search:");
		TrainableClassifier classifierForSearch = new ClassifierFactory().getDefaultClassifierForSearch();
		classifierForSearch.train(trainingSamples);
		RTESystemsUtils.normalizeClassifierForSearch(classifierForSearch);
		logger.info(String.format("Accuracy = %.5f",ClassifierUtils.accuracyOf(classifierForSearch, samples)));


		logger.info(StringUtil.generateStringOfCharacter('=', 45));
	}
	
	
	public static void setParserMode(ConfigurationParams params) throws ConfigurationException, TeEngineMlException
	{
		SystemUtils.setParserMode(params);
	}
	

//	public static void setParserMode(ConfigurationFile configurationFile) throws ConfigurationException, TeEngineMlException
//	{
//		setParserMode(configurationFile.getModuleConfiguration(RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME));
//	}
	
	public static Set<String> getLexicalRuleBasesForMultiWords(ConfigurationFile configurationFile) throws ConfigurationException
	{
		return SystemUtils.getLexicalRuleBasesForMultiWords(configurationFile);
	}
	
	public static Set<Integer> getGlobalFeatureIndexes()
	{
		Set<Feature> globalFeatures = Feature.getGlobalFeatures();
		Set<Integer> globalFeaturesIndexes = new LinkedHashSet<Integer>();
		for (Feature globalFeature : globalFeatures)
		{
			globalFeaturesIndexes.add(globalFeature.getFeatureIndex());
		}
		return globalFeaturesIndexes;
	}
	
	public static <T> void saveInSerFile(String filename, T object) throws FileNotFoundException, IOException
	{
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(filename)));
		try
		{
			output.writeObject(object);
		}
		finally
		{
			output.close();
		}
	}
	
	public static <T> T loadFromSerFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(filename)));
		try
		{
			@SuppressWarnings("unchecked")
			T ret = (T) input.readObject();
			return ret;
		}
		finally
		{
			input.close();
		}
	}
	
	public static void savePairResultsInSerFile(LinkedHashMap<ExtendedPairData, PairProcessResult> allPairsResults, int iterationIndex) throws FileNotFoundException, IOException
	{
		String filename = BiuteeConstants.RTE_PAIRS_OUTPUT_RESULTS_FILE_PREFIX+iterationIndex+BiuteeConstants.RTE_PAIRS_OUTPUT_RESULTS_FILE_POSTFIX;
		logger.info("Writing \""+filename+"\"...");
		saveInSerFile(filename, allPairsResults);
		ExperimentManager.getInstance().register(new File(filename));
		logger.info("Done.");
	}
	
	public static LinkedHashMap<ExtendedPairData, ResultWithScores<PairResult>> buildMapResultsWithScores(LinkedHashMap<ExtendedPairData, PairResult> results, Classifier classifierForPredictions, LinearClassifier classifierForSearch) throws ClassifierException
	{
		LinkedHashMap<ExtendedPairData, ResultWithScores<PairResult>> ret = new LinkedHashMap<ExtendedPairData, ResultWithScores<PairResult>>();
		double threshold = classifierForSearch.getThreshold();
		for (Map.Entry<ExtendedPairData, PairResult> result : results.entrySet())
		{
			Map<Integer,Double> featureVector = result.getValue().getBestTree().getFeatureVector();
			double weightVector_times_featureVector = classifierForSearch.getProduct(featureVector);
			double weightVector_times_featureVector_plus_threshold = weightVector_times_featureVector+threshold;
			double searchClassifierScore = classifierForSearch.classify(featureVector);
			double predictionsClassifierScore = classifierForPredictions.classify(featureVector);
			
			ResultWithScores<PairResult> resultWithScores = new ResultWithScores<PairResult>(result.getValue(),weightVector_times_featureVector,weightVector_times_featureVector_plus_threshold,searchClassifierScore,predictionsClassifierScore);
			ret.put(result.getKey(), resultWithScores);
		}
		
		return ret;
	}
	
	public static void logTextFile(File textFile, Logger logger) throws FileNotFoundException, IOException
	{
		try(BufferedReader reader = new BufferedReader(new FileReader(textFile)))
		{
			StringBuilder sb = new StringBuilder();
			sb.append(textFile.getName()).append(":\n");
			for (String line = reader.readLine();line!=null;line = reader.readLine())
			{
				sb.append(line).append("\n");
			}
			logger.info(sb.toString());
		}
	}
	
	

	private static Logger logger = Logger.getLogger(RTESystemsUtils.class);
}
