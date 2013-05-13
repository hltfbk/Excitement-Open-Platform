package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.MiniparPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.lexical.LinDependencyFromDBLexicalRuleBase;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TimeElapsedTracker;

@Deprecated
public class DemoByLemmaPosLexicalRuleBase
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)throw new TeEngineMlException("configuration file was not specified");
			new LogInitializer(args[0]).init();
			ConfigurationFile configurationFile = new ConfigurationFile(args[0]);
			configurationFile.setExpandingEnvironmentVariables(true);
			DemoByLemmaPosLexicalRuleBase app = new DemoByLemmaPosLexicalRuleBase(configurationFile);
			app.test();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}
	
	public DemoByLemmaPosLexicalRuleBase(ConfigurationFile configurationFile)
	{
		super();
		this.configurationFile = configurationFile;
	}
	public void test() throws RuleBaseException, ConfigurationException, IOException, UnsupportedPosTagStringException
	{
		ConfigurationParams confParams = configurationFile.getModuleConfiguration("LinDependencySimilarity");
		// ruleBase = new WordNetLexicalRuleBase(confParams);
		//ruleBase = new GeoLexicalRuleBase(confParams);
		//ruleBase = new LinDependencySimilarityLexicalRuleBase(confParams);
		ruleBase = new LinDependencyFromDBLexicalRuleBase(confParams);
		// ruleBase = BapFromDBLexicalRuleBase.fromConfigurationParams(confParams);
		// ruleBase = new WikipediaLexicalRuleBase(confParams);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			while(true)
			{
				System.out.print("Enter lemma: ");
				String lemma = reader.readLine();
				System.out.println();
				if (lemma.equalsIgnoreCase("bye")) break;
				System.out.print("Enter pos: ");
				String posString = reader.readLine();
				PartOfSpeech pos = new MiniparPartOfSpeech(posString);
				System.out.println();
				
				TimeElapsedTracker tracker = new TimeElapsedTracker();
				tracker.start();
				ImmutableSet<LexicalRule> rules = ruleBase.getRules(lemma, pos);
				tracker.end();
				System.out.println("Printing rules:");
				for (LexicalRule rule : rules)
				{
					System.out.println(rule.getLhsLemma()+":"+rule.getLhsPos()+" -> "+rule.getRhsLemma()+":"+rule.getLhsPos()+" with confidence: "+String.format("%-4.4f", rule.getConfidence()) );
				}
				System.out.println("Times: "+tracker.toString());
			}
		}
		finally
		{
			reader.close();
		}
		
		
		
	}

	private ConfigurationFile configurationFile;
	private ByLemmaPosLexicalRuleBase<LexicalRule> ruleBase;
	
}
