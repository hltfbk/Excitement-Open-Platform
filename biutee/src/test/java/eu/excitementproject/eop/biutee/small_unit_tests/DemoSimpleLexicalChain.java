package eu.excitementproject.eop.biutee.small_unit_tests;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.builtin_knowledge.ConstructorOfLexicalResourcesForChain;
import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.LexicalRuleWithName;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.builder.BuilderSetOfWords;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.builder.SimpleLexicalChainRuleBase;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;

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
		BuilderSetOfWords builder = new BuilderSetOfWords(con.constructResources(),params.getInt(TransformationsConfigurationParametersNames.SIMPLE_LEXICAL_CHAIN_DEPTH_PARAMETER_NAME));
		System.out.println("2");
		Set<LemmaAndPos> setWords = new LinkedHashSet<LemmaAndPos>();
		setWords.add(new LemmaAndPos("machine", new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN)));
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
					System.out.print(rule.getLhsLemma()+"["+simplerPos(rule.getLhsPos().getCanonicalPosTag()).name()+"] => "+rule.getRhsLemma()+"["+simplerPos(rule.getRhsPos().getCanonicalPosTag()).name()+"] ");
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
