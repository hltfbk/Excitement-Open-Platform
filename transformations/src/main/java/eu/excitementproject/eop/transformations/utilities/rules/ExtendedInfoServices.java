package eu.excitementproject.eop.transformations.utilities.rules;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.InfoServicesUtils;

/**
 * Used for the instantiation of rules' right-hand-sides.
 * 
 * @see InfoServices
 * 
 * @author Asher Stern
 * @since Apr 6, 2011
 *
 */
public class ExtendedInfoServices implements InfoServices<ExtendedInfo, Info>
{
	public boolean isVariableT(ExtendedInfo info)
	{
		return InfoGetFields.isVariable(info);
	}

	public boolean isVariableR(Info info)
	{
		return InfoGetFields.isVariable(info);
	}

	public ExtendedInfo newInfoRT(Info nodeInfo, ExtendedInfo edgeInfo)
	{
		return new ExtendedInfo(nodeInfo.getId(), nodeInfo.getNodeInfo(), edgeInfo.getEdgeInfo(), ExtendedNodeConstructor.EMPTY_ADDITIONAL_NODE_INFORMATION);
	}

	public ExtendedInfo newInfoFromTreeNodeRhsNodeAndEdge(ExtendedInfo treeInfo, Info rhsInfo, ExtendedInfo edgeInfo)
	{
		return new ExtendedInfo(treeInfo.getId(), InfoServicesUtils.combineNodeInfo(treeInfo, rhsInfo), edgeInfo.getEdgeInfo(),
				treeInfo.getAdditionalNodeInformation());
	}

	public ExtendedInfo newInfoFromTreeNodeAndRhsNodeAndRhsEdge(ExtendedInfo treeNodeInfo, Info rhsNodeInfo,Info rhsEdgeInfo)
	{
		return new ExtendedInfo(treeNodeInfo.getId(), InfoServicesUtils.combineNodeInfo(treeNodeInfo, rhsNodeInfo), rhsEdgeInfo.getEdgeInfo(),
				treeNodeInfo.getAdditionalNodeInformation());
	}

	public ExtendedInfo convertFromIR(Info info)
	{
		return new ExtendedInfo(info.getId(),info.getNodeInfo(),info.getEdgeInfo(),ExtendedNodeConstructor.EMPTY_ADDITIONAL_NODE_INFORMATION);
	}

	public ExtendedInfo newInfoRTT(Info nodeInfo, ExtendedInfo edgeInfo, ExtendedInfo additionalInformation)
	{
		return new ExtendedInfo(nodeInfo.getId(), nodeInfo.getNodeInfo(), edgeInfo.getEdgeInfo(), ExtendedInfoGetFields.getAdditionalNodeInformation(additionalInformation));
	}

	public ExtendedInfo convertFromIRT(Info info,
			ExtendedInfo additionalInformation)
	{
		return new ExtendedInfo(info.getId(),info.getNodeInfo(),info.getEdgeInfo(),ExtendedInfoGetFields.getAdditionalNodeInformation(additionalInformation));
	}

}
