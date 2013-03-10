package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.document_sublayer.DocumentInitializer;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;



/**
 * Converts {@link PreprocessedTopicDataSet} to an {@link ExtendedPreprocessedTopicDataSet}.
 * This is done by calling the document-sub-layer class {@link DocumentInitializer}.
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class ExtendedTopicDataSetGenerator
{
	/////////////////////////////// PUBLIC //////////////////////////////

	public ExtendedTopicDataSetGenerator(PreprocessedTopicDataSet topic, TESystemEnvironment teSystemEnvironment)
	{
		super();
		this.topic = topic;
		this.teSystemEnvironment = teSystemEnvironment;
	}

	public void generate() throws TreeCoreferenceInformationException, TeEngineMlException, AnnotatorException
	{
		Map<String, Map<Integer, ExtendedNode>> convertedDocumentTrees = new LinkedHashMap<String, Map<Integer,ExtendedNode>>();
		Map<String,ExtendedNode> convertedDocumentsHeadlinesTrees = new LinkedHashMap<String, ExtendedNode>();
		Map<String,TreeCoreferenceInformation<ExtendedNode>> convertedCoreferenceInformation = new LinkedHashMap<String, TreeCoreferenceInformation<ExtendedNode>>();

		for (String documentId : topic.getDocumentsTreesMap().keySet())
		{
			Map<Integer,BasicNode> documentAsMap = topic.getDocumentsTreesMap().get(documentId);
			if (!topic.getDocumentsHeadlinesTrees().containsKey(documentId)) throw new TeEngineMlException("document headline for document "+documentId+" was not found.");
			BasicNode headlineTree = topic.getDocumentsHeadlinesTrees().get(documentId);
			if (!topic.getCoreferenceInformation().containsKey(documentId)) throw new TeEngineMlException("Coreference information for "+documentId+" was not found.");
			TreeCoreferenceInformation<BasicNode> coreferenceInformation = topic.getCoreferenceInformation().get(documentId);
			
			DocumentInitializer documentInitializerForDocument =
					new DocumentInitializer(coreferenceInformation,teSystemEnvironment,documentAsMap);
			documentInitializerForDocument.initialize();
			convertedDocumentTrees.put(documentId, documentInitializerForDocument.getDocumentAsTreesMap());
			convertedCoreferenceInformation.put(documentId,documentInitializerForDocument.getCreatedCoreferenceInformation());
			
			DocumentInitializer documentInitializerForHeadline =
					new DocumentInitializer(null,teSystemEnvironment,headlineTree);
			documentInitializerForHeadline.initialize();
			convertedDocumentsHeadlinesTrees.put(documentId,documentInitializerForHeadline.getDocumentAsTree());
		}
		
		Map<String,ExtendedNode> convertedHypothesisMap = new LinkedHashMap<String, ExtendedNode>();
		for (String hypothesisId : topic.getHypothesisTreesMap().keySet())
		{
			BasicNode originalHypothesis = topic.getHypothesisTreesMap().get(hypothesisId);
			DocumentInitializer documentInitializerForHypothesis =
					new DocumentInitializer(null,teSystemEnvironment,originalHypothesis);
			documentInitializerForHypothesis.initialize();
			convertedHypothesisMap.put(hypothesisId,documentInitializerForHypothesis.getDocumentAsTree());
		}
		
		this.extendedTopic = new ExtendedPreprocessedTopicDataSet(topic.getTopicDataSet(),convertedHypothesisMap,convertedDocumentTrees,convertedCoreferenceInformation,convertedDocumentsHeadlinesTrees);
	}
	
	
	public ExtendedPreprocessedTopicDataSet getExtendedTopic() throws TeEngineMlException
	{
		if (null==extendedTopic) throw new TeEngineMlException("generate() was not called");
		return extendedTopic;
	}


	/////////////////////////////// PRIVATE //////////////////////////////

	private PreprocessedTopicDataSet topic;
	private TESystemEnvironment teSystemEnvironment;
	
	private ExtendedPreprocessedTopicDataSet extendedTopic = null;

}
