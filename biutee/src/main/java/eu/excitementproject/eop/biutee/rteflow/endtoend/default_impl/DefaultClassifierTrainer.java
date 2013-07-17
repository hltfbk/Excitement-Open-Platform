package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierTrainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.TrainedClassifiers;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
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
			Map<Integer, String> featureNames = featureVectorStructureOrganizer.createMapOfFeatureNames();
			
			LinearTrainableStorableClassifier classifierForSearch = classifierGenerator.createTrainableClassifierForSearch();
			classifierForSearch.train(samples);
			classifierForSearch.setFeaturesNames(featureNames);
			
			TrainableStorableClassifier classifierForPredictions = classifierGenerator.createTrainableClassifierForPredictions();
			classifierForPredictions.train(samples);
			classifierForPredictions.setFeaturesNames(featureNames);
			
			return new TrainedClassifiers(classifierForSearch,classifierForPredictions);
			
		}
		catch (ClassifierException | TeEngineMlException e)
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

	private final FeatureVectorStructureOrganizer featureVectorStructureOrganizer;
}
