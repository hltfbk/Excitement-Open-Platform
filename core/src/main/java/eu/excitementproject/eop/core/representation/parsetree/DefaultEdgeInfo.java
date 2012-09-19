package eu.excitementproject.eop.core.representation.parsetree;

/**
 * 
 * [DELETEME_LATER: imported from BIUTEE 2.4.1 with no modification - although the enum in dependency relation is different] 
 * 
 * Immutable.
 * @author Asher Stern
 *
 */
public class DefaultEdgeInfo implements EdgeInfo
{

	private static final long serialVersionUID = -738966898545696065L;

	public DefaultEdgeInfo(DependencyRelation relation)
	{
		this.relation = relation;
	}

	public DependencyRelation getDependencyRelation()
	{
		return this.relation;
	}
	
	public boolean isEqualTo(EdgeInfo other)
	{
		if (this == other) return true;
		if (other == null) return false;
		if ( (this.relation==null) && (other.getDependencyRelation()==null) )
			return true;
		if (this.relation==null) return false;
		if (other.getDependencyRelation()==null) return false;
		
		return this.relation.equals(other.getDependencyRelation());
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getDependencyRelation() == null) ? 0 : getDependencyRelation().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EdgeInfo))
			return false;
		EdgeInfo other = (EdgeInfo) obj;
		if (getDependencyRelation() == null)
		{
			if (other.getDependencyRelation() != null)
				return false;
		} else if (!getDependencyRelation().equals(other.getDependencyRelation()))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultEdgeInfo [relation=" + relation + "]";
	}

	protected DependencyRelation relation;

}
