package eu.excitementproject.eop.biutee.script;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.biutee.plugin.Plugin;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * A container of rule-bases and plug-ins.
 * Note that an {@link OperationsScript} is a rule-bases-and-plug-ins container.
 * 
 * 
 * @see OperationsScript
 * 
 * @author Asher Stern
 * @since Apr 3, 2011
 *
 */
@NotThreadSafe
public abstract class RuleBasesAndPluginsContainer<I,S extends AbstractNode<I, S>>
{
	public abstract LinkedHashSet<String> getRuleBasesNames();
	
	public abstract ByLemmaPosLexicalRuleBase<LexicalRule> getByLemmaPosLexicalRuleBase(String name) throws OperationException;
	
	public abstract ByLemmaLexicalRuleBase getByLemmaLexicalRuleBase(String name) throws OperationException;
	
	public abstract RuleBaseEnvelope<I, S> getRuleBaseEnvelope(String name) throws OperationException;
	
	public abstract RuleBaseEnvelope<I, S> getMetaRuleBaseEnvelope(String metaRuleBaseName) throws OperationException;
	
	public abstract Plugin getPlugin(String pluginId) throws TeEngineMlException;
}
