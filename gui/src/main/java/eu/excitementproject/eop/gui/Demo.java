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

import org.apache.uima.jcas.JCas;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.textpro.TextProTaggerIT;
import static java.nio.file.StandardCopyOption.*;

public class Demo {

	private static String lapAnnotDir = "./target/";
	
	private static String baseConfigFile = "configuration_example.xml";
	private static String configFileDir = "../core/src/test/resources";
	private static String configFile; 
	
	private static CommonConfig config;
	
	private static LAPAccess lap = null;
			
	private static EDABasic<?> eda;
	
	@SuppressWarnings("unused")
	private static String configSection = "PlatformConfiguration";
	
	/**
	 * @param args
	 */


	private static void initializeLAP(String language) {
		language = language.toUpperCase();
		try {
			if (language.matches("IT")) {
				lap = new TextProTaggerIT();
			} else if (language.matches("EN")) {
				lap = new TreeTaggerEN();
			} else if (language.matches("DE")) {
				lap = new OpenNLPTaggerDE();
			}		
		} catch (LAPException e) {
			System.err.println("Error initializing LAP");
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	@SuppressWarnings("unused")
	private static void runLAP(String inputFile) {
		try {
			lap.processRawInputFormat(new File(inputFile), new File(lapAnnotDir));
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
	
	
	@SuppressWarnings("unused")
	private static void editConfigFile(DemoCmdOptions option) {
		try {
			configFile = baseConfigFile + ".edited.xml";
			config = new ImplCommonConfig(ConfigFileUtils.editConfigFile(baseConfigFile, new File(configFile), option));
		} catch (Exception e) {
			System.err.println("Could not generate a configuration file object");
			e.printStackTrace();
		}
	}
		
	private static void chooseConfigFile(DemoCmdOptions option) {
		
		
		configFile = configFileDir + "/" + option.activatedEDA;
		if (! option.distance.isEmpty()) 
			configFile +=  "_" + option.distance;
		if (! option.resource.isEmpty()) {
			configFile += "_" + option.resource;
		} else {
			configFile += "_NoLexRes";
		}	
		configFile += "_" + option.language + ".xml";
			
		try {
			config = new ImplCommonConfig(new File(configFile));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private static HashMap<String,String> readResults(String file) {
		HashMap<String,String> results = new HashMap<String,String>();
		try {
			InputStream in = Files.newInputStream(Paths.get(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			Pattern p = Pattern.compile("^(.*?)\\t(.*)$");
			Matcher m;
			
			while ((line = reader.readLine()) != null) {
				m = p.matcher(line);
				if (m.matches()) {
					results.put(m.group(1), m.group(2));
					System.out.println("Added result: " + m.group(1) + " / " + m.group(2));
				}
			}
			reader.close();
			in.close();
		} catch (IOException e) {
			System.out.println("Problems reading results file " + file);
			e.printStackTrace();
		}
		return results;
	}
	
	private static void generateXMLResults(String testFile, String resultsFile, String xmlFile) {
		
		HashMap<String,String> results = readResults(resultsFile);
		try {
			InputStream in = Files.newInputStream(Paths.get(testFile));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			OutputStream out = Files.newOutputStream(Paths.get(xmlFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
			String line = null, id;
			String[] entDec;
			Pattern p = Pattern.compile("^(.*pair id=\"(\\d)\") .* (task.*)$");
			Matcher m;
			
			while ((line = reader.readLine()) != null) {
				m = p.matcher(line);
				if (m.matches()) {
					id = m.group(2);
					if (results.containsKey(id)) {
						entDec = results.get(id).split("\\t");
						line = m.group(1) + " " + "entailment=\"" + entDec[1] + "\" benchmark=\"" + entDec[0] + "\" score=\"" + entDec[2] + "\" confidence=\"1\" " + m.group(3);
					}
				}
				writer.write(line + "\n");
			}
			writer.close();
			out.close();
			reader.close();
			in.close();
		} catch (IOException e) {
			System.out.println("Problems reading test file " + testFile);
			e.printStackTrace();
		}
		
	}
	
	
/*	private static void runEOP(String test) {
		try {
			eda.startTraining(config);
			runLAP(test);
			// go through all the annotated objects (.xmi files) and process the single pair			
			
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
*/
	// copy the generated files to the given directory
	private static void runEOP(String testFile, String outDir) {
		String resultsFile = configFile + "_Result.txt";
		String xmlResultsFile = outDir + "/results.xml";
		String evalFile = resultsFile + "_Eval.xml";
		Path source;
		Path target = Paths.get(outDir);
				
		try {
//			source = Paths.get(resultsFile);
//			Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
			generateXMLResults(testFile, resultsFile, xmlResultsFile);
			
			source = Paths.get(evalFile);
			Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);

		} catch (IOException e) {
			System.out.println("Error copying run output files " + resultsFile + " and " + evalFile + " to directory " + outDir);
			e.printStackTrace();
		}
		
	}
	
	
	private static void runEOPSinglePair(String text, String hypoth) {
		System.out.println("Text: " + text);
		System.out.println("Hypothesis: " + hypoth);
		
		JCas aJCas = runLAP(text, hypoth);
		try {
			TEDecision te = eda.process(aJCas);
			System.out.println("T/H pair processing result: " + te.getDecision() + " with confidence " + te.getConfidence());
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
			
			initializeLAP(option.language);
//			editConfigFile(option);
			chooseConfigFile(option);
			
			System.out.println("Initializing EDA from file " + config.getConfigurationFileName());	

			eda = dih.startEngineBasic(new File(configFile));
			System.out.println("EDA object created from class " + eda.getClass());
			
			eda.initialize(config);
			if (option.test != null) {
				runEOP(option.test, option.output);
			} else {
				runEOPSinglePair(option.text, option.hypothesis);
			}
			eda.shutdown();

		} catch(CmdLineException | ConfigurationException | EDAException | ComponentException e) {
			e.printStackTrace();
		}
	}

}
