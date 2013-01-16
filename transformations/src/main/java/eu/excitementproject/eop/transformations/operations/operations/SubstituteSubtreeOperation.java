package eu.excitementproject.eop.transformations.operations.operations;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.rules.ExtendedInfoServices;

/**
 * An operations which replaces a sub-tree of the original tree by a new sub-tree.
 * Usually used by coreference and parser-antecedent substitution.
 * 
 * @author Asher Stern
 * @since Jan 24, 2011
 *
 */
public class SubstituteSubtreeOperation extends GenerationOperationForExtendedNode
{
	public SubstituteSubtreeOperation(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree, ExtendedNode subtreeToRemove,
			ExtendedNode subtreeToAdd, Set<ExtendedNode> subtreesToOmit) throws OperationException
			
	{
		super(textTree, hypothesisTree);
		this.subtreeToRemove = subtreeToRemove;
		this.subtreeToAdd = subtreeToAdd;
		this.subtreesToOmit = (subtreesToOmit!=null)?subtreesToOmit:emptySet;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		affectedNodes = new LinkedHashSet<ExtendedNode>();
		mapOrigToGenerated = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		mapGeneratedToOrig = new LinkedHashMap<ExtendedNode, ExtendedNode>();
		substitutionNodeEncountered = false;
		this.generatedTree = copySubTree(textTree.getTree(),null,null,false);
		updateAntecedents();
	}
	
	
	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		if (null==mapGeneratedToOrig) throw new OperationException("internal bug");
		this.mapOriginalToGenerated = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		for (ExtendedNode generatedNode : mapGeneratedToOrig.keySet())
		{
			ExtendedNode originalNode = mapGeneratedToOrig.get(generatedNode);
			mapOriginalToGenerated.put(originalNode, generatedNode);
		}
	}

	
	protected ExtendedNode copySubTree(ExtendedNode origSubTree, ExtendedInfo alternativeEdgeInfo, Integer depth, boolean copyingSubtreeToAdd) throws OperationException
	{
		ExtendedNode generatedSubTree = null;
		if (null==alternativeEdgeInfo)
		{
			generatedSubTree = new ExtendedNode(origSubTree.getInfo());
		}
		else
		{
			generatedSubTree = new ExtendedNode(infoServices.newInfoFromTreeNodeRhsNodeAndEdge(origSubTree.getInfo(), null, alternativeEdgeInfo));
		}
		
		boolean copyChildren = true;
		if (depth!=null) { if (depth.intValue()<=0) { copyChildren=false;}}
		if ((copyChildren)&&(origSubTree.getChildren()!=null))
		{
			Integer recursiveDepth = null;
			if (depth!=null) recursiveDepth = new Integer(depth.intValue()-1);
			List<ExtendedNode> generatedChildren = new ArrayList<ExtendedNode>(origSubTree.getChildren().size());
			for (ExtendedNode child : origSubTree.getChildren())
			{
				if (!copyingSubtreeToAdd || (!subtreesToOmit.contains(child)))
				{
					if (child!=subtreeToRemove)
					{
						generatedChildren.add(copySubTree(child,null,recursiveDepth,copyingSubtreeToAdd));
					}
					else
					{
						if (!substitutionNodeEncountered)
						{
							substitutionNodeEncountered = true;
							recursiveDepth = Constants.DEFAULT_COPY_SUBTREE_DEPTH;
							generatedChildren.add(copySubTree(subtreeToAdd,child.getInfo(),recursiveDepth,true));
						}
					}
				}
			}
			for (ExtendedNode generatedChild : generatedChildren)
			{
				generatedSubTree.addChild(generatedChild);
			}
		}
		
		if (copyingSubtreeToAdd)
		{
			affectedNodes.add(generatedSubTree);
		}
		mapGeneratedToOrig.put(generatedSubTree,origSubTree);
		
		if (!mapOrigToGenerated.leftContains(origSubTree))
			mapOrigToGenerated.put(origSubTree,generatedSubTree);
		
		return generatedSubTree;
	}
	
	protected void updateAntecedents()
	{
		for (ExtendedNode generatedNode : mapGeneratedToOrig.keySet())
		{
			if (mapGeneratedToOrig.get(generatedNode).getAntecedent()!=null)
			{
				if (mapOrigToGenerated.leftContains(mapGeneratedToOrig.get(generatedNode).getAntecedent()))
				{
					generatedNode.setAntecedent(mapOrigToGenerated.leftGet(mapGeneratedToOrig.get(generatedNode).getAntecedent()));
				}
				
			}
		}
	}
	
	
	protected ExtendedNode subtreeToRemove;
	protected ExtendedNode subtreeToAdd;
	protected Set<ExtendedNode> subtreesToOmit;
	protected InfoServices<ExtendedInfo, ?> infoServices = new ExtendedInfoServices();
	
	protected BidirectionalMap<ExtendedNode, ExtendedNode> mapOrigToGenerated = null;
	protected Map<ExtendedNode, ExtendedNode> mapGeneratedToOrig = null;
	protected boolean substitutionNodeEncountered = false;

	private static final Set<ExtendedNode> emptySet = new DummySet<ExtendedNode>();
	
}
