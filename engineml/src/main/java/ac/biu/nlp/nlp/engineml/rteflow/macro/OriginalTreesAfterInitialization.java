package ac.biu.nlp.nlp.engineml.rteflow.macro;
import java.util.List;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;

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
