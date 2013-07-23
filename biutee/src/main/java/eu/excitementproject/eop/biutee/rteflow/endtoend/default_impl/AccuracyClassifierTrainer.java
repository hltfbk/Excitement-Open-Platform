package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;

/**
 * 
 * @author Asher Stern
 * @since Jul 17, 2013
 *
 */
public class AccuracyClassifierTrainer extends DefaultClassifierTrainer
{
	public AccuracyClassifierTrainer(FeatureVectorStructureOrganizer featureVectorStructureOrganizer)
	{
		super(featureVectorStructureOrganizer);
	}
	
	protected Vector<LabeledSample> samplesForSearch(Vector<LabeledSample> samples,
			List<Vector<LabeledSample>> olderSamples)
	{
		if (BiuteeConstants.USE_NEGATIVES_FROM_PREVIOUS_ITERATIONS_IN_ACCURACY_TRAINING)
		{
			Vector<LabeledSample> allOldSamples = fromListVectors(olderSamples);
			double currentNegatives = (double)negativesIn(samples);
			double currentPositives = ((double)samples.size())-currentNegatives;
			double negativesInOld = (double)negativesIn(allOldSamples);
			int numberOfPositivesToAdd = (int)((currentPositives*negativesInOld)/currentNegatives);

			Vector<LabeledSample> positivesToAdd = takeSamples(extractByLabel(samples,true),numberOfPositivesToAdd);
			Vector<LabeledSample> negativesToAdd = extractByLabel(allOldSamples, false);

			Vector<LabeledSample> ret = new Vector<>();
			ret.addAll(samples);
			ret.addAll(positivesToAdd);
			ret.addAll(negativesToAdd);

			Collections.shuffle(ret, new Random(0));
			return ret;
		}
		else
		{
			return super.samplesForSearch(samples,olderSamples);
		}
	}


	private static <T> Vector<T> fromListVectors(List<Vector<T>> listVectors)
	{
		Vector<T> ret = new Vector<>();
		for (Vector<T> vector : listVectors)
		{
			ret.addAll(vector);
		}
		return ret;
	}
	
	private int negativesIn(Vector<LabeledSample> samples)
	{
		int ret = 0;
		for (LabeledSample sample : samples)
		{
			if (false==sample.getLabel())
			{
				++ret;
			}
		}
		return ret;
	}
	
	private Vector<LabeledSample> extractByLabel(Vector<LabeledSample> source, boolean label)
	{
		Vector<LabeledSample> ret = new Vector<>();
		for (LabeledSample sample : source)
		{
			if (label==sample.getLabel())
			{
				ret.add(sample);
			}
		}
		return ret;
		
	}
	
	private Vector<LabeledSample> takeSamples(Vector<LabeledSample> source, int howMany)
	{
		if (0==source.size()) return null;
		
		Vector<LabeledSample> ret = new Vector<>();
		Iterator<LabeledSample> iterator = source.iterator();
		for (int index=0;index<howMany;++index)
		{
			if (!iterator.hasNext())
			{
				iterator = source.iterator();
			}
			ret.add(iterator.next());
		}
		return ret;
	}
	

}
