package ac.biu.nlp.nlp.engineml.representation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.EdgeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;

/**
 * 
 * @author Asher Stern
 * @since Apr 6, 2011
 *
 */
public class ExtendedInfo extends DefaultInfo
{
	private static final long serialVersionUID = -2585898760131431769L;


	public ExtendedInfo(String id, NodeInfo nodeInfo, EdgeInfo edgeInfo, AdditionalNodeInformation additionalNodeInformation)
	{
		super(id, nodeInfo, edgeInfo);
		this.additionalNodeInformation = additionalNodeInformation;
	}

	public ExtendedInfo(Info info, AdditionalNodeInformation additionalNodeInformation)
	{
		super(info.getId(), info.getNodeInfo(), info.getEdgeInfo());
		this.additionalNodeInformation = additionalNodeInformation;
	}

	public AdditionalNodeInformation getAdditionalNodeInformation()
	{
		return additionalNodeInformation;
	}
	
	




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtendedInfo [nodeInfo=" + InfoGetFields.getLemma(nodeInfo) + "/" + InfoGetFields.getPartOfSpeech(nodeInfo)
				+ ", edgeInfo=" + InfoGetFields.getRelation(edgeInfo) + ", id=" + id + ", additionalNodeInformation=" + additionalNodeInformation + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((additionalNodeInformation == null) ? 0
						: additionalNodeInformation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedInfo other = (ExtendedInfo) obj;
		if (additionalNodeInformation == null)
		{
			if (other.additionalNodeInformation != null)
				return false;
		} else if (!additionalNodeInformation
				.equals(other.additionalNodeInformation))
			return false;
		return true;
	}






	protected AdditionalNodeInformation additionalNodeInformation;
}
