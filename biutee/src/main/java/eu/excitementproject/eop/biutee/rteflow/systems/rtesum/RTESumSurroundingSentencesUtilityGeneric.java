package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.GenericPreprocessedTopicDataSet;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DocumentMetaData;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Nov 14, 2012
 *
 * @param <I>
 * @param <S>
 */
public class RTESumSurroundingSentencesUtilityGeneric<I, S extends AbstractNode<I,S>>
{
	public static final String CURRENT_IDENTIFIER = "current";
	public static final String HEADLINE_IDENTIFIER_PREFIX = "headline";
	
	public RTESumSurroundingSentencesUtilityGeneric(GenericPreprocessedTopicDataSet<I,S> topic) throws TreeStringGeneratorException
	{
		this.topic = topic;
		init();
	}
	
	
	public void setPreviousSentencesToAdd(int previousSentencesToAdd)
	{
		this.previousSentencesToAdd = previousSentencesToAdd;
	}
	
	public void setAddHeadLines(boolean addHeadLines)
	{
		this.addHeadLines = addHeadLines;
	}
	public void setAddAllSentences(boolean addAllSentences)
	{
		this.addAllSentences = addAllSentences;
	}


	public List<S> getSurroundingSentences(SentenceIdentifier sentenceID,S textTree) throws TeEngineMlException
	{
		List<TreeAndIdentifier<I, S>> surrounding = getSurroundingSentencesWithIdentifier(sentenceID, textTree);
		List<S> ret = new ArrayList<S>(surrounding.size());
		for (TreeAndIdentifier<I, S> treeAndIdentifier : surrounding)
		{
			ret.add(treeAndIdentifier.getTree());
		}
		
		return ret;
	}
	
	public List<TreeAndIdentifier<I, S>> getSurroundingSentencesWithIdentifier(SentenceIdentifier sentenceID,S textTree) throws TeEngineMlException
	{
		int sentenceIndex = Integer.valueOf(sentenceID.getSentenceId());
		// List<S> surroundingTextTrees = new ArrayList<S>(surroundingBaseList.size()+1+1);
		List<TreeAndIdentifier<I, S>> surroundingTextTrees = new ArrayList<TreeAndIdentifier<I, S>>(surroundingBaseList.size()+1+previousSentencesToAdd);
		if (addHeadLines)
		{
			surroundingTextTrees.addAll(surroundingBaseList);
		}
		String currentSentence = rawDocumentsMap.get(sentenceID.getDocumentId()).get(Integer.parseInt(sentenceID.getSentenceId()));
		if (null==currentSentence) throw new TeEngineMlException("null current sentence in RTE-Sum surrounding sentences utility.");
		surroundingTextTrees.add(new TreeAndIdentifier<I, S>(textTree,CURRENT_IDENTIFIER,currentSentence));
		if (addAllSentences)
		{
			int idCurrent = Integer.parseInt(sentenceID.getSentenceId());
			
			Map<Integer, S> theWholeDocument = topicTreesMap.get(sentenceID.getDocumentId());
			for (Integer intId : theWholeDocument.keySet())
			{
				if (intId.intValue()!=idCurrent)
				{
					String rawSentence = rawDocumentsMap.get(sentenceID.getDocumentId()).get(intId);
					if (null==rawSentence) throw new TeEngineMlException("null raw sentence for a surrounding sentence, in RTE-Sum surrounding utility.");
					int offset = intId.intValue()-idCurrent;
					String offsetString = String.valueOf(offset);
					S treeInDocument = theWholeDocument.get(intId);
					surroundingTextTrees.add(new TreeAndIdentifier<I, S>(treeInDocument,offsetString,rawSentence));
				}
			}
		}
		else
		{
			for (int index=0;index<previousSentencesToAdd;++index)
			{
				if (sentenceIndex>index)
				{
					String rawSentence = rawDocumentsMap.get(sentenceID.getDocumentId()).get(sentenceIndex-(index+1));
					if (null==rawSentence) throw new TeEngineMlException("null raw sentence for a surrounding sentence, in RTE-Sum surrounding utility.");
					surroundingTextTrees.add(
							new TreeAndIdentifier<I, S>(
									topicTreesMap.get(sentenceID.getDocumentId()).get(sentenceIndex-(index+1)),
									String.valueOf(-(index+1)),
									rawSentence));
				}
			}
		}

		return surroundingTextTrees;
	}
	

	private void init() throws TreeStringGeneratorException
	{
		topicTreesMap = topic.getDocumentsTreesMap();
		rawDocumentsMap = topic.getTopicDataSet().getDocumentsMap();
		documentsMetaData = topic.getTopicDataSet().getDocumentsMetaData();
		createBaseList();
	}
	
	
	protected void createBaseList() throws TreeStringGeneratorException
	{
		surroundingBaseList = new ArrayList<TreeAndIdentifier<I, S>>(topic.getDocumentsHeadlinesTrees().keySet().size());
		for (String documentId : topic.getDocumentsHeadlinesTrees().keySet())
		{
			surroundingBaseList.add(new TreeAndIdentifier<I, S>(
					topic.getDocumentsHeadlinesTrees().get(documentId),
					HEADLINE_IDENTIFIER_PREFIX+": "+documentId,
					documentsMetaData.get(documentId).getHeadline()
					));
		}
	}

	// input
	private GenericPreprocessedTopicDataSet<I,S> topic;
	protected int previousSentencesToAdd = 1;
	protected boolean addHeadLines = true;
	protected boolean addAllSentences = false;
	
	// internals
	private Map<String, Map<Integer, S>> topicTreesMap = null;
	protected List<TreeAndIdentifier<I, S>> surroundingBaseList = null;
	protected Map<String,Map<Integer, String>> rawDocumentsMap;
	protected Map<String,DocumentMetaData> documentsMetaData;
	
}
