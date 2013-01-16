package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class PreprocessedTopicDataSet extends GenericPreprocessedTopicDataSet<Info, BasicNode>
{
	public PreprocessedTopicDataSet(
			TopicDataSet topicDataSet,
			Map<String, BasicNode> hypothesisTrees,
			Map<String, Map<Integer, BasicNode>> documentTrees,
			Map<String, TreeCoreferenceInformation<BasicNode>> coreferenceInformation,
			Map<String, BasicNode> documentsHeadlinesTrees)
			throws TeEngineMlException
	{
		super(topicDataSet, hypothesisTrees, documentTrees, coreferenceInformation,
				documentsHeadlinesTrees);
	}

	private static final long serialVersionUID = -8833760625092454161L;

	

}
