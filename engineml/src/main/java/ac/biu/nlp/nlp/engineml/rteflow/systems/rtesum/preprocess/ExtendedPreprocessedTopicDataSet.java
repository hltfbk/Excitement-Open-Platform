package ac.biu.nlp.nlp.engineml.rteflow.systems.rtesum.preprocess;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;

/**
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class ExtendedPreprocessedTopicDataSet extends GenericPreprocessedTopicDataSet<ExtendedInfo, ExtendedNode>
{
	private static final long serialVersionUID = -3949446439127009095L;
	
	
	public ExtendedPreprocessedTopicDataSet(
			TopicDataSet topicDataSet,
			Map<String, ExtendedNode> hypothesisTrees,
			Map<String, Map<Integer, ExtendedNode>> documentTrees,
			Map<String, TreeCoreferenceInformation<ExtendedNode>> coreferenceInformation,
			Map<String, ExtendedNode> documentsHeadlinesTrees)
			throws TeEngineMlException
	{
		super(topicDataSet, hypothesisTrees, documentTrees, coreferenceInformation,
				documentsHeadlinesTrees);
	}



	

}
