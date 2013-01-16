package eu.excitementproject.eop.transformations.representation.srl_informations;
import java.io.Serializable;

import eu.excitementproject.eop.transformations.representation.srl_types.SemanticRole;


/**
 * 
 * @author Asher Stern
 * @since Dec 27, 2011
 *
 */
public class SemanticRoleLabelById implements Serializable
{
	private static final long serialVersionUID = 4678447600755800054L;
	
	public SemanticRoleLabelById(String predicateId, SemanticRole semanticRole)
	{
		super();
		this.predicateId = predicateId;
		this.semanticRole = semanticRole;
	}
	
	
	public String getPredicateId()
	{
		return predicateId;
	}
	public SemanticRole getSemanticRole()
	{
		return semanticRole;
	}
	
	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((predicateId == null) ? 0 : predicateId.hashCode());
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
		SemanticRoleLabelById other = (SemanticRoleLabelById) obj;
		if (predicateId == null)
		{
			if (other.predicateId != null)
				return false;
		} else if (!predicateId.equals(other.predicateId))
			return false;
		if (semanticRole == null)
		{
			if (other.semanticRole != null)
				return false;
		} else if (!semanticRole.equals(other.semanticRole))
			return false;
		return true;
	}




	private final String predicateId;
	private final SemanticRole semanticRole;
}
