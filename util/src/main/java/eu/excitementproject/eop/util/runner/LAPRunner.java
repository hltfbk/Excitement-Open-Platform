package eu.excitementproject.eop.util.runner;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class LAPRunner {

	private LAPAccess lap = null;
	
	private String language;
//	private String lapAnnotDir = "/tmp/";
	
	private Logger logger;
	
	
	/**
	 * LAPRunner constructor -- initializes a lap from the given name and language
	 * 
	 * @param lapName -- name of the LAP (e.g. treetagger, opennlp, ... -- given as full class names or partial)
	 * @param language -- the language parameter for the LAP
	 */
	public LAPRunner(String lapName, String language) {

		logger = Logger.getLogger("eu.excitementproject.eop.util.runner.LAPRunner");
		
		this.language = language;
		initializeLAP(getLAPClass(lapName));
	}

	/**
	 * LAPRunner constructor -- initializes a lap from information in an EDA configuration file
	 * 
	 * @param configFile -- EDA configuration file
	 */
	public LAPRunner(File configFile) {
		
		logger = Logger.getLogger("eu.excitementproject.eop.util.runner.LAPRunner");
		
		language = ConfigFileUtils.getAttribute(configFile, "language");
		initializeLAP(getLAPClass(configFile));	
	}
	

	/**
	 * LAPRunner constructor -- initalizes a default LAP for the given language (OpenNLP)
	 * 
	 * @param language
	 */
	public LAPRunner(String language) {

		logger = Logger.getLogger("eu.excitementproject.eop.util.runner.LAPRunner");
		
		this.language = language;
		initializeLAP(getDefaultLAPClass());
	}	
	
	
	/**
	 * Initializes the LAP based on the lap parameter (if given) and the language information in the configuration file
	 * 
	 * @param option -- (parsed) command arguments
	 */
	public void initializeLAP(String lapClassName) {
				
		logger.info("LAP initialized from class " + lapClassName);
		
		try {
				Class<?> lapClass = Class.forName(lapClassName);
				Constructor<?> lapClassConstructor = lapClass.getConstructor();
				lap = (LAPAccess) lapClassConstructor.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("Error initializing LAP : " + e.getClass());
			e.printStackTrace();
		} 
	}
	


	
	/**
	 * Determine the class of the chosen LAP tool
	 * 
	 * @param option -- command-line arguments
	 * @param language -- language option
	 * @return -- the name of the class for the chosen LAP
	 */
	private String getLAPClass(String lapName) {
				
	  String classStem = "eu.excitementproject.eop.lap";
	  String lapClass = null;
	  
	  if (lapName.matches("eu.excitementproject.*")) {
		  lapClass = lapName;
	  } else {
		  if (lapName.matches("(?i).*textpro.*")) {
			  // there is TextPro only for Italian
			  lapClass = classStem + ".textpro.TextProTaggerIT";
		  } else {
			  if (lapName.matches("(?i).*opennlp.*")) {
				  lapClass = classStem + ".dkpro.OpenNLPTagger" + language;
			  } else {
				  if (lapName.matches("(?i).*treetagger.*")) {
					  lapClass = classStem + ".dkpro.TreeTagger" + language;
				  } 
			  }
		  }
	  }
	  
	  return lapClass;	
	}

	
	/**
	 * Get the lap class from the configuration file
	 * 
	 * @param configFile
	 * @return -- the activated LAP specified in the configuration file
	 */
	private String getLAPClass(File configFile) {
		return ConfigFileUtils.getAttribute(configFile, "activatedLAP");
	}

	
	/** 
	 * The class for the default LAP (OpenNLP)
	 * @return
	 */
	private String getDefaultLAPClass() {
		
		return "eu.excitementproject.eop.lap.dkpro.OpenNLPTagger" + language;
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
		}
		
		return aJCas;
	}	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
