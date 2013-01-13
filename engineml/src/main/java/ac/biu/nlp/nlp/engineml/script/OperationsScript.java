package ac.biu.nlp.nlp.engineml.script;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.plugin.InstanceBasedPlugin;
import ac.biu.nlp.nlp.engineml.plugin.Plugin;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * Defines the operations and their ordering that are involved in constructing a proof.
 * <P>
 * A proof is a sequence of operations that transform T to H. The system tries to find
 * proofs, according to the script. Finding a proof is an iterative process: in each iteration
 * the system applies some operations on the trees. The script defines which operations are
 * to be applied in a each iteration. 
 * 
 * @author Asher Stern
 * @since February  2011
 *
 * @param <I> The information of the rules' nodes. Usually {@link Info}
 * @param <S> The type of the rules' nodes. Usually {@link BasicNode}
 */
@NotThreadSafe
public abstract class OperationsScript<I,S extends AbstractNode<I, S>> extends RuleBasesAndPluginsContainer<I,S>
{
	public abstract void init() throws OperationException;
	public abstract void cleanUp();
	
	public void setHypothesisInformation(HypothesisInformation hypothesisInformation) throws TeEngineMlException
	{
		this.hypothesisInformation = hypothesisInformation;
	}
	
	/**
	 * Returns list of operations to be performed in a given iteration.
	 * This is the main method of this class.
	 * @param iterationIndex
	 * @param trees
	 * @return
	 */
	public ImmutableList<SingleOperationItem> getItemListForIteration(int iterationIndex,Set<TreeAndFeatureVector> trees) throws OperationException, TeEngineMlException
	{
		if (null==this.hypothesisInformation)
			throw new TeEngineMlException("Hypothesis information not set!");
		
		return getItemListForIterationImpl(iterationIndex,trees);
	}
	
	public ImmutableList<SingleOperationItem> getItemListForLocalCreativeIteration(int globalIteration, int localIteration, Set<TreeAndFeatureVector> trees) throws OperationException, TeEngineMlException
	{
		if (null==this.hypothesisInformation)
			throw new TeEngineMlException("Hypothesis information not set!");
		
		return getItemListForLocalCreativeIterationImpl(globalIteration,localIteration,trees);
		
	}
	
	
	
	
	
	
	public ByLemmaPosLexicalRuleBase<LexicalRule> getByLemmaPosLexicalRuleBase(String name) throws OperationException
	{
		ByLemmaPosLexicalRuleBase<LexicalRule> ret = null;
		if (byLemmaPosLexicalRuleBases!=null)
		{
			if (byLemmaPosLexicalRuleBases.containsKey(name))
				ret = byLemmaPosLexicalRuleBases.get(name);
		}
		if (null==ret)
			throw new OperationException("rule base: "+name+" does not exist in the operation script.");
		return ret;
	}
	
	public ByLemmaLexicalRuleBase getByLemmaLexicalRuleBase(String name) throws OperationException
	{
		ByLemmaLexicalRuleBase ret = null;
		if (this.byLemmaLexicalRuleBases!=null)
		{
			if (byLemmaLexicalRuleBases.containsKey(name))
				ret = byLemmaLexicalRuleBases.get(name);
		}
		if (null==ret)
			throw new OperationException("rule base: "+name+" does not exist in the operation script.");
		return ret;
	}
	
	public RuleBaseEnvelope<I, S> getRuleBaseEnvelope(String name) throws OperationException
	{
		RuleBaseEnvelope<I, S> ret = null;
		if (ruleBasesEnvelopes!=null)
		{
			if (ruleBasesEnvelopes.containsKey(name))
				ret = ruleBasesEnvelopes.get(name);
		}
		if (null==ret)
			throw new OperationException("rule base: "+name+" does not exist in the operation script.");
		return ret;
	}

