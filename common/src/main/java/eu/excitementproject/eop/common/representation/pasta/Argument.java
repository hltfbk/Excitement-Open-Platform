package eu.excitementproject.eop.common.representation.pasta;

import java.io.Serializable;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * An {@link Argument} is a set of nodes that represent an argument.
 * The argument has a semantic head, which is the word that represents the argument itself.
 * It also has a syntactic head - the head of the subtree of the argument.
 * If the connection between the predicate to the argument is indirect - via an "antecedent" node -
 * then the argument has a "syntactic representative", which is the "antecedent" node. Otherwise,
 * the "syntactic representative" is null.
 * 
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 * @param <I>
 * @param <S>
 */
public class Argument<I extends Info, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = -3907923916740146986L;
	
	public Argument(Set<S> nodes, S semanticHead, S syntacticHead, S syntacticRepresentative)
	{
		super();
		this.nodes = nodes;
		this.semanticHead = semanticHead;
		this.syntacticHead = syntacticHead;
		this.syntacticRepresentative = syntacticRepresentative;
	}
	
	public Argument(Set<S> nodes, S semanticHead, S syntacticHead)
	{
		super();
		this.nodes = nodes;
		this.semanticHead = semanticHead;
		this.syntacticHead = syntacticHead;
		this.syntacticRepresentative = null;
	}
	
	

	public Set<S> getNodes()
	{
		return nodes;
	}

	public S getSemanticHead()
	{
		return semanticHead;
	}

	public S getSyntacticHead()
	{
		return syntacticHead;
	}

	public S getSyntacticRepresentative()
	{
		return syntacticRepresentative;
	}
	
	
	
	
	@Override
	public String toString()
	{
		return "Argument: "+((getSemanticHead()!=null)?InfoGetFields.getLemma(getSemanticHead().getInfo()):"null");
	}



	private final Set<S> nodes;
	private final S semanticHead;
	private final S syntacticHead;
	private final S syntacticRepresentative;
}
