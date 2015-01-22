package eu.excitementproject.eop.util.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

@SuppressWarnings("unused")
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
		
		Pattern biuteeP = Pattern.compile(".*biutee.*",Pattern.CASE_INSENSITIVE);
		Matcher biuteeM = biuteeP.matcher(configFile.getName());
		
		if (biuteeM.matches()) {
			initializeLAP(getLAPClass(configFile), configFile);
		} else {
			initializeLAP(getLAPClass(configFile));
		}
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
		
		logger.info("\t LAP initialized: " + lap.getClass());
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
	 * Initializes the LAP based on the lap parameter (if given) and the language information in the configuration file
	 * 
	 * @param option -- (parsed) command arguments
	 */
	public void initializeLAP(String lapClassName, File configFile) {
				
		logger.info("LAP initialized from class " + lapClassName);
		try {

//			CommonConfig config = new ImplCommonConfig(configFile);
		
			Class<?> lapClass = Class.forName(lapClassName);
			Constructor<?> lapClassConstructor = lapClass.getConstructor(CommonConfig.class);
			lap = (LAPAccess) lapClassConstructor.newInstance(new ImplCommonConfig(configFile));
//			lap = (LAPAccess) lapClassConstructor.newInstance(config);
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ConfigurationException e) {
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

			File dir = new File(outDir);
			if (! dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			} else {
				FileUtils.cleanDirectory(dir);
			}
		
			lap.processRawInputFormat(new File(inputFile), dir);
		} catch (LAPException e) {
			System.err.println("Error running the LAP");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not clean up LAP output directory " + outDir);
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
			
//			serializeCAS(aJCas);
			
		} catch (LAPException e) {
			logger.error("Error running the LAP");
			e.printStackTrace();
		}
		
		return aJCas;
	}	

/*	
	// code copied from LAP_ImplBase.java, just for testing the LAP when processing one pair from the command line
	private void serializeCAS(JCas aJCas) throws LAPException {
		// serialize 
		String xmiName = "1.from_commandLine.xmi"; 
		File xmiOutFile = new File("/tmp/", xmiName); 
	
		try {
			FileOutputStream out = new FileOutputStream(xmiOutFile);
			XmiCasSerializer ser = new XmiCasSerializer(aJCas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(aJCas.getCas(), xmlSer.getContentHandler());
			out.close();
		} catch (FileNotFoundException e) {
			throw new LAPException("Unable to create/open the file" + xmiOutFile.toString(), e);
		} catch (SAXException e) {
			throw new LAPException("Failed to serialize the CAS into XML", e); 
		} catch (IOException e) {
			throw new LAPException("Unable to access/close the file" + xmiOutFile.toString(), e);
		}

		logger.info("Pair written as " + xmiOutFile.toString() );
	}
*/	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
