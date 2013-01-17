package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.external_classifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.RteSumSingleCandidateResult;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * No longer used.
 * @author Asher Stern
 * 
 *
 */
public class AnswerFromExternalPredictions
{
	public AnswerFromExternalPredictions(
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> results,
			Vector<Boolean> externalPredictions)
	{
		super();
		this.results = results;
		this.externalPredictions = externalPredictions;
	}

	public void createAnswers() throws TeEngineMlException
	{
		Iterator<Boolean> predictionsIterator = externalPredictions.iterator();
		answers = new LinkedHashMap<String, LinkedHashMap<String,Set<SentenceIdentifier>>>();
		for (String topicId : results.keySet())
		{
			LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>> topicResults = results.get(topicId);
			LinkedHashMap<String,Set<SentenceIdentifier>> topicAnswers = new LinkedHashMap<String, Set<SentenceIdentifier>>();
			for (String hypothesisId : topicResults.keySet())
			{
				LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisResults = topicResults.get(hypothesisId);
				Set<SentenceIdentifier> hypothesisAnswers = new LinkedHashSet<SentenceIdentifier>();
				for (SentenceIdentifier sentenceId : hypothesisResults.keySet())
				{
					if (!predictionsIterator.hasNext()) throw new TeEngineMlException("Length incompatible between prediction vector and results");
					Boolean prediction = predictionsIterator.next();
					if (prediction==true)
					{
						hypothesisAnswers.add(sentenceId);
					}
				}
				topicAnswers.put(hypothesisId,hypothesisAnswers);
			}
			answers.put(topicId, topicAnswers);
		}
		if (predictionsIterator.hasNext())throw new TeEngineMlException("Length incompatible prediction vector and results");
	}
	
	
	

	public LinkedHashMap<String, LinkedHashMap<String, Set<SentenceIdentifier>>> getAnswers()
	{
		return answers;
	}




	private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> results;
	private Vector<Boolean> externalPredictions;
	
	private LinkedHashMap<String,LinkedHashMap<String,Set<SentenceIdentifier>>> answers;
}
