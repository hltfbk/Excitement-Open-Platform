package ac.biu.nlp.nlp.engineml.small_unit_tests;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

import ac.biu.nlp.nlp.engineml.builtin_knowledge.ConstructorOfLexicalResourcesForChain;
import ac.biu.nlp.nlp.engineml.builtin_knowledge.KnowledgeResource;
import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.datastructures.LemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.LexicalRuleWithName;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.builder.BuilderSetOfWords;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.builder.SimpleLexicalChainRuleBase;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;

/**
 * 
 * @author Asher Stern
 * @since Jan 22, 2012
 *
 */
public class DemoSimpleLexicalChain
{
	public DemoSimpleLexicalChain(ConfigurationFile configurationFile)
	{
		super();
		this.configurationFile = configurationFile;
		this.configurationFile.setExpandingEnvironmentVariables(true);
	}

	public void go() throws ConfigurationException, TeEngineMlException, LexicalResourceException, UnsupportedPosTagStringException, RuleBaseException, IOException
	{
		ConfigurationParams params = 
				configurationFile.getModuleConfiguration(KnowledgeResource.SIMPLE_LEXICAL_CHAIN.getModuleName());
		ConstructorOfLexicalResourcesForChain con = 
				new ConstructorOfLexicalResourcesForChain(configurationFile,params);
		System.out.println("1");
		BuilderSetOfWords builder = new BuilderSetOfWords(con.constructResources(),params.getInt(ConfigurationParametersNames.SIMPLE_LEXICAL_CHAIN_DEPTH_PARAMETER_NAME));
		System.out.println("2");
		Set<LemmaAndPos> setWords = new LinkedHashSet<LemmaAndPos>();
		setWords.add(new LemmaAndPos("machine", new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN)));
		ImmutableSet<LemmaAndPos> imSetWords = new ImmutableSetWrapper<LemmaAndPos>(setWords);
		System.out.println("3");
		builder.createRuleBase(imSetWords);
		System.out.println("4");
		Map<CanonicalLemmaAndPos, ImmutableSet<ChainOfLexicalRules>> rules =  builder.getRulesForRuleBase();
		System.out.println("rules.keySet().size() = "+rules.keySet().size());
		System.out.println("5");
		SimpleLexicalChainRuleBase ruleBase = new SimpleLexicalChainRuleBase(this);
		ruleBase.setRules(rules, this);
		System.out.println("6");
		for (CanonicalLemmaAndPos lhs : rules.keySet())
		{
			for (ChainOfLexicalRules chain : rules.get(lhs))
			{
				
				for (LexicalRuleWithName rule_ : chain.getChain())
				{
					LexicalRule rule = rule_.getRule();
					System.out.print(rule.getLhsLemma()+"["+rule.getLhsPos().getCanonicalPosTag().name()+"] => "+rule.getRhsLemma()+"["+rule.getRhsPos().getCanonicalPosTag().name()+"] ");
				}
				System.out.println();
			}
		}
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			new LogInitializer(args[0]).init();
			DemoSimpleLexicalChain app = new DemoSimpleLexicalChain(new ConfigurationFile(args[0]));
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	private ConfigurationFile configurationFile;

}
