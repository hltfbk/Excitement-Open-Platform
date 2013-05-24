package eu.excitementproject.eop.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.IEditDistanceTEDecision;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerDE;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.textpro.TextProTaggerIT;
import static java.nio.file.StandardCopyOption.*;

@SuppressWarnings("unused")
public class Demo {

	private static String lapAnnotDir = "./target/";
	
	private static String baseConfigFile = "configuration_example.xml";
	private static String configFileDir = "./";
	private static String configFileName;
	private static File configFile;
	
	private static CommonConfig config;
	
	private static LAPAccess lap = null;
			
	private static EDABasic<?> eda;
	
	private static String configSection = "PlatformConfiguration";
	
	/**
	 * @param args
	 */


	private static void initializeLAP(DemoCmdOptions option) {
		String language;
		if (option.language != null) {
			language = option.language.toUpperCase();
		} else {
			language = ConfigFileUtils.getAttribute(configFile, "language");
		}
		
		try {
			if (language.matches("IT")) {
				lap = new TextProTaggerIT();
			} else if (language.matches("EN")) {
//				lap = new TreeTaggerEN();
				lap = new OpenNLPTaggerEN();
			} else if (language.matches("DE")) {
				lap = new OpenNLPTaggerDE();
			}		
		} catch (LAPException e) {
			System.err.println("Error initializing LAP");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void runLAPOnFile(String inputFile, String outDir) {
		
		System.out.println("Running lap on file: " + inputFile + " // writing output to directory " + outDir);
		
		try {
			lap.processRawInputFormat(new File(inputFile), new File(outDir));
		} catch (LAPException e) {
			System.err.println("Error running the LAP");
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	private static JCas runLAP(String text, String hypothesis) {
		JCas aJCas = null;
		try {
			aJCas = lap.generateSingleTHPairCAS(text, hypothesis);
			PlatformCASProber.probeCasAndPrintContent(aJCas, System.out);
		} catch (LAPException e) {
			System.err.println("Error running the LAP");
			e.printStackTrace();
			System.exit(1);			
		}
		
		return aJCas;
	}
	
	
	private static void editConfigFile(DemoCmdOptions option) {
		try {
			configFileName = baseConfigFile + ".edited.xml";
			configFile = new File(configFileName);
			config = new ImplCommonConfig(ConfigFileUtils.editConfigFile(baseConfigFile, configFile, option));
		} catch (Exception e) {
			System.err.println("Could not generate a configuration file object");
			e.printStackTrace();
		}
	}
		
	private static void chooseConfigFile(DemoCmdOptions option) {
		
		configFileName = option.config;
		configFile = new File(configFileName);
		
		System.out.println("Configuration file: " + configFileName);
		
		try {
			config = new ImplCommonConfig(configFile);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private static String getOptionValue(String fileOption, String string) {
		
		if (fileOption == null || fileOption.isEmpty()) {
			fileOption = ConfigFileUtils.getAttribute(configFile,string);
		}
		
		return fileOption;
	}
	
	
	private static void runEOPTrain(String trainFile, String trainDir) {
		try {
			runLAPOnFile(trainFile, trainDir);
			eda.startTraining(config);
			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EDAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// copy the generated files to the given directory
	private static void runEOPTest(String testFile, String testDirStr, String outDir) {
		String resultsFile = configFile + "_Result.txt";
		String xmlResultsFile = outDir + "/results.xml";
		Path source;
		Path target = Paths.get(outDir + "/report.xml");
				
		try {
//			source = Paths.get(resultsFile);
//			Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
			
			runLAPOnFile(testFile,testDirStr);
			
			File testDir = new File(testDirStr);
			OutputStream out = Files.newOutputStream(Paths.get(resultsFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, System.out);
				TEDecision teDecision = eda.process(cas);
				// System.err.println(teDecision1.getDecision().toString()) ;
				
				writer.write(OutputUtils.getPairID(cas) + "\t" + OutputUtils.getGoldLabel(cas) + "\t"  + teDecision.getDecision().toString() + "\t" + teDecision.getConfidence() + "\n");				
			}
			writer.close();
			out.close();
			
			OutputUtils.generateXMLResults(testFile, resultsFile, xmlResultsFile);
			
			System.out.println("Results file: " + xmlResultsFile);
			System.out.println("Evaluation file: " + target.getFileName());
			
		} catch (Exception e) {
			System.out.println("Error copying run output files " + resultsFile + " to directory " + outDir);
			e.printStackTrace();
		}
		
	}
	
	
	private static void runEOPSinglePair(DemoCmdOptions option) {
		System.out.println("Text: " + option.text);
		System.out.println("Hypothesis: " + option.hypothesis);
		
		JCas aJCas = runLAP(option.text, option.hypothesis);
		try {
			TEDecision te = eda.process(aJCas);
			System.out.println("T/H pair processing result: " + te.getDecision() + " with confidence " + te.getConfidence());
			OutputUtils.makeSinglePairXML(te, aJCas, option.output, option.language);
		} catch (EDAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
		
	
	private static void showHelp(CmdLineParser parser) {
		System.out.println("Demo  [options ...] [arguments ...]");
		parser.printUsage(System.out);
	}
	
	public static void main(String[] args) {
		
		DemoCmdOptions option = new DemoCmdOptions();
		CmdLineParser parser = new CmdLineParser(option);

		@SuppressWarnings("rawtypes")
		DemoInitializationHelper<?> dih = new DemoInitializationHelper();
		
		if (args.length == 0){
			showHelp(parser);
		}
		try{ 
			parser.parseArgument(args);

			if (option.dir != null) {
				configFileDir = option.dir;
			}
			
//			editConfigFile(option);
			chooseConfigFile(option);
			initializeLAP(option);
			
			System.out.println("Initializing EDA from file " + config.getConfigurationFileName());	

			eda = dih.startEngineBasic(configFile);
			System.out.println("EDA object created from class " + eda.getClass());
			
			
			if (option.train) {
				
//				eda.initialize(config);
				
				String trainFile = getOptionValue(option.trainFile, "trainFile");
				String trainDir = getOptionValue(option.trainDir, "trainDir");
				runEOPTrain(trainFile, trainDir);
			}
			
			if (option.test) {
				
				eda.initialize(config);

				if (option.testFile != null) {
					String testFile = getOptionValue(option.testFile, "testFile");
					String testDir = getOptionValue(option.testDir, "testDir");
					runEOPTest(testFile, testDir, option.output);
				} else {
					runEOPSinglePair(option);
				}
			}
			eda.shutdown();

		} catch(CmdLineException | ConfigurationException | EDAException | ComponentException e) {
			e.printStackTrace();
		}
	}

}
