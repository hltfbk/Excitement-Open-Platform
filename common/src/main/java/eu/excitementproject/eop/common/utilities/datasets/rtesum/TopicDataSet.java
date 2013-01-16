package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Represents the whole content of a single topic (of Rte6 main task data set).
 * This includes: the documents, candidates and hypotheses.
 * 
 * 
 * @author Asher Stern
 * @since Aug 11, 2010
 *
 */
public class TopicDataSet implements Serializable
{
	private static final long serialVersionUID = -6554748478599475324L;
	
	
	/**
	 * Constructor with the whole topic contents. Used by {@link Rte6DatasetLoader}.
	 * @param topicId the topic-id
	 * @param candidatesMap map from hypothesis-id to a set of candidate-sentences (sentences which perhaps entail it).
	 * @param hypothesisMap map from hypothesis-id to the hypothesis-sentence
	 * @param documentsMap map from document-id to document-meta-data
	 */
	public TopicDataSet(String topicId,
			Map<String, Set<SentenceIdentifier>> candidatesMap,
			Map<String, String> hypothesisMap,
			Map<String, Map<Integer, String>> documentsMap,
			Map<String,DocumentMetaData> documentsMetaData
			)
	{
		super();
		this.topicId = topicId;
		this.candidatesMap = candidatesMap;
		this.hypothesisMap = hypothesisMap;
		this.documentsMap = documentsMap;
		this.documentsMetaData = documentsMetaData;
		
	}
	
	
	/**
	 * Returns the topic id.
	 * @return the topic id.
	 */
	public String getTopicId()
	{
		return topicId;
	}
	
	/**
	 * Returns the evaluation pairs.
	 * The return value is a map from hypothesis-id, to set of sentences that
	 * are candidates for entailing it.
	 * 
	 * @return a map from hypothesis-id, to set of sentences that
	 * are candidates for entailing it.
	 */
	public Map<String, Set<SentenceIdentifier>> getCandidatesMap()
	{
		return candidatesMap;
	}
	
	/**
	 * Returns the hypotheses of this topic.
	 * Returns a map from hypothesis id to the hypothesis itself (the hypothesis
	 * sentence).
	 * 
	 * @return a map from hypothesis id to the hypothesis itself (the hypothesis
	 * sentence).
	 */
	public Map<String, String> getHypothesisMap()
	{
		return hypothesisMap;
	}
	
	/**
	 * Returns the documents of this topic.
	 * The return value is a map from document id to a map that represents the document
	 * itself, which is a map from sentence id to sentence.
	 * 
	 * @return a map from document id to a map that represents the document
	 * itself, which is a map from sentence id to sentence.
	 */
	public Map<String, Map<Integer, String>> getDocumentsMap()
	{
		return documentsMap;
	}
	

	/**
	 * Returns a map from document-id to its {@link DocumentMetaData}.
	 * @return
	 */
	public Map<String, DocumentMetaData> getDocumentsMetaData()
	{
		return documentsMetaData;
	}
	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((candidatesMap == null) ? 0 : candidatesMap.hashCode());
		result = prime * result
				+ ((documentsMap == null) ? 0 : documentsMap.hashCode());
		result = prime
				* result
				+ ((documentsMetaData == null) ? 0 : documentsMetaData
						.hashCode());
		result = prime * result
				+ ((hypothesisMap == null) ? 0 : hypothesisMap.hashCode());
		result = prime * result + ((topicId == null) ? 0 : topicId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicDataSet other = (TopicDataSet) obj;
		if (candidatesMap == null)
		{
			if (other.candidatesMap != null)
				return false;
		} else if (!candidatesMap.equals(other.candidatesMap))
			return false;
		if (documentsMap == null)
		{
			if (other.documentsMap != null)
				return false;
		} else if (!documentsMap.equals(other.documentsMap))
			return false;
		if (documentsMetaData == null)
		{
			if (other.documentsMetaData != null)
				return false;
		} else if (!documentsMetaData.equals(other.documentsMetaData))
			return false;
		if (hypothesisMap == null)
		{
			if (other.hypothesisMap != null)
				return false;
		} else if (!hypothesisMap.equals(other.hypothesisMap))
			return false;
		if (topicId == null)
		{
			if (other.topicId != null)
				return false;
		} else if (!topicId.equals(other.topicId))
			return false;
		return true;
	}














	private final String topicId;
	private final Map<String, Set<SentenceIdentifier>> candidatesMap;
	private final Map<String,String> hypothesisMap;
	private final Map<String,Map<Integer, String>> documentsMap;
	private final Map<String,DocumentMetaData> documentsMetaData;
}
