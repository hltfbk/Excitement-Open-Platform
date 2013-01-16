package eu.excitementproject.eop.transformations.representation.srl_types;

import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2011
 *
 */
public class SemanticRole implements Serializable
{
	private static final long serialVersionUID = 2405008197793922086L;
	
	public static SemanticRole createArgument(int argumentNumber)
	{
		return new SemanticRole(SemanticRoleType.ARGUMENT,argumentNumber,null);
		
	}

	public static SemanticRole createArgumentCauser()
	{
		return new SemanticRole(SemanticRoleType.ARGUMENT_CAUSER,null,null);
		
	}

	public static SemanticRole createModifier(String modifierType)
	{
		return new SemanticRole(SemanticRoleType.MODIFIER,null,modifierType);
	}



	public boolean isArgument()
	{
		return type.isArgument();
	}


	public Integer getArgumentNumber()
	{
		return argumentNumber;
	}


	public String getModifierType()
	{
		return modifierType;
	}
	
	public SemanticRoleType getType()
	{
		return type;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argumentNumber == null) ? 0 : argumentNumber.hashCode());
		result = prime * result
				+ ((modifierType == null) ? 0 : modifierType.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SemanticRole other = (SemanticRole) obj;
		if (argumentNumber == null)
		{
			if (other.argumentNumber != null)
				return false;
		} else if (!argumentNumber.equals(other.argumentNumber))
			return false;
		if (modifierType == null)
		{
			if (other.modifierType != null)
				return false;
		} else if (!modifierType.equals(other.modifierType))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	protected SemanticRole(SemanticRoleType type, Integer argumentNumber, String modifierType)
	{
		this.type = type;
		this.argumentNumber = argumentNumber;
		this.modifierType = modifierType;
	}


	private final SemanticRoleType type;
	private final Integer argumentNumber;
	private final String modifierType;
}
