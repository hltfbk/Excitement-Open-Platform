package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * 
 * @author Asher Stern
 * @since Aug 19, 2013
 *
 */
public class PairDataCollapseToSingleTree
{
	public PairDataCollapseToSingleTree(ExtendedPairData originalPairData)
	{
		super();
		this.originalPairData = originalPairData;
	}

	public ExtendedPairData collapse()
	{
		if (1==originalPairData.getTextTrees().size())
		{
			return originalPairData;
		}
		logger.debug("Collapsing multiple trees into a single tree.");
		
		ExtendedNode collapsedTree = collapseTrees(originalPairData.getTextTrees());
		List<String> allSentences = new LinkedList<>();
		for (Map.Entry<ExtendedNode, String> entry : originalPairData.getMapTreesToSentences().entrySet())
		{
			allSentences.add(entry.getValue());
		}
		String allSentencesString = StringUtil.joinIterableToString(allSentences, " ", true);

		return new ExtendedPairData(
				originalPairData.getPair(),
				Collections.singletonList(collapsedTree),
				originalPairData.getHypothesisTree(),
				Collections.singletonMap(collapsedTree, allSentencesString),
				originalPairData.getCoreferenceInformation(),
				originalPairData.getDatasetName()
				);
	}
	
	private ExtendedNode collapseTrees(List<ExtendedNode> trees)
	{
		ExtendedNode root = TreeUtilities.createNodeOfArtificialRoot();
		List<ExtendedNode> contentRoots = new ArrayList<>(trees.size());
		for (ExtendedNode tree : trees)
		{
			if (TreeUtilities.isArtificialRoot(tree))
			{
				if (tree.hasChildren())
				{
					if (tree.getChildren().size()>1) {GlobalMessages.globalWarn("Encountered a tree with more than one content root.",logger);}
					for (ExtendedNode aContentRoot : tree.getChildren())
					{
						contentRoots.add(aContentRoot);
					}
				}
				else {GlobalMessages.globalWarn("Encountered a tree with no contents - only artificial root.", logger);}
			}
			else
			{
				GlobalMessages.globalWarn("Encountered a tree with no artificial root.", logger);
				contentRoots.add(tree);
			}
		}
		for (ExtendedNode contentRoot : contentRoots)
		{
			root.addChild(contentRoot);
		}
		return root;
	}

	private final ExtendedPairData originalPairData;
	
	private static final Logger logger = Logger.getLogger(PairDataCollapseToSingleTree.class);
}
