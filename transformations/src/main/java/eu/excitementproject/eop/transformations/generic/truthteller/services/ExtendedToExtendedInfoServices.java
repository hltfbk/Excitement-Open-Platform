/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.services;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.InfoServicesUtils;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * @author Amnon Lotan
 * @since 19/06/2011
 * 
 */
public class ExtendedToExtendedInfoServices implements InfoServices<ExtendedInfo, ExtendedInfo> {

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.utilities.InfoServices#isVariableT(java.lang.Object)
	 */
	public boolean isVariableT(ExtendedInfo info) {
		return ExtendedInfoGetFields.isVariable(info);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.utilities.InfoServices#isVariableR(java.lang.Object)
	 */
	public boolean isVariableR(ExtendedInfo info) {
		return ExtendedInfoGetFields.isVariable(info);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.utilities.InfoServices#newInfoRT(java.lang.Object, java.lang.Object)
	 */
	public ExtendedInfo newInfoRT(ExtendedInfo nodeInfo, ExtendedInfo edgeInfo) {
		return new ExtendedInfo(nodeInfo.getId(), nodeInfo.getNodeInfo(), edgeInfo.getEdgeInfo(), nodeInfo.getAdditionalNodeInformation());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.utilities.InfoServices#newInfoFromTreeNodeRhsNodeAndEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public ExtendedInfo newInfoFromTreeNodeRhsNodeAndEdge(ExtendedInfo treeInfo, ExtendedInfo rhsInfo, ExtendedInfo edgeInfo) {
		return new ExtendedInfo(treeInfo.getId(), InfoServicesUtils.combineNodeInfo(treeInfo, rhsInfo), edgeInfo.getEdgeInfo(), 
				treeInfo.getAdditionalNodeInformation());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.utilities.InfoServices#newInfoTR(java.lang.Object, java.lang.Object)
	 */
	public ExtendedInfo newInfoFromTreeNodeAndRhsNodeAndRhsEdge(ExtendedInfo treeInfo,  ExtendedInfo rhsInfo, ExtendedInfo ruleRhsEdgeInfo) {
		return new ExtendedInfo(treeInfo.getId(),InfoServicesUtils.combineNodeInfo(treeInfo, rhsInfo), ruleRhsEdgeInfo.getEdgeInfo(), treeInfo.getAdditionalNodeInformation());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.utilities.InfoServices#convertFromIR(java.lang.Object)
	 */
	public ExtendedInfo convertFromIR(ExtendedInfo info) {
		return new ExtendedInfo(info.getId(),info.getNodeInfo(),info.getEdgeInfo(), info.getAdditionalNodeInformation());
	}

	public ExtendedInfo newInfoRTT(ExtendedInfo nodeInfo,
			ExtendedInfo edgeInfo, ExtendedInfo additionalInformation) throws TeEngineMlException
	{
		throw new TeEngineMlException("Method newInfoRTT in class ExtendedToExtendedInfoServices is not implemented");
	}

	public ExtendedInfo convertFromIRT(ExtendedInfo info,
			ExtendedInfo additionalInformation) throws TeEngineMlException
	{
		throw new TeEngineMlException("Method convertFromIRT in class ExtendedToExtendedInfoServices is not implemented");
	}

}
