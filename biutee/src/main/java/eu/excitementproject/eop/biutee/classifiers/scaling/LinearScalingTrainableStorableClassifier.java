package eu.excitementproject.eop.biutee.classifiers.scaling;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;


/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2012
 *
 */
public class LinearScalingTrainableStorableClassifier extends LinearScalingClassifier implements LinearTrainableStorableClassifier
{
	public LinearScalingTrainableStorableClassifier(LinearTrainableStorableClassifier realLinearTrainableStorableClassifier)
	{
		this.realClassifier = realLinearTrainableStorableClassifier;
		this.realLinearClassifier = realLinearTrainableStorableClassifier;
		this.realLlinearTrainableStorableClassifier = realLinearTrainableStorableClassifier;
	}
	
	public void setDoNotScale(Set<Integer> doNotScale)
	{
		this.doNotScale = doNotScale;
		
		//// Write some logging information
		StringBuilder sb = new StringBuilder();
		sb.append("Will not scale features: ");
		boolean firstIteration = true;
		for (Integer i : this.doNotScale)
		{
			if (firstIteration) firstIteration=false; else sb.append(", ");
			sb.append(i);
		}
		logger.info(sb.toString());
		////
	}


	@Override
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		findMinMaxMaps(samples);
		
		Vector<LabeledSample> scaledSamples = getScaledSamples(samples);
		realLlinearTrainableStorableClassifier.train(scaledSamples);
	}
	
	@Override
	public void reset() throws ClassifierException
	{
		realLlinearTrainableStorableClassifier.reset();
		minMap = null;
		maxMap = null;
	}

	@Override
	public LearningModel store() throws ClassifierException
	{
		return new LearningModelForLinearScalingClassifier(maxMap,minMap,doNotScale,realLlinearTrainableStorableClassifier.store(),descriptionOfTraining());
	}
	
	@Override
	public LinearTrainableStorableClassifier getRealClassifier()
	{
		return this.realLlinearTrainableStorableClassifier;
	}



	private LinearTrainableStorableClassifier realLlinearTrainableStorableClassifier;
	
	private static final Logger logger = Logger.getLogger(LinearScalingTrainableStorableClassifier.class);
}
