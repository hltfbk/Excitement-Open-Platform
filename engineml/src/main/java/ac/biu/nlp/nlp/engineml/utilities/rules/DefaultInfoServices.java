package ac.biu.nlp.nlp.engineml.utilities.rules;
import ac.biu.nlp.nlp.engineml.operations.operations.RuleRhsInstantiation;
import ac.biu.nlp.nlp.engineml.utilities.InfoServices;
import ac.biu.nlp.nlp.engineml.utilities.InfoServicesUtils;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;


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
