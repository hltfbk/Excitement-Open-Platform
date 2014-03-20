package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.io.File;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class AccuracyClassifierGenerator extends DefaultAbstractClassifierGenerator
{
	public AccuracyClassifierGenerator(ClassifierFactory classifierFactory, FeatureVectorStructureOrganizer featureVectorStructure, File modelForSearch, File modelForPredictions)
	{
		super(classifierFactory, featureVectorStructure, modelForSearch, modelForPredictions);
	}

	public AccuracyClassifierGenerator(ClassifierFactory classifierFactory, FeatureVectorStructureOrganizer featureVectorStructure)
	{
		super(classifierFactory, featureVectorStructure);
	}

	
	@Override
	public LinearTrainableStorableClassifier createTrainableClassifierForSearch() throws BiuteeException
	{
		try
		{
			return classifierFactory.getProperClassifierForSearch(false);
		}
		catch (ClassifierException e)
		{
			throw new BiuteeException("Failed to create classifier.",e);
		}
	}

	@Override
	public TrainableStorableClassifier createTrainableClassifierForPredictions() throws BiuteeException
	{
		try
		{
			return classifierFactory.getProperClassifier(false);
		}
		catch (ClassifierException e)
		{
			throw new BiuteeException("Failed to create classifier.",e);
		}
	}
}
