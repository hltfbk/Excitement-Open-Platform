package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.external_classifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.RteSumSingleCandidateResult;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * No longer used.
 * @author Asher Stern
 * @since Aug 4, 2011
 *
 */
public class ResultsToSamples
{
	public ResultsToSamples(
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> results,
			Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers)
	{
		super();
		this.results = results;
		this.goldStandardAnswers = goldStandardAnswers;
	}

	public void createSamples() throws TeEngineMlException
	{
		samples = new Vector<LabeledSample>();
		
		for (String topicId : results.keySet())
		{
			LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>> topicResults = results.get(topicId);
			Map<String, Set<SentenceIdentifier>> gsTopic = null;
			if (goldStandardAnswers!=null)
			{
				gsTopic = goldStandardAnswers.get(topicId);
				if (null==gsTopic)throw new TeEngineMlException("bad gold standard. Missing "+topicId);
			}
			for (String hypothesisId : topicResults.keySet())
			{
				LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisResults = topicResults.get(hypothesisId);
				Set<SentenceIdentifier> gsHypothesis = null;
				if (goldStandardAnswers!=null)
				{
					gsHypothesis = gsTopic.get(hypothesisId);
					if (null==gsHypothesis) throw new TeEngineMlException("bad gold standard. Missing "+hypothesisId);
				}
				
				for (SentenceIdentifier sentenceId : hypothesisResults.keySet())
				{
					RteSumSingleCandidateResult sentenceResult = hypothesisResults.get(sentenceId);
					boolean gsSentence = false;
					if (goldStandardAnswers!=null)
					{
						gsSentence = gsHypothesis.contains(sentenceId);
					}
					samples.add(new LabeledSample(sentenceResult.getFeatureVector(), gsSentence));
				}
			}
			
		}
	}
	
	

	public Vector<LabeledSample> getSamples()
	{
		return samples;
	}



	private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> results;
	private Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers;
	
	private Vector<LabeledSample> samples;
}
