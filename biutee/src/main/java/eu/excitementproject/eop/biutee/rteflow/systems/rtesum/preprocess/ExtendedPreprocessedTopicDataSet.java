package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Contains all parse-trees and coreference-information of an RTE-Sum topic.
 * The type of tree nodes is {@link ExtendedNode}.
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
