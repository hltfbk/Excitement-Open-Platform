package eu.excitementproject.eop.transformations.representation.srl_types;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2011
 *
 */
public class WordLabeledWithSemanticRole implements Serializable
{
	private static final long serialVersionUID = 7481425096269542476L;
	
	public WordLabeledWithSemanticRole(String word,
			Set<SemanticRoleLabelByIndex> semanticRoles)
	{
		super();
		this.word = word;
		this.semanticRoles = semanticRoles;
	}
	
	
	public String getWord()
	{
		return word;
	}
	public Set<SemanticRoleLabelByIndex> getSemanticRoles()
	{
		return semanticRoles;
	}
	
	
	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((semanticRoles == null) ? 0 : semanticRoles.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		WordLabeledWithSemanticRole other = (WordLabeledWithSemanticRole) obj;
		if (semanticRoles == null)
		{
			if (other.semanticRoles != null)
				return false;
		} else if (!semanticRoles.equals(other.semanticRoles))
			return false;
		if (word == null)
		{
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}





	private final String word;
	private final Set<SemanticRoleLabelByIndex> semanticRoles;
}
