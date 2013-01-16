package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * 
 * @author Asher Stern
 * @since Feb 14, 2011
 *
 */
public class AnnotationRuleWithDescription<N extends AbstractNode<? extends Info, N>, A> implements Serializable
{
	
	private static final long serialVersionUID = 1710057779157234688L;

	public AnnotationRuleWithDescription(AnnotationRule<N, A> rule, String description)
	{
		super();
		this.rule = rule;
		this.description = description;
	}
	
	
	
	public AnnotationRule<N, A> getRule()
	{
		return rule;
	}
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return description;
	}

	protected AnnotationRule<N, A> rule;
	protected String description;
}
