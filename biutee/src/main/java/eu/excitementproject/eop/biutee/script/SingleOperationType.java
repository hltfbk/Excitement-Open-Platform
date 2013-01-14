package eu.excitementproject.eop.biutee.script;
import eu.excitementproject.eop.biutee.rteflow.micro.perform.PerformFactoryFactory;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;

/**
 * A type of a generation-operation, that can be applied on a tree.
 * 
 * @see OperationsScript
 * @see PerformFactoryFactory
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public enum SingleOperationType
{
	UNJUSTIFIED_INSERTION(false),
	UNJUSTIFIED_MOVE(false),
	PARSER_ANTECEDENT_SUBSTITUTION(false),
	MULTIWORD_SUBSTITUTION(false),
	FLIP_POS_SUBSTITUTION(false),
	COREFERENCE_SUBSTITUTION(false),
	IS_A_COREFERENCE_CONSTRUCTION(false),
	CHANGE_PREDICATE_TRUTH(false),
	RULE_APPLICATION(true),
	LEXICAL_RULE_BY_LEMMA_APPLICATION(true),
	LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION(true),
	LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D(true),
	
	/**
	 * <p>An operation that replaces a text-node with a generic hypothesis-node, for example,
	 * replace a noun node like "world" with the variable specifier "{noun}". 
	 * @see ac.biu.nlp.nlp.engineml.variables.DefaultCollectionOfHypothesisVariableSpecification  
	 * @see ac.biu.nlp.nlp.engineml.variables.RulesOnTheFlyFinder
	 * @see ac.biu.nlp.nlp.engineml.rteflow.macro.Feature#VARIABLE_GENERALIZATION
	 */
	VARIABLE_GENERALIZATION(true),
	
	/**
	 * This type refers to special types of rule applications.
	 * Currently, the only "special" type of rule application is the usage of an external
	 * chaining, done outside the system of lexical rules (Eyal's component).
	 * @see RuleBaseEnvelope#getChainOfLexicalRulesRuleBase()
	 */
	META_RULE_APPLICATION(true),
	
	PLUGIN_APPLICATION(false)
	;
	
	public boolean isRuleApplication()
	{
		return ruleApplication;
	}
	
	
	
	private SingleOperationType(boolean ruleApplication)
	{
		this.ruleApplication = ruleApplication;
	}
	private final boolean ruleApplication;
}
