package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.CandidateIdentifier;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.RTESumSurroundingSentencesUtility;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 21, 2013
 *
 */
public class RteSumInstance extends Instance
{
	private static final long serialVersionUID = -7449848402310443166L;
	
	public RteSumInstance(CandidateIdentifier candidateIdentifier, RteSumDatasetContents datasetContents) throws BiuteeException
	{
		super();
		this.candidateIdentifier = candidateIdentifier;
		this.datasetContents = datasetContents;
		
		retrieveRelevantInformation();
	}

	@Override
	public HypothesisInformation getHypothesisInformation() throws BiuteeException
	{
		return hypothesisInformation;
	}

	@Override
	public String toString()
	{
		return "Rte-sum instance: "+candidateIdentifier.getTopicId()+" ["+candidateIdentifier.getHypothesisID()+"] "+candidateIdentifier.getSentenceID();
	}

	@Override
	public Boolean getBinaryLabel() throws BiuteeException
	{
		return label.booleanValue();
	}
	
	public CandidateIdentifier getCandidateIdentifier()
	{
		return candidateIdentifier;
	}

	public ExtendedNode getTextTree()
	{
		return textTree;
	}

	public List<ExtendedNode> getSurroundingTextTrees()
	{
		return surroundingTextTrees;
	}

	public String getTextSentence()
	{
		return textSentence;
	}
	
	public String getHypothesisSentence()
	{
		return hypothesisSentence;
	}

	public ExtendedNode getHypothesisTree()
	{
		return hypothesisTree;
	}
	
	
	public TreeCoreferenceInformation<ExtendedNode> getCoreferenceInformation() throws BiuteeException
	{
		ExtendedPreprocessedTopicDataSet extendedTopic = datasetContents.getTopics_mapIdToTopic().get(candidateIdentifier.getTopicId());
		return extendedTopic.getCoreferenceInformation().get(candidateIdentifier.getSentenceID().getDocumentId());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		try
		{
			retrieveRelevantInformation();
		}
		catch(BiuteeException e)
		{
			// I can't throw a checked exception here.
			throw new RuntimeException("Could not deserialize.",e);
		}
	}

	
	private synchronized void createHypothesisInformation() throws BiuteeException
	{
		ExtendedPreprocessedTopicDataSet extendedTopic = datasetContents.getTopics_mapIdToTopic().get(candidateIdentifier.getTopicId());
		String hypothesisSentence =  extendedTopic.getTopicDataSet().getHypothesisMap().get(candidateIdentifier.getHypothesisID());
		ExtendedNode hypothesisTree = extendedTopic.getHypothesisTreesMap().get(candidateIdentifier.getHypothesisID());
		hypothesisInformation = new HypothesisInformation(hypothesisSentence, hypothesisTree);
	}
	
	private synchronized void retrieveLabel() throws BiuteeException
	{
		Map<String,Map<String,Set<SentenceIdentifier>>> goldStandardAnswers = datasetContents.getGoldStandardAnswers();
		if (null==goldStandardAnswers)
		{
			label=null;
		}
		else
		{
			Map<String,Set<SentenceIdentifier>> mapOfTopic = goldStandardAnswers.get(candidateIdentifier.getTopicId());
			if (null==mapOfTopic) throw new BiuteeException("Wrong gold-standard was given. The gold-standard does not contain the topic \""+candidateIdentifier.getTopicId()+"\"");
			Set<SentenceIdentifier> setOfHypothesis = mapOfTopic.get(candidateIdentifier.getHypothesisID());
			if (null==setOfHypothesis) throw new BiuteeException("Wrong gold-standard was given. The gold-standard does not contain the hypothesis \""+candidateIdentifier.getHypothesisID()+"\"");
			label = setOfHypothesis.contains(candidateIdentifier.getSentenceID());
		}
	}

	private synchronized void retrieveRelevantInformation() throws BiuteeException
	{
		createHypothesisInformation();
		retrieveLabel();
		
		ExtendedPreprocessedTopicDataSet extendedTopic = datasetContents.getTopics_mapIdToTopic().get(candidateIdentifier.getTopicId());

		// Take the parse tree of the candidate
		Map<String, Map<Integer, ExtendedNode>> topicTreesMap = extendedTopic.getDocumentsTreesMap();
		Map<Integer, ExtendedNode> documentTrees = topicTreesMap.get(candidateIdentifier.getSentenceID().getDocumentId());
		int sentenceIndex = Integer.valueOf(candidateIdentifier.getSentenceID().getSentenceId());
		textTree = documentTrees.get(sentenceIndex); // this is the parse tree.


		// Create a list of other sentence that will be considered as "exist in pair"
		RTESumSurroundingSentencesUtility surroundingUtility = datasetContents.getTopics_mapTopicidToSurroundingUtility().get(candidateIdentifier.getTopicId());
		synchronized(surroundingUtility)
		{
			try{surroundingTextTrees = surroundingUtility.getSurroundingSentences(candidateIdentifier.getSentenceID(), textTree);}
			catch (TeEngineMlException e){throw new BiuteeException("Failed to retrieve information for this instance.",e);}
		}

		TopicDataSet topicDS = extendedTopic.getTopicDataSet();
		textSentence = topicDS.getDocumentsMap().get(candidateIdentifier.getSentenceID().getDocumentId()).get(sentenceIndex);
		
		// Though the same information has already retrieved for HypothesisInformation,
		// one should not trust the HypothesisInformation to hold them.
		// It is unknown whether and which changes will be applied to HypothesisInformation in the future.
		hypothesisSentence =  extendedTopic.getTopicDataSet().getHypothesisMap().get(candidateIdentifier.getHypothesisID());
		hypothesisTree = extendedTopic.getHypothesisTreesMap().get(candidateIdentifier.getHypothesisID());
	}

	private final CandidateIdentifier candidateIdentifier;
	private final RteSumDatasetContents datasetContents;
	
	// The following fields are constructed either in the constructor, or in deserialization
	private transient HypothesisInformation hypothesisInformation = null;
	private transient Boolean label = null;
	private transient ExtendedNode textTree = null;
	private transient List<ExtendedNode> surroundingTextTrees = null;
	private transient String textSentence = null;
	private transient String hypothesisSentence = null;
	private transient ExtendedNode hypothesisTree = null;
}
