package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.RTESumSurroundingSentencesUtility;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;

/**
 * RTE-sum dataset, which includes the T-H pairs, the gold-standard, and the
 * {@link RTESumSurroundingSentencesUtility}s.
 * 
 * @author Asher Stern
 * @since Jul 21, 2013
 *
 */
public class RteSumDatasetContents implements Serializable
{
	private static final long serialVersionUID = 762135769325641411L;
	
	public RteSumDatasetContents(
			Map<String, ExtendedPreprocessedTopicDataSet> topics_mapIdToTopic,
			Map<String, RTESumSurroundingSentencesUtility> topics_mapTopicidToSurroundingUtility,
			Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers)
	{
		super();
		this.topics_mapIdToTopic = topics_mapIdToTopic;
		this.topics_mapTopicidToSurroundingUtility = topics_mapTopicidToSurroundingUtility;
		this.goldStandardAnswers = goldStandardAnswers;
	}
	
	public Map<String, ExtendedPreprocessedTopicDataSet> getTopics_mapIdToTopic()
	{
		return topics_mapIdToTopic;
	}
	public Map<String, RTESumSurroundingSentencesUtility> getTopics_mapTopicidToSurroundingUtility()
	{
		return topics_mapTopicidToSurroundingUtility;
	}
	public Map<String, Map<String, Set<SentenceIdentifier>>> getGoldStandardAnswers()
	{
		return goldStandardAnswers;
	}



	private final Map<String, ExtendedPreprocessedTopicDataSet> topics_mapIdToTopic;
	private final Map<String, RTESumSurroundingSentencesUtility> topics_mapTopicidToSurroundingUtility;
	private final Map<String,Map<String,Set<SentenceIdentifier>>> goldStandardAnswers;
}
