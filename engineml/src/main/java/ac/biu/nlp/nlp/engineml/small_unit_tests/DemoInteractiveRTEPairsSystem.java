package ac.biu.nlp.nlp.engineml.small_unit_tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.PairProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.interactive.RTEPairsSingleThreadInteractiveSystem;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.TreeHistoryUtilities;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitterException;

public class DemoInteractiveRTEPairsSystem
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DemoInteractiveRTEPairsSystem app = new DemoInteractiveRTEPairsSystem(args[0],args[1],args[2]);
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}

	}


	
	public DemoInteractiveRTEPairsSystem(String configurationFileName,
			String preprocessConfigurationModuleName,
			String trainAndTestConfigurationModuleName)
	{
		super();
		this.configurationFileName = configurationFileName;
		this.preprocessConfigurationModuleName = preprocessConfigurationModuleName;
		this.trainAndTestConfigurationModuleName = trainAndTestConfigurationModuleName;
	}



	public void go() throws TeEngineMlException, IOException, ConfigurationFileDuplicateKeyException, PluginAdministrationException, ConfigurationException, LemmatizerException, TextPreprocessorException, SentenceSplitterException, NamedEntityRecognizerException, ParserRunException, CoreferenceResolutionException, TreeCoreferenceInformationException, TreeStringGeneratorException, OperationException, ClassifierException, AnnotatorException, ScriptException, RuleBaseException
	{
		RTEPairsSingleThreadInteractiveSystem system =
				new RTEPairsSingleThreadInteractiveSystem(configurationFileName,
						preprocessConfigurationModuleName,
						trainAndTestConfigurationModuleName);
		
		system.initLogger();
		system.init();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("At any time, type \"exit\" for exit.");
			int pairId=1;
			while (true)
			{
				System.out.println("Enter text:");
				String text = reader.readLine();
				if ("exit".equals(text)) break;
				System.out.println("Enter hypothesis:");
				String hypothesis = reader.readLine();
				if ("exit".equals(hypothesis)) break;

						
				PairProcessor processor = system.processPair(new TextHypothesisPair(text, hypothesis, pairId++, "IR"));
				processor.process();
				boolean result = system.getResult(processor.getBestTree());
				System.out.println("Result = "+result);

				System.out.println("Proof is:\n"+TreeHistoryUtilities.historyToString(processor.getBestTreeHistory()));
				System.out.println(StringUtil.generateStringOfCharacter('=', 60));
				System.out.println();
			}
		}
		finally
		{
			system.cleanUp();
		}

	}

	private String configurationFileName;
	private String preprocessConfigurationModuleName;
	private String trainAndTestConfigurationModuleName;
}
