package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.GenericPreprocessedTopicDataSet;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;



/**
 * 
 * @author Asher Stern
 * @since Nov 9, 2011
 *
 */
public class RTESumSurroundingSentencesUtility extends RTESumSurroundingSentencesUtilityGeneric<ExtendedInfo,ExtendedNode>
{
	private static final long serialVersionUID = -6444401235425594395L;

	public RTESumSurroundingSentencesUtility(GenericPreprocessedTopicDataSet<ExtendedInfo, ExtendedNode> extendedTopic) throws TreeStringGeneratorException
	{
		super(extendedTopic);
	}

	@Override
	protected void createBaseList() throws TreeStringGeneratorException
	{
		super.createBaseList();
		
		if (logger.isDebugEnabled())
		{
			logger.debug("Printing surroundingBaseList for this topic");
			StringBuffer sb = new StringBuffer();
			int index=0;
			for (TreeAndIdentifier<ExtendedInfo, ExtendedNode> surroundingTreeAndIdentifier : surroundingBaseList)
			{
				ExtendedNode surroundingTree = surroundingTreeAndIdentifier.getTree();
				sb.append("Tree #");
				sb.append(index);
				sb.append("\n");
				sb.append(TreeUtilities.treeToString(surroundingTree));
				sb.append("\n");
				++index;
			}
			int totalNumberOfSurroundingTrees = index;
			sb.append("Total: ");
			sb.append(totalNumberOfSurroundingTrees);
			sb.append(" trees");
			sb.append("\n");
			
			logger.debug(sb.toString());
		}
	}
	
	private static final Logger logger = Logger.getLogger(RTESumSurroundingSentencesUtility.class);
}
