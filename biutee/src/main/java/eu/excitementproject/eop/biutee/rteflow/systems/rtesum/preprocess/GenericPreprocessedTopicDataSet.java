package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;
import java.io.Serializable;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Contains all parse-trees and coreference-information of an RTE-Sum topic.
 * The type of tree nodes is a generic parameter.
 * 
 * 
 * @author Asher Stern
 * @since Jun 2, 2011
 *
 */
public class GenericPreprocessedTopicDataSet<I,S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = -139465654588727682L;
	
	public GenericPreprocessedTopicDataSet(
			TopicDataSet topicDataSet,
			Map<String, S> hypothesisTrees,
			Map<String, Map<Integer, S>> documentTrees,
			Map<String, TreeCoreferenceInformation<S>> coreferenceInformation,
			Map<String,S> documentsHeadlinesTrees) throws TeEngineMlException
	{
		super();
		if (null==topicDataSet) throw new TeEngineMlException("Null topicDataSet");
		if (null==hypothesisTrees) throw new TeEngineMlException("Null hypothesisTrees");
		if (null==documentTrees) throw new TeEngineMlException("Null documentTrees");
		if (null==coreferenceInformation) throw new TeEngineMlException("Null coreferenceInformation");
		
		this.topicDataSet = topicDataSet;
		this.hypothesisTrees = hypothesisTrees;
		this.documentTrees = documentTrees;
		this.coreferenceInformation = coreferenceInformation;
		this.documentsHeadlinesTrees = documentsHeadlinesTrees;
	}

	public TopicDataSet getTopicDataSet()
	{
		return topicDataSet;
	}
	
	public Map<String, S> getHypothesisTreesMap()
	{
		return this.hypothesisTrees;
	}
	
	/**
	 * Returns map from document-id to the document trees (which are map from sentence id to
	 * sentence's tree).
	 * 
	 * @return map from document-id to the document trees (which are map from sentence id to
	 * sentence's tree).
	 */
	public Map<String, Map<Integer, S>> getDocumentsTreesMap()
	{
		return this.documentTrees;
	}
	
	/**
	 * Returns a map from document-id to its coreference-information
	 * 
	 * @return a map from document-id to its coreference-information
	 */
	public Map<String,TreeCoreferenceInformation<S>> getCoreferenceInformation()
	{
		return this.coreferenceInformation;
	}
	
	

	/**
	 * Returns a map from document-id to a tree that represents the headline of that
	 * document.
	 * 
	 * @return a map from document-id to a tree that represents the headline of that
	 * document.
	 */
	public Map<String, S> getDocumentsHeadlinesTrees()
	{
		return documentsHeadlinesTrees;
	}




	private TopicDataSet topicDataSet;
	private Map<String, S> hypothesisTrees;
	private Map<String, Map<Integer, S>> documentTrees;
	private Map<String,S> documentsHeadlinesTrees;
	private Map<String,TreeCoreferenceInformation<S>> coreferenceInformation;
}
