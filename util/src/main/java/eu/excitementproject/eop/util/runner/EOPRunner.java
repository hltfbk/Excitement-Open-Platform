package eu.excitementproject.eop.util.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.EditDistanceTEDecision;
import eu.excitementproject.eop.core.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerDE;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.textpro.TextProTaggerIT;
import eu.excitementproject.eop.util.eval.EDAScorer;
import static java.nio.file.StandardCopyOption.*;

/**
 * 
 * @author Vivi Nastase (FBK)
 *
 * Class for processing data with the EOP. 
 * 
 * It can be used for training and/or testing on train/test files or 
 * given test/hypothesis pairs. 
 *
 */
@SuppressWarnings("unused")
public class EOPRunner {

	// command line options
	private EOPRunnerCmdOptions option;
	private EOPRunnerInitializationHelper<?> dih;
	
	private String language;
	private String lapAnnotDir = "/tmp/";
	
	private String baseConfigFile = "";
	private String configFileDir = "";
	private String configFileName;
	private File configFile;
	private String hasGoldLabel = null;

	private String xmlResultsFile = null;
	
	private CommonConfig config;
	
	private LAPAccess lap = null;
			
	private EDABasic<?> eda;
	
	private String configSection = "PlatformConfiguration";

	private Logger logger;

	
	/**
	 * @param args
	 */

