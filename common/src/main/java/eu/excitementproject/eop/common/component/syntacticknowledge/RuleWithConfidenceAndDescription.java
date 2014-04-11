package eu.excitementproject.eop.common.component.syntacticknowledge;
import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * A {@link SyntacticRule} with confidence (a double) and description (a string).
 * 
 * @author Asher Stern
 * @since Feb 14, 2011
 *
 * @param <I>
 * @param <S>
 */
public class RuleWithConfidenceAndDescription<I, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 302928681803343343L;
	
	public RuleWithConfidenceAndDescription(SyntacticRule<I, S> rule, double confidence, String description)
	{
		super();
		this.rule = rule;
		this.confidence = confidence;
		this.description = description;
	}
	
	
	
	public SyntacticRule<I, S> getRule()
	{
		return rule;
	}
	public double getConfidence()
	{
		return confidence;
	}
	public String getDescription()
	{
		return description;
	}



	protected SyntacticRule<I,S> rule;
	protected double confidence;
	protected String description;
}
