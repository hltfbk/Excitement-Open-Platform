package eu.excitement.type.alignment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.alignmentedas.P1EdaRTERunner;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Test code for, how much memory does it require to load 800 CASes in memory, 
 * for parameter optimization... 
 * 
 * Okay. 800 CASes can be handled in 8G memory system... 
 * (with just LAP annotations... ) 
 * 
 * For now, we have no fast "binary" serialization methods available (e.g. requires later DKPro) 
 * So, let's proceed with this memory version for "parameter (of evaluateAlignment / Feature extractor)" 
 * optimizers. 
 * 
 * @author Tae-Gil Noh
 *
 */
@SuppressWarnings("unused")
public class InMemoryCasTest {

//	public InMemoryCasTest() {
//	}

	
	public static void main(String[] args) {		
		
		// logger 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO); 

		try {

			// generate Xmis
			//LAP_ImplBase lap = new TreeTaggerEN(); 
			LAP_ImplBase lap = new MaltParserEN(); 
        	File rteInputXML = new File("../core/src/main/resources/data-set/English_dev.xml");  
    		File xmiDir = new File ("target/xmiTest/"); 
			P1EdaRTERunner.runLAPForXmis(lap, rteInputXML, xmiDir); 
			
			logger.info("***"); 			logger.info("***"); 			logger.info("***"); 
			
			// load Xmis 
			List<JCas> jCasList = loadXmisAsJCasList(xmiDir); 
			System.out.println("JCas list loaded : " + jCasList.size() + " instances."); 
			
		}
		catch (Exception e)
		{
			logger.error(e.getMessage()); 
			System.exit(1); 
		}
	}

	
	public static List<JCas> loadXmisAsJCasList(File xmiDir)
	{		
		List<JCas> casList = new ArrayList<JCas>();
		
		// walk each XMI files in the Directory ... 
		File[] files =  xmiDir.listFiles(); 
		if (files == null)
		{
			logger.warn("Path " + xmiDir.getAbsolutePath() + " does not hold XMI files"); 
			System.exit(1); 
		}
		
		for (File f : files)
		{
			// is it a XMI file?
			// 

			logger.info("Working with file " + f.getName()); 
			if(!f.isFile()) 
			{	// no ... 
				logger.warn(f.toString() + " is not a file... ignore this"); 
				continue; 
			}
			if(!f.getName().toLowerCase().endsWith("xmi")) // let's trust name, if it does not end with XMI, pass
			{
				logger.warn(f.toString() + " is not a XMI file... ignoring this"); 
				continue; 
			}
			
			// So, we have an XMI file. Load in to CAS 
			JCas aTrainingPair = null; 
			try {
				 aTrainingPair = PlatformCASProber.probeXmi(f, null);
			}
			catch (LAPException le)
			{
				logger.warn("File " + f.toString() + " looks like XMI file, but its contents are *not* proper EOP EDA JCas"); 
				System.exit(1); 
			}
			casList.add(aTrainingPair); 
		}

		logger.info("Loaded " + casList.size() + " XMI files as JCas..."); 
		return casList; 
	}
	
	public static Logger logger = Logger.getLogger(InMemoryCasTest.class); 
 

	
}
