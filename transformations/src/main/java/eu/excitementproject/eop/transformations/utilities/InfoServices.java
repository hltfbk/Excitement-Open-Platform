package eu.excitementproject.eop.transformations.utilities;
import eu.excitementproject.eop.transformations.operations.operations.RuleRhsInstantiation;

/**
 * This interface is used by {@link RuleRhsInstantiation}.
 * 
 * @author Asher Stern
 * @since Feb 7, 2011
 *
 * @param <IT> info in trees
 * @param <IR> info in rules
 */
public interface InfoServices<IT, IR>
{
	public boolean isVariableT(IT info);
	public boolean isVariableR(IR info);
	
	
	/**
	 * Creates an IT, composed of the node information in treeInfo (combined with the POS in rhsInfo), and edge information in edgeInfo.
	 * @param treeInfo
	 * @param rhsInfo may be null!
	 * @param edgeInfo
	 * @return
	 */
	public IT newInfoFromTreeNodeRhsNodeAndEdge(IT treeInfo, IR rhsInfo, IT edgeInfo);
	
	/**
	 * Creates an IT, composed of the node information in treeInfo (combined with the POS in rhsInfo), and edge information in rhsInfo.
	 * @param treeInfo
	 * @param rhsInfo
	 * @return
	 */
	public IT newInfoFromTreeNodeAndRhsNodeAndRhsEdge(IT treeInfo, IR rhsInfo, IR ruleRhsEdgeInfo);
	

	
	/**
	 * Creates a new info of type IT, with node information of "nodeInfo",
	 * edge information of "edgeInfo", and additional-node-information of "additionalInformation".
	 * <BR>
	 * Used for creating a node by rule application such that the node is in the
	 * rule's right-hand-side, and the additional-node-information should come from
	 * the original tree.
	 * @param nodeInfo
	 * @param edgeInfo
	 * @param additionalInformation
	 * @return
	 * @throws TeEngineMlException 
	 */
	public IT newInfoRTT(IR nodeInfo, IT edgeInfo, IT additionalInformation) throws TeEngineMlException;
	
	/**
	 * Used to create a node in rule application, such that the node itself
	 * is defined in the rule's right hand side, but the additional information
	 * comes from the original tree. 
	 * @param info
	 * @param additionalInformation
	 * @return
	 * @throws TeEngineMlException 
	 */
	public IT convertFromIRT(IR info, IT additionalInformation) throws TeEngineMlException;
	
	/**
	 * Creates an IT, composed of the node information in nodeInfo, and edge information in edgeInfo.
	 * @param nodeInfo
	 * @param edgeInfo
	 * @return
	 */
	public IT newInfoRT(IR nodeInfo, IT edgeInfo);

	/**
	 * Converts the given IR to an object of type IT
	 * @param info
	 * @return
	 */
	public IT convertFromIR(IR info);
}
