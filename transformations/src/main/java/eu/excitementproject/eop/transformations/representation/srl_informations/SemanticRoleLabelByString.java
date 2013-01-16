package eu.excitementproject.eop.transformations.representation.srl_informations;
import java.io.Serializable;

import eu.excitementproject.eop.transformations.representation.srl_types.SemanticRole;


/**
 * Note that {@link #equals(Object)} uses {@link String#equalsIgnoreCase(String)} for
 * the predicateString.
 * @author Asher Stern
 * @since Dec 27, 2011
 *
 */
public class SemanticRoleLabelByString implements Serializable
{
	private static final long serialVersionUID = 4401669791355145294L;
	
	public SemanticRoleLabelByString(SemanticRole semanticRole,
			String predicateString)
	{
		super();
		this.semanticRole = semanticRole;
		this.predicateString = predicateString;
	}
	
	
	public SemanticRole getSemanticRole()
	{
		return semanticRole;
	}
	public String getPredicateString()
	{
		return predicateString;
	}


	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((predicateString == null) ? 0 : predicateString.hashCode());
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
		SemanticRoleLabelByString other = (SemanticRoleLabelByString) obj;
		if (predicateString == null)
		{
			if (other.predicateString != null)
				return false;
		} else if (!predicateString.equalsIgnoreCase(other.predicateString))
			return false;
		if (semanticRole == null)
		{
			if (other.semanticRole != null)
				return false;
		} else if (!semanticRole.equals(other.semanticRole))
			return false;
		return true;
	}




	private final String predicateString;
	private final SemanticRole semanticRole;
}
