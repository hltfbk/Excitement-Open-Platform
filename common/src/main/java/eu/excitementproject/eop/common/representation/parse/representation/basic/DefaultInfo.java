package eu.excitementproject.eop.common.representation.parse.representation.basic;

/**
 * This class is immutable.
 * @see Info
 * 
 * @author Asher Stern
 *
 */
public class DefaultInfo implements Info
{
	private static final long serialVersionUID = -8652916076324285710L;
	
	public DefaultInfo(String id, NodeInfo nodeInfo, EdgeInfo edgeInfo)
	{
		this.id = id;
		this.nodeInfo = nodeInfo;
		this.edgeInfo = edgeInfo;
	}
	
	public NodeInfo getNodeInfo()
	{
		return this.nodeInfo;
	}
	
	public EdgeInfo getEdgeInfo()
	{
		return this.edgeInfo;
	}

	
	public String getId()
	{
		return this.id;
	}
	
	

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getEdgeInfo() == null) ? 0 : getEdgeInfo().hashCode());
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result
				+ ((getNodeInfo() == null) ? 0 : getNodeInfo().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Info))
			return false;
		Info other = (Info) obj;
		if (getEdgeInfo() == null)
		{
			if (other.getEdgeInfo() != null)
				return false;
		} else if (!getEdgeInfo().equals(other.getEdgeInfo()))
			return false;
		if (getId() == null)
		{
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getNodeInfo() == null)
		{
			if (other.getNodeInfo() != null)
				return false;
		} else if (!getNodeInfo().equals(other.getNodeInfo()))
			return false;
		return true;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultInfo [nodeInfo=" + nodeInfo + ", edgeInfo=" + edgeInfo
				+ ", id=" + id + "]";
	}




	protected NodeInfo nodeInfo;
	protected EdgeInfo edgeInfo;
	protected String id;

}
