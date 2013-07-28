package eu.excitementproject.eop.common.representation.pasta;

import java.io.Serializable;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * A predicate. It may be either verbal, or nominal. If it is a nominal argument, then the corresponding verbal form of that argument
 * (that does <B>not</B> appear (usually) in the sentence) is given in the field {@link #verbsForNominal}. Since there might be
 * several verbal forms for a given nominal predicate, this field is a list, rather than just one string.
 * For example "flight" is a nominal predicate, with verbal form "fly".
 * 
 * This class represents the predicate as a set of nodes (which might contain only a single element, in many cases), the head
 * (which is the syntactic head), and optionally the verbal form of the predicate, if it is a nominal predicate.
 * 
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 * @param <I>
 * @param <S>
 */
public class Predicate<I extends Info, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 1270071998746507933L;
	
	public Predicate(Set<S> nodes, S head)
	{
		super();
		this.nodes = nodes;
		this.head = head;
		this.verbsForNominal = null;
	}
	
	public Predicate(Set<S> nodes, S head, ImmutableList<String> verbsForNominal)
	{
		super();
		this.nodes = nodes;
		this.head = head;
		this.verbsForNominal = verbsForNominal;
	}




	public Set<S> getNodes()
	{
		return nodes;
	}
	public S getHead()
	{
		return head;
	}
	public ImmutableList<String> getVerbsForNominal()
	{
		return verbsForNominal;
	}
	
	@Override
	public String toString()
	{
		String headString = null;
		if (getHead()!=null){if (getHead().getInfo()!=null)
		{
			headString = getHead().getInfo().toString();
		}}
		return "Predicate: "+headString;
	}



	private final Set<S> nodes;
	private final S head;
	private final ImmutableList<String> verbsForNominal; // null for verbs.
}
