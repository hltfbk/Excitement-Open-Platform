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
	private static String configFile; 
	
	private static CommonConfig config;
	
	private static LAPAccess lap = null;
			
	private static EDABasic<?> eda;
	
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
//				lap = new OpenNLPTaggerEN();
			} else if (language.matches("DE")) {
				lap = new OpenNLPTaggerDE();
			}		
		} catch (LAPException e) {
			System.err.println("Error initializing LAP");
			e.printStackTrace();
			System.exit(1);
		}
	}

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
			BufferedReader reader = Files.newBufferedReader(Paths.get(testFile), StandardCharsets.UTF_8);
			//InputStream in = Files.newInputStream(Paths.get(testFile));
			//BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			OutputStream out = Files.newOutputStream(Paths.get(xmlFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
			String line = null, id;
			String[] entDec;
			Pattern p = Pattern.compile("^(.*pair id=\"(\\d+)\") .* (task.*)$");
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
			//in.close();
		} catch (IOException e) {
			System.out.println("Problems reading test file " + testFile);
			e.printStackTrace();
		}
		
	}
	
	
	private static void makeSinglePairXML(TEDecision decision, JCas aJCas, String outDir, String lang) {
		
		String xmlResultsFile = outDir + "/results.xml";
		try {
			
			OutputStream out = Files.newOutputStream(Paths.get(xmlResultsFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
			writer.write("<entailment-corpus lang=\"" + lang + "\">\n");
			writer.write("  <pair id=\"1\" entailment=\"" + decision.getDecision().name() + "\" benchmark=\"N/A\" score=\"" + decision.getConfidence() + "\" confidence=\"1\" task=\"EOP test\">\n");
			writer.write("    <t>" + aJCas.getView("TextView").getDocumentText() + "</t>\n");
			writer.write("    <h>" + aJCas.getView("HypothesisView").getDocumentText() + "</h>\n");
			writer.write("  </pair>\n");
			writer.write("</entailment-corpus>\n");
			writer.close();
			out.close();

			
			System.out.println("Results file: " + xmlResultsFile);
			
		} catch (IOException | CASException e) {
			System.out.println("Coudl not write to output file " + xmlResultsFile);
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
		Path target = Paths.get(outDir + "/report.xml");
				
		try {
//			source = Paths.get(resultsFile);
//			Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
			generateXMLResults(testFile, resultsFile, xmlResultsFile);
			
			source = Paths.get(evalFile);
			Files.copy(source, target, REPLACE_EXISTING);

			System.out.println("Results file: " + xmlResultsFile);
			System.out.println("Evaluation file: " + target.getFileName());
			
		} catch (IOException e) {
			System.out.println("Error copying run output files " + resultsFile + " and " + evalFile + " to directory " + outDir);
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
			makeSinglePairXML(te, aJCas, option.output, option.language);
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

			if (! option.dir.isEmpty()) {
				configFileDir = option.dir;
			}
			
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
				runEOPSinglePair(option);
			}
			eda.shutdown();

		} catch(CmdLineException | ConfigurationException | EDAException | ComponentException e) {
			e.printStackTrace();
		}
	}

}
