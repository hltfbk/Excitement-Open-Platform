package eu.excitementproject.eop.transformations.representation.srl_types;

import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2011
 *
 */
public class SemanticRoleLabelByIndex implements Serializable
{
	private static final long serialVersionUID = -6869466630733819470L;
	
	public SemanticRoleLabelByIndex(SemanticRole semanticRole, int index)
	{
		super();
		this.semanticRole = semanticRole;
		this.predicateIndex = index;
	}
	
	
	public SemanticRole getSemanticRole()
	{
		return semanticRole;
	}

	public int getPredicateIndex()
	{
		return predicateIndex;
	}





	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + predicateIndex;
		result = prime * result
				+ ((semanticRole == null) ? 0 : semanticRole.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SemanticRoleLabelByIndex other = (SemanticRoleLabelByIndex) obj;
		if (predicateIndex != other.predicateIndex)
			return false;
		if (semanticRole == null)
		{
			if (other.semanticRole != null)
				return false;
		} else if (!semanticRole.equals(other.semanticRole))
			return false;
		return true;
	}



	private final int predicateIndex;
	private final SemanticRole semanticRole;
}
