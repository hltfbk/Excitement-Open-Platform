package eu.excitementproject.eop.transformations.utilities.rules;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.transformations.operations.operations.RuleRhsInstantiation;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.InfoServicesUtils;


/**
 * Was used by {@link RuleRhsInstantiation}.
 * See also {@link ExtendedInfoServices}.
 * 
 * @author Asher Stern
 * @since Feb 7, 2011
 *
 */
public class DefaultInfoServices implements InfoServices<Info, Info>
{
	public boolean isVariableT(Info info)
	{
		return InfoGetFields.isVariable(info);
	}

	public boolean isVariableR(Info info)
	{
		return InfoGetFields.isVariable(info);
	}

	public Info newInfoRT(Info nodeInfo, Info edgeInfo)
	{
		return new DefaultInfo(nodeInfo.getId(), nodeInfo.getNodeInfo(), edgeInfo.getEdgeInfo());
	}
	
	public Info newInfoFromTreeNodeRhsNodeAndEdge(Info treeInfo, Info rhsInfo, Info edgeInfo) {
		return new DefaultInfo(treeInfo.getId(), InfoServicesUtils.combineNodeInfo(treeInfo, rhsInfo), edgeInfo.getEdgeInfo());
	}

	public Info newInfoFromTreeNodeAndRhsNodeAndRhsEdge(Info nodeInfo, Info ruleRhsNodeInfo, Info ruleRhsEdgeInfo)
	{
		return new DefaultInfo(nodeInfo.getId(), InfoServicesUtils.combineNodeInfo(nodeInfo, ruleRhsNodeInfo), ruleRhsEdgeInfo.getEdgeInfo());
	}


	public Info convertFromIR(Info info)
	{
		return info;
	}

	public Info newInfoRTT(Info nodeInfo, Info edgeInfo,
			Info additionalInformation)
	{
		return new DefaultInfo(nodeInfo.getId(), nodeInfo.getNodeInfo(), edgeInfo.getEdgeInfo());
	}

	public Info convertFromIRT(Info info, Info additionalInformation)
	{
		return info;
	}



}
