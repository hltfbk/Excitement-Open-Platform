package eu.excitementproject.eop.biutee.rteflow.macro;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * Stores the trees as originally given to {@link TextTreesProcessor}.
 * Used by GUI.
 * 
 * @author Asher Stern
 * @since Jan 30, 2012
 *
 */
public class OriginalTreesAfterInitialization
{
	public OriginalTreesAfterInitialization(
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation) throws TreeAndParentMapException
	{
		super();
		this.originalTextTrees = originalTextTrees;
		this.hypothesisTree = hypothesisTree;
		this.originalMapTreesToSentences = originalMapTreesToSentences;
		hypothesisTreeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(this.hypothesisTree);
		this.coreferenceInformation = coreferenceInformation;
	}
	
	public List<ExtendedNode> getOriginalTextTrees()
	{
		return originalTextTrees;
	}
	public ExtendedNode getHypothesisTree()
	{
		return hypothesisTree;
	}
	public Map<ExtendedNode, String> getOriginalMapTreesToSentences()
	{
		return originalMapTreesToSentences;
	}

	public TreeAndParentMap<ExtendedInfo, ExtendedNode> getHypothesisTreeAndParentMap()
	{
		return hypothesisTreeAndParentMap;
	}
	

	public TreeCoreferenceInformation<ExtendedNode> getCoreferenceInformation()
	{
		return coreferenceInformation;
	}

	
	private final List<ExtendedNode> originalTextTrees;
	private final ExtendedNode hypothesisTree;
	private final Map<ExtendedNode, String> originalMapTreesToSentences;
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTreeAndParentMap;
	private final TreeCoreferenceInformation<ExtendedNode> coreferenceInformation;
}
