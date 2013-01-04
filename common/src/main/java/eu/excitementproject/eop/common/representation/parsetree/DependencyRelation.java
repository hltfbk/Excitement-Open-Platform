package eu.excitementproject.eop.common.representation.parsetree;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parsetree.DependencyRelationType;

/**
 * 
 * [DELETEME_LATER: Imported from BIUTEE 2.4.1 with no modification - but note that the <code>DependencyRelationType</code> is a new type]
 * dependency relation is the relation between a node in the dependency
 * parse tree and its parent.
 * For example: subject, object.
 * <P>
 * The relation is represented as a string, and as a canonical representation by 
 * {@link DependencyRelationType}. 
 * 
 * <P>
 * This class is IMMUTABLE
 * 
 * 
 * @author Asher Stern
 *
 */
public class DependencyRelation implements Serializable
{
	
	private static final long serialVersionUID = -1407618627652677290L;
	
	/**
	 * 
	 * @param stringRepresentation free string representation.
	 * for example "subj", "subject", "s".
	 * @param type either {@link DependencyRelationType} or <code> null </code>
	 * Note: <code> null </code> is absolutely legal here.
	 */
	public DependencyRelation(String stringRepresentation,DependencyRelationType type)
	{
		this.itsStringRepresentation = stringRepresentation;
		this.type = type;
	}
	
	public String getStringRepresentation()
	{
		return this.itsStringRepresentation;
	}
	
	public DependencyRelationType getType()
	{
		return this.type;
	}
	
	
	@Override
	public String toString()
	{
		String ret = null;
		ret = ((itsStringRepresentation==null)?"null":itsStringRepresentation);
		return ret;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((itsStringRepresentation == null) ? 0
						: itsStringRepresentation.hashCode());
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
		DependencyRelation other = (DependencyRelation) obj;
		if (itsStringRepresentation == null)
		{
			if (other.itsStringRepresentation != null)
				return false;
		} else if (!itsStringRepresentation
				.equals(other.itsStringRepresentation))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	protected String itsStringRepresentation;
	protected DependencyRelationType type = null; // null is legal here. 
											      // Gil: should it also be legal on EXCITEMENT? (we now have canonical dep rel). NEED2TALK  
}
