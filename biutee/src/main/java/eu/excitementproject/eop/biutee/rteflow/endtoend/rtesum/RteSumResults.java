package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultAbstractResults;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.InstanceAndProofAndClassification;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.CandidateIdentifier;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class RteSumResults extends DefaultAbstractResults<RteSumInstance, RteSumProof>
{
	public RteSumResults(List<InstanceAndProof<RteSumInstance, RteSumProof>> proofs, Classifier classifierForPredictions, boolean f1_optimized) throws BiuteeException
	{
		super(proofs, classifierForPredictions, f1_optimized);
	}
	
	@Override
	public void compute() throws BiuteeException
	{
		super.compute();
		createMaps();
	}


	@Override
	public void save(File file) throws BiuteeException
	{
		try
		{
			AnswersFileWriter answersWriter = new DefaultAnswersFileWriter();
			answersWriter.setWriteTheEvaluationAttribute(false);
			answersWriter.setAnswers(answer);
			answersWriter.setXml(file.getPath());
			answersWriter.write();
		}
		catch (Rte6mainIOException e)
		{
			throw new BiuteeException("Failed to save answer file.",e);
		}
	}
	
	
	@Override
	protected String detailsOfProof(InstanceAndProofAndClassification<RteSumInstance, RteSumProof> proof) throws BiuteeException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(proof.getInstanceAndProof().getInstance().toString()).append("\n");
		
		sb.append("Text = ").append(proof.getInstanceAndProof().getInstance().getTextSentence()).append("\n");
		sb.append("Hypothesis = ").append(proof.getInstanceAndProof().getInstance().getHypothesisSentence()).append("\n");
		Boolean gs = proof.getInstanceAndProof().getInstance().getBinaryLabel();
		boolean classification = proof.getClassification();
		double score = proof.getScore();
		sb.append("Real annotation = "+ ((gs==null)?"unknown":String.valueOf(gs.booleanValue())) ).append(", ");
		sb.append("Classification = ").append(classification).append(". Score = ").append(String.format("%-3.4f", score)).append("\n");
		sb.append("Proof:\n").append(proof.getInstanceAndProof().getProof().toString()).append("\n");
		
		return sb.toString();
	}

	
	
	private void createMaps()
	{
		mapResults = new LinkedHashMap<>();
		for (InstanceAndProofAndClassification<RteSumInstance, RteSumProof> instance : classifications)
		{
			CandidateIdentifier candidateIdentifier = instance.getInstanceAndProof().getInstance().getCandidateIdentifier();
			String topicId = candidateIdentifier.getTopicId();
			Map<String, Map<SentenceIdentifier, InstanceAndProofAndClassification<RteSumInstance, RteSumProof>>> mapOfTopic = mapOfMapsGetValue(mapResults,topicId);
			Map<SentenceIdentifier, InstanceAndProofAndClassification<RteSumInstance, RteSumProof>> mapOfHypothesis = mapOfMapsGetValue(mapOfTopic,candidateIdentifier.getHypothesisID());
			mapOfHypothesis.put(candidateIdentifier.getSentenceID(), instance);
		}
		createAnswer();
	}
	
	
	private void createAnswer()
	{
		answer = new LinkedHashMap<>();
		for (String topicID : mapResults.keySet())
		{
			Map<String, Map<SentenceIdentifier, InstanceAndProofAndClassification<RteSumInstance, RteSumProof>>> topicClassifications = mapResults.get(topicID);
			Map<String,Set<SentenceIdentifier>> topicAnswer = new LinkedHashMap<String, Set<SentenceIdentifier>>();
			for (String hypothesisID : topicClassifications.keySet())
			{
				Map<SentenceIdentifier, InstanceAndProofAndClassification<RteSumInstance, RteSumProof>> hypothesisClassification = topicClassifications.get(hypothesisID);
				Set<SentenceIdentifier> hypothesisAnswer = new LinkedHashSet<SentenceIdentifier>();
				for (SentenceIdentifier sentenceID : hypothesisClassification.keySet())
				{
					InstanceAndProofAndClassification<RteSumInstance, RteSumProof> candidateClassification = hypothesisClassification.get(sentenceID);
					if (candidateClassification.getClassification())
					{
						hypothesisAnswer.add(sentenceID);
					}
				}
				
				topicAnswer.put(hypothesisID,hypothesisAnswer);
			}
			
			answer.put(topicID,topicAnswer);
		}
	}
	
	private static <T,U,V> Map<U,V> mapOfMapsGetValue(Map<T,Map<U,V>> mapOfMaps, T key)
	{
		Map<U,V> ret = mapOfMaps.get(key);
		if (null==ret)
		{
			mapOfMaps.put(key, new LinkedHashMap<U,V>());
			ret = mapOfMaps.get(key);
		}
		return ret;
	}
	
	private Map<String, Map<String, Map<SentenceIdentifier, InstanceAndProofAndClassification<RteSumInstance, RteSumProof>>>> mapResults;
	//            ^           ^               ^
	//          topic    hypothesis   sentence-id-in-document
	
	
	private Map<String,Map<String,Set<SentenceIdentifier>>> answer;
	//            ^           ^               ^
	//          topic    hypothesis   sentence-id-in-document
	
}