	/**
	 * EOPRunner object constructor: 
	 *   - initializes the demo, 
	 *   - parses the command line arguments
	 *   - sets value to various parameters
	 *   - initializes the LAP
	 *   
	 * @param args -- command line arguments
	 */
	@SuppressWarnings("rawtypes")
	public EOPRunner(String[] args) {
		
		option = new EOPRunnerCmdOptions();
		CmdLineParser parser = new CmdLineParser(option);

		dih = new EOPRunnerInitializationHelper();
		
		logger = Logger.getLogger("eu.excitementproject.eop.util.runner.EOPRunner");
		
		if (args.length == 0){
			showHelp(parser);
		}
		try{ 
			parser.parseArgument(args);

			if (option.dir != null) {
				configFileDir = option.dir;
			}
			
//			editConfigFile(option);
			chooseConfigFile();
			initializeLAP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the LAP based on the lap parameter (if given) and the language information in the configuration file
	 * 
	 * @param option -- (parsed) command arguments
	 */
	public void initializeLAP() {
		if (option.language != null) {
			language = option.language.toUpperCase();
		} else {
			language = ConfigFileUtils.getAttribute(configFile, "language");
		}
		
		String lapClassName = getLAPClass(option, configFile, language);	
		
		logger.info("LAP initialized from class " + lapClassName);
		
		try {
				Class<?> lapClass = Class.forName(lapClassName);
				Constructor<?> lapClassConstructor = lapClass.getConstructor();
				lap = (LAPAccess) lapClassConstructor.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("Error initializing LAP : " + e.getClass());
			e.printStackTrace();
			System.exit(1);
		} 
	}

	
	/**
	 * Determine the class of the chosen LAP tool
	 * 
	 * @param option -- command-line arguments
	 * @param language -- language option
	 * @return -- the name of the class for the chosen LAP
	 */
	private String getLAPClass(EOPRunnerCmdOptions option, File configFile, String language) {
				
	  String lapClass = "eu.excitementproject.eop.lap";
	  
	  if (option.lap != null) {
		  if (option.lap.matches("(?i).*TextPro.*")) {
			  // there is TextPro only for Italian
			  lapClass += ".textpro.TextProTaggerIT";
		  } else {
			  if (option.lap.matches("(?i).*OpenNLP.*")) {
				  lapClass += ".dkpro.OpenNLPTagger" + language;
			  } else {
				  if (option.lap.matches("(?i).*TreeTagger.*")) {
					  lapClass += ".dkpro.TreeTagger" + language;
				  } 
			  }
		  }
	  } else {
			lapClass = ConfigFileUtils.getAttribute(configFile, "activatedLAP");
	  }
	  
	  if (lapClass == null || lapClass.matches("eu.excitementproject.eop.lap")) {
				// fall back to defaults
		  lapClass = "eu.excitementproject.eop.lap";
		  if (language.matches("IT")) {
			lapClass += ".textpro.TextProTaggerIT";
		  } else {
			lapClass += ".dkpro.OpenNLPTagger" + language;
  		  }
	  }
	  return lapClass;  
	}
	
	/**
	 * When the input consists of a file (as opposed to a test/hypothesis pair)
	 * it is processed through the LAP
	 * 
	 * @param inputFile -- input (RTE formatted) file
	 * @param outDir -- directory for writing the serialized CAS objects produced from the test/hypothesis pairs in the input data
	 */
	public void runLAPOnFile(String inputFile, String outDir) {
		
		logger.info("Running lap on file: " + inputFile + " // writing output to directory " + outDir);
		
		try {
			lap.processRawInputFormat(new File(inputFile), new File(outDir));
		} catch (LAPException e) {
			System.err.println("Error running the LAP");
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	/**
	 * Run the LAP on a given text/hypothesis pair
	 * 
	 * @param text -- text portion of the entailment pair
	 * @param hypothesis -- hypothesis portion of the entailment pair
	 * @return -- a CAS object for the given text/hypothesis pair
	 */
	public JCas runLAP(String text, String hypothesis) {
		JCas aJCas = null;
		try {
			aJCas = lap.generateSingleTHPairCAS(text, hypothesis);
			PlatformCASProber.probeCasAndPrintContent(aJCas, System.out);
		} catch (LAPException e) {
			logger.error("Error running the LAP");
			e.printStackTrace();
			System.exit(1);			
		}
		
		return aJCas;
	}
	
	/**
	 * Unused method for editing a given input configuration file
	 * 
	 * @param option
	 */
	private void editConfigFile(EOPRunnerCmdOptions option) {
		try {
			configFileName = baseConfigFile + ".edited.xml";
			configFile = new File(configFileName);
			config = new ImplCommonConfig(ConfigFileUtils.editConfigFile(baseConfigFile, configFile, option));
		} catch (Exception e) {
			logger.error("Could not generate a configuration file object");
			e.printStackTrace();
		}
	}
		
	/**
	 * Create the CommonConfig object based on the configuration file name provided as argument
	 * 
	 * @param option -- (parsed) command line arguments
	 */
	private void chooseConfigFile() {
		
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
	
	/**
	 * Find the value for a given parameter either from the command line arguments or from the configuration file
	 * 
	 * @param fileOption -- option value from the command line arguments
	 * @param string -- name of tag of the wanted value in the configuration file
	 * @return -- value of the wanted parameter
	 */
	private String getOptionValue(String fileOption, String string) {
		
		if (fileOption == null || fileOption.isEmpty()) {
			fileOption = ConfigFileUtils.getAttribute(configFile,string);
		}
		
		return fileOption;
	}
	
	/**
	 * Train the EDA
	 * 
	 * @param trainFile -- training data (RTE formatted)
	 * @param trainDir -- directory for storing the processed training data (e.g. CAS-es produced by the LAP)
	 */
	public void runEOPTrain(String trainFile, String trainDir) {
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

	
	/**
	 * Run the EDA on the test data
	 * 
	 * @param testFile -- test file (RTE formatted)
	 * @param testDirStr -- directory for storing testing decisions
	 * @param outDir -- directory for storing the results in the web-demo-friendly format
	 */
	public void runEOPTest(String testFile, String testDirStr, String outDir) {
		
		String resultsFile = outDir + "/" + configFile.getName() + "_results.txt";
		
		if (option.results != null) {
			xmlResultsFile = option.results;
		} else { 
			xmlResultsFile = outDir + "/" + configFile.getName() + "_results.xml";
		}
		
		Path source;
//		Path target = Paths.get(outDir + "/" + configFile.getName() + "_report.xml");
		
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
				hasGoldLabel = OutputUtils.getGoldLabel(cas);
			}
			writer.close();
			out.close();
			
			OutputUtils.generateXMLResults(testFile, resultsFile, xmlResultsFile);
			
			logger.info("Results file -- XML format: " + xmlResultsFile);
			logger.info("Results file -- txt format: " + resultsFile);			
			
		} catch (Exception e) {
			logger.error("Error copying run output files " + resultsFile + " to directory " + outDir);
			e.printStackTrace();
		}
		
	}

	
	public void scoreResults(String resultsFile) {
		scoreResults(resultsFile,Paths.get(resultsFile + "_report.xml"));
	}
	
	
	public void scoreResults(String resultsFile, Path target) {
		EDAScorer.score(new File(resultsFile), target.toString());
		logger.info("Results file: " + resultsFile);
		logger.info("Evaluation file: " + target.toString());
	}
	
	

	/**
	 * Run the platform on a single test/hypothesis pair
	 * 	 	
	 * @param option -- command line arguments
	 */
	public void runEOPSinglePair() {
		
		logger.info("Text: " + option.text);
		logger.info("Hypothesis: " + option.hypothesis);
		
		JCas aJCas = runLAP(option.text, option.hypothesis);
		try {
			TEDecision te = eda.process(aJCas);
			logger.info("T/H pair processing result: " + te.getDecision() + " with confidence " + te.getConfidence());
			OutputUtils.makeSinglePairXML(te, aJCas, option.output, option.language);
		} catch (EDAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
		
	
	/**
	 * When the command line arguments could not be parsed, show the help
	 * 
	 * @param parser
	 */
	private static void showHelp(CmdLineParser parser) {
		System.out.println("EOPRunner  [options ...] [arguments ...]");
		parser.printUsage(System.out);
	}

	
	/**
	 * Run the platform, according to the specified command-line arguments
	 */
	public void run() {
		
		try{	
			logger.info("Initializing EDA from file " + config.getConfigurationFileName());	

			eda = dih.startEngineBasic(configFile);
			logger.info("EDA object created from class " + eda.getClass());
			
			
			if (option.train) {
								
				String trainFile = getOptionValue(option.trainFile, "trainFile");
				String trainDir = getOptionValue(option.trainDir, "trainDir");
				runEOPTrain(trainFile, trainDir);
			}
			
			if (option.test) {
				
				eda.initialize(config);

				if (option.testFile != null) {
					String testFile = getOptionValue(option.testFile, "testFile");
					String testDir = getOptionValue(option.testDir, "testDir");
					if (option.output.isEmpty()) {
						runEOPTest(testFile, testDir, "./");
					} else {
						runEOPTest(testFile, testDir, option.output);
					}
				} else {
					runEOPSinglePair();
				}
			}
			
			if (option.score || hasGoldLabel != null) {
				scoreResults(xmlResultsFile);
			} 

			eda.shutdown();

		} catch(ConfigurationException | EDAException | ComponentException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		
		EOPRunner eopRunner = new EOPRunner(args);
		eopRunner.run();
	}
}
