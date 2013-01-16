package eu.excitementproject.eop.transformations.operations.specifications;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
public class IsASpecification extends Specification
{
	private static final long serialVersionUID = 5670521182408219610L;

	/**
	 * A constructor for "X is Y"
	 * @param entity1 the "X"
	 * @param entity2 the "Y"
	 * @param involvedNodes Usually should be <code>null</code>.
	 */
	public IsASpecification(ExtendedNode entity1, ExtendedNode entity2, Set<ExtendedNode> involvedNodes)
	{
		this.entity1 = entity1;
		this.entity2 = entity2;
		this.involvedNodes = involvedNodes;
	}

	public ExtendedNode getEntity1()
	{
		return entity1;
	}



	public ExtendedNode getEntity2()
	{
		return entity2;
	}



	@Override
	public Set<ExtendedNode> getInvolvedNodesInTree()
	{
		return involvedNodes;
	}

	@Override
	public StringBuffer specString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Is-A: ");
		sb.append(InfoGetFields.getLemma(entity1.getInfo()));
		sb.append(" = ");
		sb.append(InfoGetFields.getLemma(entity2.getInfo()));
		
		return sb;
	}

	@Override
	public String toShortString()
	{
		if (null==shortString)
			shortString = specString().toString();
		
		return shortString;
	}

	private final ExtendedNode entity1;
	private final ExtendedNode entity2;
	private final Set<ExtendedNode> involvedNodes;
	
	private transient String shortString = null;
}
