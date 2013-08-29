package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class F1ClassifierTrainer extends DefaultClassifierTrainer
{
	public F1ClassifierTrainer(FeatureVectorStructureOrganizer featureVectorStructureOrganizer)
	{
		super(featureVectorStructureOrganizer);
	}
	
	
	protected Vector<LabeledSample> samplesForSearch(Vector<LabeledSample> samples,
			List<Vector<LabeledSample>> olderSamples)
	{
		logger.warn("DOES NOT USE SAMPLES FROM EARLIER ITERATIONS. THIS SHOULD BE CHANGED.");
		return super.samplesForSearch(samples,olderSamples);
	}

	protected Vector<LabeledSample> samplesForPredictions(Vector<LabeledSample> samples,
			List<Vector<LabeledSample>> olderSamples)
	{
		logger.warn("DOES NOT USE SAMPLES FROM EARLIER ITERATIONS. THIS SHOULD BE CHANGED.");
		return super.samplesForPredictions(samples,olderSamples);
	}

	

	private static final Logger logger = Logger.getLogger(F1ClassifierTrainer.class);
}
