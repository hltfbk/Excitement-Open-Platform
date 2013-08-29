package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.io.File;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ReasonableGuessCreator;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public abstract class DefaultAbstractClassifierGenerator extends ClassifierGenerator
{
	public DefaultAbstractClassifierGenerator(
			FeatureVectorStructureOrganizer featureVectorStructure,
			File modelForSearch, File modelForPredictions)
	{
		super();
		this.featureVectorStructure = featureVectorStructure;
		this.modelForSearch = modelForSearch;
		this.modelForPredictions = modelForPredictions;
	}

	
	public DefaultAbstractClassifierGenerator(FeatureVectorStructureOrganizer featureVectorStructure)
	{
		super();
		this.featureVectorStructure = featureVectorStructure;
		this.modelForSearch = null;
		this.modelForPredictions = null;
	}

	
	@Override
	public LinearTrainableStorableClassifier createReasonableGuessClassifier() throws BiuteeException
	{
		try
		{
			ReasonableGuessCreator reasonableGuessCreator = new ReasonableGuessCreator(featureVectorStructure);
			reasonableGuessCreator.create();
			return reasonableGuessCreator.getClassifier();
		}
		catch (ClassifierException | TeEngineMlException e)
		{
			throw new BiuteeException("Failed to create classifier. See nested exception.",e);
		}
	}

	@Override
	public LinearClassifier loadClassifierForSearch() throws BiuteeException
	{
		if (null==modelForSearch) throw new BiuteeException("Cannot load classifier, since no model file is given." +
				"It seems that this ClassifierGenerator was intended to be used in training, but a method for test is used.");
		try
		{
			return SafeClassifiersIO.loadLinearClassifier(featureVectorStructure, modelForSearch);
		}
		catch (TeEngineMlException e)
		{
			throw new BiuteeException("Failed to load classifier. See nested exception.",e);
		}
	}

	@Override
	public Classifier loadClassifierForPredictions() throws BiuteeException
	{
		if (null==modelForPredictions) throw new BiuteeException("Cannot load classifier, since no model file is given." +
				"It seems that this ClassifierGenerator was intended to be used in training, but a method for test is used.");
		try
		{
			return SafeClassifiersIO.load(featureVectorStructure, modelForPredictions);
		}
		catch (TeEngineMlException e)
		{
			throw new BiuteeException("Failed to load classifier. See nested exception.",e);
		}
	}


	protected final FeatureVectorStructureOrganizer featureVectorStructure;
	private final File modelForSearch; // might be null
	private final File modelForPredictions; // might be null
}