	/**
	 * The order is important here. It must be LinkedHashSet.
	 * @return
	 */
	public synchronized LinkedHashSet<String> getRuleBasesNames()
	{
		if (null==ruleBasesNames)
		{
			ruleBasesNames = new LinkedHashSet<String>();
			List<LinkedHashMap<String,?>> ruleBases = new LinkedList<LinkedHashMap<String,?>>();
			if (byLemmaPosLexicalRuleBases!=null)
				ruleBases.add(byLemmaPosLexicalRuleBases);
			if (byLemmaLexicalRuleBases!=null)
				ruleBases.add(byLemmaLexicalRuleBases);
			if (ruleBasesEnvelopes!=null)
				ruleBases.add(ruleBasesEnvelopes);

			for (LinkedHashMap<String,?> ruleBaseMap : ruleBases)
			{
				for (String name : ruleBaseMap.keySet())
				{
					ruleBasesNames.add(name);
				}
			}
			if (null!=metaRuleBasesEnvelopes)
			{
				for (Map.Entry<String,RuleBaseEnvelope<I, S>> metaItem : metaRuleBasesEnvelopes.entrySet())
				{
					LinkedHashSet<String> namesFromMeta = metaItem.getValue().getRuleBasesNames();
					if (namesFromMeta != null)
					{
						for (String nameFromMeta : namesFromMeta)
						{
							ruleBasesNames.add(nameFromMeta);
						}
					}
				}
			}
		}
		return ruleBasesNames;
	}
	
	public RuleBaseEnvelope<I, S> getMetaRuleBaseEnvelope(String metaRuleBaseName) throws OperationException
	{
		RuleBaseEnvelope<I, S> ret = null;
		if (metaRuleBasesEnvelopes!=null)
		{
			if (metaRuleBasesEnvelopes.containsKey(metaRuleBaseName))
			{
				ret = metaRuleBasesEnvelopes.get(metaRuleBaseName);
			}
		}
		if (null==ret)throw new OperationException("Could not find meta rule base: "+metaRuleBaseName);
		return ret;
	}
	
	public Plugin getPlugin(String pluginId) throws TeEngineMlException
	{
		if (!this.mapPlugins.containsKey(pluginId)) throw new TeEngineMlException("Plugin \""+pluginId+"\" does not exist.");
		return this.mapPlugins.get(pluginId);
	}
	public ImmutableSet<InstanceBasedPlugin> getInstanceBasedPlugins() throws TeEngineMlException
	{
		if (null==instanceBasedPlugins) throw new TeEngineMlException("Not initialized");
		return instanceBasedPlugins;
	}
	
	protected abstract ImmutableList<SingleOperationItem> getItemListForIterationImpl(int iterationIndex,Set<TreeAndFeatureVector> trees) throws OperationException, TeEngineMlException;
	
	protected abstract ImmutableList<SingleOperationItem> getItemListForLocalCreativeIterationImpl(int globalIteration, int localIteration, Set<TreeAndFeatureVector> trees) throws OperationException, TeEngineMlException;
	
	
	
	// Order is crucial here, since featureUpdate uses that order.
	// See method getRuleBasesNames().
	protected LinkedHashMap<String, ByLemmaPosLexicalRuleBase<LexicalRule>> byLemmaPosLexicalRuleBases = null;
	protected LinkedHashMap<String,ByLemmaLexicalRuleBase> byLemmaLexicalRuleBases = null;
	protected LinkedHashMap<String,RuleBaseEnvelope<I, S>> ruleBasesEnvelopes = null;
	protected LinkedHashMap<String,RuleBaseEnvelope<I, S>> metaRuleBasesEnvelopes = null;
	
	private LinkedHashSet<String> ruleBasesNames = null;
	
	protected ImmutableMap<String, ? extends Plugin> mapPlugins;
	protected ImmutableSet<InstanceBasedPlugin> instanceBasedPlugins = null;
	
	protected HypothesisInformation hypothesisInformation = null;
}
