package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;
import eu.excitementproject.eop.common.datastructures.dgraph.scan.BfsDirectedGraphScan;
import eu.excitementproject.eop.common.datastructures.dgraph.scan.DirectedGraphScanException;
import eu.excitementproject.eop.common.datastructures.dgraph.scan.ScanOperation;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;

/**
 * Given a graph ({@link DirectedGraph}) produced by C&C parser,
 * and a start node, this class generates a tree by BFS.
 * 
 * @author Asher Stern
 *
 */
public class CandCTreeGenerator<T, S extends AbstractConstructionNode<T, S>>
{
	/**
	 * Constructs the generator with the C&C graph, the start node from which the BFS scan will
	 * start, and a {@linkplain CandCTreeNodeGenerator} which creates the nodes of the tree.
	 * @param graph C&C graph
	 * @param startNode start node for BFS scan
	 * @param nodeGenerator generates the nodes.
	 */
	public CandCTreeGenerator(DirectedGraph<CCNode, CCEdgeInfo> graph, CCNode startNode, CandCTreeNodeGenerator<T,S> nodeGenerator)
	{
		this.graph = graph;
		this.startNode = startNode;
		this.nodeGenerator = nodeGenerator;
	}

	/**
	 * Generates the tree
	 * @throws CandCTreesGenerationException
	 */
	public void generateTree() throws CandCTreesGenerationException
	{
		try
		{
			mapCCNodeToTreeNode = new SimpleBidirectionalMap<CCNode, S>();
			BfsDirectedGraphScan<CCNode, CCEdgeInfo> scan = new BfsDirectedGraphScan<CCNode, CCEdgeInfo>(new CCTreeGenerationScanOperation(), graph);
			scan.scan(startNode);
			if (exception != null)
				throw exception;
		}
		catch (DirectedGraphScanException e)
		{
			throw new CandCTreesGenerationException("graph scan error. see nested exception",e);
		}
		catch (DirectedGraphException e)
		{
			throw new CandCTreesGenerationException("graph error. see nested exception",e);
		}
	}
	
	/**
	 * Returns mapping from graph nodes to tree nodes
	 * @return mapping from graph nodes to tree nodes
	 */
	public BidirectionalMap<CCNode, S> getMapCCNodeToTreeNode() throws CandCTreesGenerationException
	{
		if (exception != null)
			throw new CandCTreesGenerationException("generation failed due to nested exception",exception);
		if (null==mapCCNodeToTreeNode)
			throw new CandCTreesGenerationException("Call generateTree() before calling this method.");			
		
		return mapCCNodeToTreeNode;
	}

	/**
	 * Returns the tree
	 * @return the tree
	 */
	public S getTree() throws CandCTreesGenerationException
	{
		if (exception != null)
			throw new CandCTreesGenerationException("generation failed due to nested exception",exception);
		if (null==tree)
			throw new CandCTreesGenerationException("Call generateTree() before calling this method.");			

		return tree;
	}




	///////////////////// PRIVATE PART //////////////////////////////

	private class CCTreeGenerationScanOperation implements ScanOperation<CCNode>
	{
		public void doOperationFor(CCNode node, CCNode from)
		{
			if (null==exception) // no error till now
			{
				CCEdgeInfo edgeInfo = null;
				try
				{
					if (from != null)
						edgeInfo = graph.getEdge(from, node);
				}
				catch(DirectedGraphException e)
				{
					exception = new CandCTreesGenerationException("Unknwon error in the graph. See nested exception",e);
				}
				S treeNode = nodeGenerator.generateNode(node.getInfo(), edgeInfo);
				if (treeNode==null)
					exception = new CandCTreesGenerationException("node generation error. returned null");
				else
				{
					if (null==from)
					{
						// this is the root
						tree = treeNode;
					}
					else
					{
						S parent = mapCCNodeToTreeNode.leftGet(from);
						if (parent==null)
							exception = new CandCTreesGenerationException("parent does not exist. internal bug.");
						else
							parent.addChild(treeNode);
					}
					mapCCNodeToTreeNode.put(node, treeNode);
				}
			}			
			
		}
		
	}
	

	private CandCTreesGenerationException exception = null;
	private BidirectionalMap<CCNode, S> mapCCNodeToTreeNode;
	private S tree = null;
	
	private DirectedGraph<CCNode, CCEdgeInfo> graph;
	private CCNode startNode;
	private CandCTreeNodeGenerator<T,S> nodeGenerator;

}
