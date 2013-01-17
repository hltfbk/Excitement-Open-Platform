package eu.excitementproject.eop.transformations.operations.finders;
import java.util.Map;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
//import eu.excitementproject.eop.transformations.rteflow.macro.search.old_beam_search.BeamSearchTextTreesProcessor;


/**
 * For a given tree, this class finds mapping from its nodes to nodes in other
 * trees that are of the same coreference group (i.e. nodes that refer to the
 * same entity in the real world).
 * <P>
 * @deprecated <B>Note: This class is no longer used.</B> A better approach to co-reference usage is
 * by {@link SubstitutionCoreferenceFinder}.
 * <P>
 * This class gets as input the tree, a co-reference information
 * ( {@link TreeCoreferenceInformation} ), and a mapping from the nodes of
 * this tree to nodes in the original trees (that mapping is maintained by
 * the caller, e.g. {@link BeamSearchTextTreesProcessor}). Then it calculates
 * a {@link ValueSetMap} from the nodes of the given tree, to other nodes
 * that exist in the {@link TreeCoreferenceInformation} object, given as input.
 * <P>
 * The {@link ValueSetMap} returned by this class can than be used by other
 * classes that find operations that can be done, based on this coreference
 * mapping. For example, it can be used by
 * {@link ByMapSubstitutionCoreferenceFinder}.
 * 
 * 
 * @see SubstitutionCoreferenceFinder

 * 
 * @author Asher Stern
 * @since Feb 2, 2011
 *
 */
@Deprecated
public class AbstractCoreferenceFinder
{
	protected AbstractCoreferenceFinder(
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			Map<ExtendedNode, ExtendedNode> mapNodeToOriginal,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree)
	{
		super();
		this.coreferenceInformation = coreferenceInformation;
		this.mapNodeToOriginal = mapNodeToOriginal;
		this.textTree = textTree;
	}

	protected void findCoreferringNodes() throws TreeCoreferenceInformationException
	{
		mapNodeToCoreferringNodes = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		for (ExtendedNode node : AbstractNodeUtils.treeToSet(textTree.getTree()))
		{
			if (mapNodeToOriginal.containsKey(node))
			{
				ExtendedNode itsOriginal = mapNodeToOriginal.get(node);
				if (coreferenceInformation.isNodeExist(itsOriginal))
				{
					Integer originalNodeId = coreferenceInformation.getIdOf(itsOriginal);
					ImmutableSet<ExtendedNode> nodesInTheGroup = coreferenceInformation.getGroup(originalNodeId);
					if (nodesInTheGroup.size()>1) // this node is not alone
					{
						for (ExtendedNode coreferringNode : nodesInTheGroup)
						{
							if (coreferringNode!=itsOriginal) // we are looking for other nodes
							{
								mapNodeToCoreferringNodes.put(node, coreferringNode);
							}
						}
					}
				}
				
			}
		}
	}
	
	

	protected ValueSetMap<ExtendedNode, ExtendedNode> getMapNodeToCoreferringNodes()
	{
		return mapNodeToCoreferringNodes;
	}



	protected TreeCoreferenceInformation<ExtendedNode> coreferenceInformation;
	protected Map<ExtendedNode, ExtendedNode> mapNodeToOriginal;
	protected TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	
	protected ValueSetMap<ExtendedNode, ExtendedNode> mapNodeToCoreferringNodes;
}
