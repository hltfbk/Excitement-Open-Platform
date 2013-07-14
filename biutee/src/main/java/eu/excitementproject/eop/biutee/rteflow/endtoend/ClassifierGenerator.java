package eu.excitementproject.eop.biutee.rteflow.endtoend;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public abstract class ClassifierGenerator
{
	public abstract LinearClassifier createReasonableGuessClassifier() throws BiuteeException;
	public abstract LinearClassifier loadClassifierForSearch() throws BiuteeException;
	public abstract Classifier loadClassifierForPredictions() throws BiuteeException;
	public abstract LinearTrainableStorableClassifier createTrainableClassifierForSearch() throws BiuteeException;
	public abstract TrainableStorableClassifier createTrainableClassifierForPredictions() throws BiuteeException;
}
