package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierTrainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.TrainedClassifiers;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class DefaultClassifierTrainer extends ClassifierTrainer
{
	public DefaultClassifierTrainer(
			FeatureVectorStructureOrganizer featureVectorStructureOrganizer)
	{
		super();
		this.featureVectorStructureOrganizer = featureVectorStructureOrganizer;
	}

	@Override
	public TrainedClassifiers train(Vector<LabeledSample> samples,
			List<Vector<LabeledSample>> olderSamples,
			ClassifierGenerator classifierGenerator) throws BiuteeException
	{
		try
		{
			int useCounter = counter;
			synchronized(DefaultClassifierTrainer.class)
			{
				useCounter = counter;
				++counter;
			}
			
			Map<Integer, String> featureNames = featureVectorStructureOrganizer.createMapOfFeatureNames();
			
			LinearTrainableStorableClassifier classifierForSearch = classifierGenerator.createTrainableClassifierForSearch();
			Vector<LabeledSample> searchSamples = samplesForSearch(samples,olderSamples);
			saveSamples(searchSamples,useCounter,BiuteeConstants.LEARNING_MODEL_FILE_SEARCH_INDICATOR);
			logger.info("Training classifier for search with "+searchSamples.size()+" samples.");
			classifierForSearch.train(searchSamples);
			classifierForSearch.setFeaturesNames(featureNames);
			
			TrainableStorableClassifier classifierForPredictions = classifierGenerator.createTrainableClassifierForPredictions();
			Vector<LabeledSample> predictionsSamples = samplesForPredictions(samples,olderSamples);
			saveSamples(predictionsSamples,useCounter,BiuteeConstants.LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR);
			logger.info("Training classifier for predictions with "+predictionsSamples.size()+" samples.");
			classifierForPredictions.train(predictionsSamples);
			classifierForPredictions.setFeaturesNames(featureNames);
			
			return new TrainedClassifiers(classifierForSearch,classifierForPredictions);
			
		}
		catch (ClassifierException | TeEngineMlException | IOException e)
		{
			throw new BiuteeException("Failed to train classifier",e);
		}
	}
	
	protected Vector<LabeledSample> samplesForSearch(Vector<LabeledSample> samples,
			List<Vector<LabeledSample>> olderSamples)
	{
		return samples;
	}

	protected Vector<LabeledSample> samplesForPredictions(Vector<LabeledSample> samples,
			List<Vector<LabeledSample>> olderSamples)
	{
		return samples;
	}
	
	protected void saveSamples(Vector<LabeledSample> samples, int useCounter, String searchPredictionsIndicator) throws FileNotFoundException, IOException
	{
		File file = new File(BiuteeConstants.LABELED_SAMPLES_FILE_PREFIX+useCounter+BiuteeConstants.LABELED_SAMPLES_FILE_POSTFIX);
		try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file)))
		{
			stream.writeObject(samples);
		}
		ExperimentManager.getInstance().register(file);
	}

	private final FeatureVectorStructureOrganizer featureVectorStructureOrganizer;
	
	private static int counter=0;
	
	private static final Logger logger = Logger.getLogger(DefaultClassifierTrainer.class);
}
