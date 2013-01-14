package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;

/**
 * 
 * @author Asher Stern
 * @since Aug 7, 2012
 *
 */
public class CandidateIdentifier
{
	public CandidateIdentifier(String topicId, String hypothesisID,
			SentenceIdentifier sentenceID)
	{
		super();
		this.topicId = topicId;
		this.hypothesisID = hypothesisID;
		this.sentenceID = sentenceID;
	}
	
	
	
	public String getTopicId()
	{
		return topicId;
	}
	public String getHypothesisID()
	{
		return hypothesisID;
	}
	public SentenceIdentifier getSentenceID()
	{
		return sentenceID;
	}



	private final String topicId;
	private final String hypothesisID;
	private final SentenceIdentifier sentenceID;
}
