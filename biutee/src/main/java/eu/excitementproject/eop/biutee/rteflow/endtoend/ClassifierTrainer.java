package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.util.List;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * Given samples, which are the vectors that represent proofs of a labeled data,
 * trains the classifiers (for search and for predictions).
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public abstract class ClassifierTrainer
{
	public abstract TrainedClassifiers train(Vector<LabeledSample> samples, List<Vector<LabeledSample>> olderSamples, ClassifierGenerator classifierGenerator) throws BiuteeException;
}
