package eu.excitementproject.eop.common.representation.parse.tree;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * This class is used to create a copy of a given tree. The original tree and its copy might
 * not be of the same type. For example, the original tree might be of type
 * {@link BasicConstructionNode}, which the copy might be of type {@link BasicNode}.
 * <P>
 * This class creates an exact copy of the original tree, but with the information (info)
 * in the nodes of the new tree copied according to a specification given by the user.
 * <BR>
 * That specification is given by implementing {@link InfoConverter}. If the two trees
 * have the same information type (i.e. OI and TI are the same) then the implementation
 * of {@link InfoConverter} can be naive: just return the <code>Info<code> of the node it is given as input.
 * If, on the contrary, the info of the copy differs from the info of the original tree, then
 * the implementation of {@link InfoConverter} specifies how to convert the information from
 * the original tree to the new tree.
 * <P>
 * Note that this class also preserves the "antecedent" information. (see {@link AbstractNode#getAntecedent()}) 
 * 
 * 
 * 
 * @author Asher Stern
 * @since Apr 7, 2011
 *
 * @param <OI> type of information on the nodes of the original tree (e.g. {@link Info})
 * @param <OS> the original tree nodes' type (e.g. {@link BasicNode})
 * @param <TI> type of information on the nodes of the generated tree (e.g. {@link Info})
 * @param <TS> the generated tree nodes' type (e.g. {@link BasicNode})
 */
public class TreeCopier<OI, OS extends AbstractNode<OI, OS>, TI, TS extends AbstractNode<TI,TS>>
{
	//////////////////////////////// PUBLIC /////////////////////////////////

	// nested types 
	
	/**
	 * An interface that specifies how to convert the information of the original tree's nodes
	 * to the new tree's nodes.
	 * 
	 * @author Asher Stern
	 * 
	 *
	 * @param <OS> The type of nodes of the original tree (note that this is the
	 * type of the nodes themselves, not their information). For example {@link BasicNode}.
	 * 
	 * @param <TI> The type of the nodes in the new tree (the tree that will be created). (e.g. {@link Info})
	 */
	public static interface InfoConverter<OS,TI>
	{
		public TI convert(OS os);
	}
	
	// constructor and methods

	public TreeCopier(OS originalTree, InfoConverter<OS, TI> infoConverter,
			AbstractNodeConstructor<TI, TS> nodeConstructor)
	{
		super();
		this.originalTree = originalTree;
		this.infoConverter = infoConverter;
		this.nodeConstructor = nodeConstructor;
	}
	
	/**
	 * Creates the new tree, which is an exact copy of the given tree (according
	 * to {@link InfoConverter} which specifies how to convert the information from the
	 * original tree's nodes to the new tree's nodes).
	 */
	public void copy()
	{
		nodesMap = new SimpleBidirectionalMap<OS, TS>();
		generatedTree = copySubTree(originalTree);
		copyAntecedents();
	}
	
	/**
	 * Returns the copied (the created) tree
	 * 
	 * @return the copied (the created) tree
	 */
	public TS getGeneratedTree()
	{
		return generatedTree;
	}

	/**
	 * Returns a one-to-one mapping of the nodes from the original tree to the copied
	 * (the created) tree.
	 * 
	 * @return a one-to-one mapping of the nodes from the original tree to the copied
	 * (the created) tree.
	 */
	public BidirectionalMap<OS, TS> getNodesMap()
	{
		return nodesMap;
	}
	


	
	//////////////////////////////// PRIVATE /////////////////////////////////
	
	/**
	 * Takes a tree (OS) and creates a new tree (TS)
	 * TS = generated tree type<BR>
	 * OS = original tree type<BR>
	 * 
	 * @param os the original tree
	 * @return the generated tree
	 */
	private TS copySubTree(OS os)
	{
		TS generatedTree = nodeConstructor.newNode(infoConverter.convert(os));
		if (os.getChildren()!=null)
		{
			for (OS child : os.getChildren())
			{
				TS generatedChild = copySubTree(child);
				generatedTree.addChild(generatedChild);
			}
		}
		nodesMap.put(os, generatedTree);
		return generatedTree;
	}
	
	private void copyAntecedents()
	{
		for (OS originalNode : nodesMap.leftSet())
		{
			if (originalNode.getAntecedent()!=null)
			{
				OS originalAntecedent = originalNode.getAntecedent();
				TS generatedAntecedent = nodesMap.leftGet(originalAntecedent);
				nodesMap.leftGet(originalNode).setAntecedent(generatedAntecedent);
			}
		}
	}

	private OS originalTree;



	private TS generatedTree;
	private BidirectionalMap<OS, TS> nodesMap;
	private InfoConverter<OS,TI> infoConverter;
	private AbstractNodeConstructor<TI, TS> nodeConstructor;

}
