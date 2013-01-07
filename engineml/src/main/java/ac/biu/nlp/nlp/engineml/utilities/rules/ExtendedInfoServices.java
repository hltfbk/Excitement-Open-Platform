package ac.biu.nlp.nlp.engineml.utilities.rules;

import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfoGetFields;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.utilities.InfoServices;
import ac.biu.nlp.nlp.engineml.utilities.InfoServicesUtils;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;

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
