package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public abstract class ClassifierTrainer
{
	public abstract TrainedClassifiers train(List<LabeledSample> samples, List<List<LabeledSample>> olderSamples, ClassifierGenerator classifierGenerator) throws BiuteeException;
}
