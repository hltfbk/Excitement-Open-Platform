package eu.excitementproject.eop.transformations.operations.rules;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * A {@link BagOfRulesRuleBase} rule base which stores the rules in an {@link ImmutableSet}
 * 
 * @author Asher Stern
 * @since Feb 24, 2011
 *
 * @param <I>
 * @param <S>
 */
public class SetBagOfRulesRuleBase<I, S extends AbstractNode<I, S>> implements BagOfRulesRuleBase<I, S>
{
	protected SetBagOfRulesRuleBase()
	{}
	
	public static <I, S extends AbstractNode<I, S>> SetBagOfRulesRuleBase<I,S> fromSetWithConfidenceAndDescription(Set<RuleWithConfidenceAndDescription<I, S>> setRules)
	{
		SetBagOfRulesRuleBase<I,S> ret = new SetBagOfRulesRuleBase<I, S>();
		ret.setOfRules = new ImmutableSetWrapper<RuleWithConfidenceAndDescription<I,S>>(setRules);
		return ret;
	}
	
	public SetBagOfRulesRuleBase(Set<SyntacticRule<I,S>> simpleSet) throws RuleBaseException
	{
		Set<RuleWithConfidenceAndDescription<I, S>> setWithConfidenceAndDescription =
			new LinkedHashSet<RuleWithConfidenceAndDescription<I,S>>();
		
		for (SyntacticRule<I,S> rule : simpleSet)
		{
			setWithConfidenceAndDescription.add(
					new RuleWithConfidenceAndDescription<I, S>(rule, EMINUS1, "generic rule"));
		}
		this.setOfRules = new ImmutableSetWrapper<RuleWithConfidenceAndDescription<I,S>>(setWithConfidenceAndDescription);
	}
	
	public static <I, S extends AbstractNode<I, S>> SetBagOfRulesRuleBase<I,S> fromSimpleSerializationFile(File file) throws RuleBaseException, ClassNotFoundException
	{
		SetBagOfRulesRuleBase<I,S> ret = null;
		try
		{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			try
			{
				@SuppressWarnings("unchecked")
				Set<SyntacticRule<I,S>> simpleSet = (Set<SyntacticRule<I,S>>) inputStream.readObject();
				ret = new SetBagOfRulesRuleBase<I,S>(simpleSet);
			}
			finally
			{
				inputStream.close();
			}
			
			return ret;
			
		} catch (FileNotFoundException e)
		{
			throw new RuleBaseException("Could not load rules from serialization file: "+file.getAbsolutePath(),e);
		} catch (IOException e)
		{
			throw new RuleBaseException("Could not load rules from serialization file: "+file.getAbsolutePath(),e);
		}


	}
	
	
	public ImmutableSet<RuleWithConfidenceAndDescription<I, S>> getRules()
	{
		return setOfRules;
	}
	
	
	private ImmutableSet<RuleWithConfidenceAndDescription<I, S>> setOfRules;
	private static final double EMINUS1 = Math.exp(-1);

}
