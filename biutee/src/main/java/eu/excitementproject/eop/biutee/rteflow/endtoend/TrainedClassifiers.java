package eu.excitementproject.eop.biutee.rteflow.endtoend;

import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;

/**
 * Holds two classifiers: classifier for search and classifier for predictions.
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public class TrainedClassifiers
{
	public TrainedClassifiers(
			LinearTrainableStorableClassifier classifierForSearch,
			TrainableStorableClassifier classifierForPredictions)
	{
		super();
		this.classifierForSearch = classifierForSearch;
		this.classifierForPredictions = classifierForPredictions;
	}
	
	
	
	public LinearTrainableStorableClassifier getClassifierForSearch()
	{
		return classifierForSearch;
	}
	public TrainableStorableClassifier getClassifierForPredictions()
	{
		return classifierForPredictions;
	}



	private final LinearTrainableStorableClassifier classifierForSearch;
	private final TrainableStorableClassifier classifierForPredictions;
}
