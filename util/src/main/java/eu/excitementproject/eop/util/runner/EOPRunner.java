package eu.excitementproject.eop.util.runner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.kohsuke.args4j.CmdLineParser;

import eu.excitementproject.eop.alignmentedas.p1eda.visualization.P1EdaVisualizer;
import eu.excitementproject.eop.alignmentedas.p1eda.visualization.Visualizer;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.PlatformCASProber;
//import eu.excitementproject.eop.util.eval.EDAScorer;
//it enables multi-class problems evaluation
import eu.excitementproject.eop.util.eval.Scorer;

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

public class EOPRunner {

	// command line options
	private EOPRunnerCmdOptions option;
	private EOPRunnerInitializationHelper<?> dih;

	private LAPRunner lapRunner = null;
	
	private String configFileName;
	private File configFile = null;

	private String resultsFile = null;
	private String xmlResultsFile = null;

	private String outputDir = "./";
	
	@SuppressWarnings("unused")
	private String language = "EN";
	
	private CommonConfig config;
				
	private EDABasic<?> eda = null;
	
//	private String configSection = "PlatformConfiguration";

	private Logger logger;

	private Visualizer visualizer = null;
	
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
				
		if (args.length == 0)
			showHelp(parser);
		
		try{ 
			parser.parseArgument(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// make the output directory if given
		if (! option.output.isEmpty()) {
			outputDir = option.output;

			File dir = new File(outputDir);
			if (! dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}

	
	/**
	 * Unused method for editing a given input configuration file
	 * 
	 * @param option
	 */
/*	private void editConfigFile(EOPRunnerCmdOptions option) {
		try {
			configFileName = baseConfigFile + ".edited.xml";
			configFile = new File(configFileName);
			config = new ImplCommonConfig(ConfigFileUtils.editConfigFile(baseConfigFile, configFile, option));
		} catch (Exception e) {
			logger.error("Could not generate a configuration file object");
			e.printStackTrace();
		}
	}
*/

	public void setLanguage() {
	
		if (option.language != null) {
			language = option.language.toUpperCase();
		} else {
			if (configFile != null)
				language = ConfigFileUtils.getAttribute(configFile, "language");
		}
	}

	
	
	/**
	 * Create the CommonConfig object based on the configuration file name provided as argument
	 */
	public void initializeConfigFile() {
		
		configFileName = option.config;
		configFile = new File(configFileName);
		
		logger.info("Configuration file: " + configFileName);
		
		try {
			config = new ImplCommonConfig(configFile);
			initializeEDA();
		} catch (ConfigurationException e) {
			logger.error("Problem generating the CommonConfig object for the configuration file");
			e.printStackTrace();
		}	
	}
	
	/**
	 * Initialize P1EDA's visualizer to enable tracing
	 */
	public void initializeVisualizer() {		
		visualizer = new P1EdaVisualizer();
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
	 * Create 
	 */
	public void initializeEDA() {
		
		logger.info("Initializing EDA from file " + config.getConfigurationFileName());	

		try {
			eda = dih.startEngineBasic(configFile);
		} catch (ConfigurationException | EDAException | ComponentException e) {
			logger.error("Could not create EDA object");
			e.printStackTrace();
		} 
		logger.info("EDA object created from class " + eda.getClass());		
	}
	
	/**
	 * Train the EDA
	 */
	public void runEOPTrain() {
		
		try {
			eda.startTraining(config);
		} catch (ConfigurationException | EDAException | ComponentException e) {
			logger.error("Could not perform training");
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
	public void runEOPTest(String testDirStr, String outDir) {

		File outDirectory = new File(outDir);
		if (! outDirectory.exists())
			outDirectory.mkdirs();
		
		if (option.results != null) {
			resultsFile = option.results;
			xmlResultsFile = option.results.replaceAll(".txt$", ".xml");
		} else {
			resultsFile = outDir + "/" + configFile.getName() + "_results.txt";
			xmlResultsFile = outDir + "/" + configFile.getName() + "_results.xml";
		}

		try {
						
			File testDir = new File(testDirStr);
			OutputStream out = Files.newOutputStream(Paths.get(resultsFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, System.out);
				TEDecision teDecision = eda.process(cas);
				
				writer.write(OutputUtils.getPairID(cas) + "\t" + OutputUtils.getGoldLabel(cas) + "\t"  + teDecision.getDecision().toString() + "\t" + teDecision.getConfidence() + "\n");
//				hasGoldLabel = OutputUtils.getGoldLabel(cas);
				
				if (visualizer != null) {
					OutputUtils.makeTraceHTML(teDecision, cas, outputDir, visualizer);
				}
			}
			writer.close();
			out.close();

			// generate the XML results file
			logger.info("Results file -- XML format: " + xmlResultsFile);
			OutputUtils.generateXMLResults(option.testFile, resultsFile, xmlResultsFile);
			
			logger.info("Results file -- txt format: " + resultsFile);			
			
			File outputDir = new File(outDir);
			
			logger.info("Copying configuration file in output directory " + outDir);
			try {
				FileUtils.copyFileToDirectory(configFile, outputDir);
			} catch (IOException e) {
				logger.info("Problem copying the configuration file " + configFile.getName() + " to directory " + outputDir.getName());
			}
			
			
			// careful with the copying! The model file may have a relative path which must be first resolved!

			logger.info("Copying model in output directory " + outDir);
			String modelFile = ConfigFileUtils.getAttribute(configFile, "modelFile");
			if (modelFile != null && !modelFile.isEmpty()) {
				FileUtils.copyFileToDirectory(new File(modelFile), outputDir);
			} else {
//				logger.info("No model file found");
			}
			
		} catch (Exception e) {
			logger.error("Error testing the EOP");
			e.printStackTrace();
		}
	}

	

	/**
	 * Run the platform on a single test/hypothesis pair
	 * 	 	
	 * @param option -- command line arguments
	 */
	public void runEOPSinglePair() {
		
		logger.info("Text: " + option.text);
		logger.info("Hypothesis: " + option.hypothesis);
		
		JCas aJCas = lapRunner.runLAP(option.text, option.hypothesis);

		if (option.results != null) {
			resultsFile = option.results;
			xmlResultsFile = option.results.replaceAll(".txt$", ".xml");
		} else {
			resultsFile = outputDir + "/" + configFile.getName() + "_results.txt";
			xmlResultsFile = outputDir + "/" + configFile.getName() + "_results.xml";
		}		
		
		try {
			TEDecision teDecision = eda.process(aJCas);
			logger.info("T/H pair processing result: " + teDecision.getDecision() + " with confidence " + teDecision.getConfidence());
			OutputUtils.makeSinglePairXML(teDecision, aJCas, xmlResultsFile, option.language);
			
			if (visualizer != null) {
				OutputUtils.makeTraceHTML(teDecision, aJCas, outputDir, visualizer);
			}
		} catch (EDAException e) {
			System.err.println("Problem running the EDA");
			e.printStackTrace();
		} catch (ComponentException e) {
			System.err.println("Problem running a component of the EDA");
			e.printStackTrace();
		} 
	}

	
	/**
	 * Score the results relative to the given gold standard
	 */
	public void scoreResults() {

		String availableResultsFile = resultsFile;
		if (option.results != null) {
			availableResultsFile = option.results;
		}
		
		if (availableResultsFile != null) {

			if (xmlResultsFile == null) {
				xmlResultsFile = availableResultsFile.replaceAll(".txt$", ".xml");
			}

			scoreResults(availableResultsFile,Paths.get(availableResultsFile + "_report.xml"));			

			if (option.testFile != null) {
				logger.info("Results file -- XML format: " + xmlResultsFile);
				OutputUtils.generateXMLResults(option.testFile, availableResultsFile, xmlResultsFile);
			} else {
				logger.error("Could not score the results -- the testFile option is missing");
			} 
		} else {
				logger.error("Could not score the results -- check that you have provided the correct test file, and that the results file (" + availableResultsFile + ") was properly generated");
		}
		
	}
	
	
	public void scoreResults(String resultsFile, Path target) {
		//EDAScorer.score(new File(resultsFile), target.toString());
		//it enables multi-class problems evaluation
		Scorer.score(new File(resultsFile), target.toString());
		logger.info("Results file: " + resultsFile);
		logger.info("Evaluation file: " + target.toString());
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
		
		String testFile, testDir = "";
		
		try{	
			
			logger.info("running the EOP");
			
			if (option.config != null) 
				initializeConfigFile();
			
			setLanguage();

			
			if (option.trace) 
				initializeVisualizer();
			
			if (option.lap != null) 
				lapRunner = new LAPRunner(option.lap);

//			if ((lapRunner == null) && (option.test || option.train))
			if (lapRunner == null && option.config != null)
				lapRunner = new LAPRunner(configFile);
			
			if (option.trainFile != null) {
								
				String trainFile = getOptionValue(option.trainFile, "trainFile");
								
				if (! option.nolap) {
					String trainDir = getOptionValue(option.trainDir, "trainDir");
					logger.info("\t training file: " + trainFile + "\n\t training dir: " + trainDir);

					lapRunner.runLAPOnFile(trainFile, trainDir);
				}
			}
			
			if (option.train) 				
				runEOPTrain();
						
			if (option.testFile != null) {
				testFile = getOptionValue(option.testFile, "testFile");
				
				if ((! option.nolap) && (option.config != null)) {
					testDir = getOptionValue(option.testDir, "testDir");
					logger.info("\t testing file: " + testFile + "\n\t testing dir: " + testDir);

					lapRunner.runLAPOnFile(testFile, testDir);
				} else {
					logger.info("Skipping LAP processing (if you think it shouldn't skip, check that the config option was used and the configuration file was given, and that the option \"-nolap\" was not used. \n");
				}
			}
			
			if (option.test) {
				
//				if (! option.train)
				    eda.initialize(config);
				
				testDir = getOptionValue(option.testDir, "testDir");
				
				if (! option.text.isEmpty()) {
					runEOPSinglePair();					
				} else {
					runEOPTest(testDir,outputDir);
				}
			}
			
			if (option.score)
				scoreResults();

			if (eda!= null)
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
