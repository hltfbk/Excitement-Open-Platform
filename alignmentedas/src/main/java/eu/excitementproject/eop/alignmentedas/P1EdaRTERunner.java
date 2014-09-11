package eu.excitementproject.eop.alignmentedas;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.instances.SimpleWordCoverageDE;
import eu.excitementproject.eop.alignmentedas.p1eda.instances.SimpleWordCoverageEN;
import eu.excitementproject.eop.alignmentedas.p1eda.instances.SimpleWordCoverageIT;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WNVOMT;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithVO;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithoutVO;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerIT;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * A simple (EOP)-RTE XML data runner for P1EDA configurations 
 *
 */
@SuppressWarnings("unused")
public class P1EdaRTERunner 
{
	public static void main( String[] args )
    {
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // set INFO to hide Debug info. 
		
    	try 
    	{	
    		// Prepare LAP and EDA (here, both for English) and eval on RTE3 (again, EN)  
    		LAP_ImplBase lapEN = new TreeTaggerEN(); 
    		P1EDATemplate p1edaEN = new SimpleWordCoverageEN(); // Put your (configured, instance) P1EDA here... 
    		evaluateOnRTE3EN(lapEN, p1edaEN, false);  // set final argument true, if lap has not been changed from last call. (to reuse saved XMI files) 
    		
    		// use evaluateOnRTE3DE for German 
//    		LAP_ImplBase lapDE = new TreeTaggerDE(); 
//    		P1EDATemplate p1edaDE = new SimpleWordCoverageDE(); 
//    		evaluateOnRTE3DE(lapDE, p1edaDE, true); 
    		
    		// use evaluateOnRTE3IT for Italian 
//    		LAP_ImplBase lapIT = new TreeTaggerIT(); 
//    		P1EDATemplate p1edaIT = new SimpleWordCoverageIT(); 
//    		evaluateOnRTE3IT(lapIT, p1edaIT, false); 
    		
    	}
    	catch(Exception e) 
    	{
    		System.err.println("Run stopped with an exception: " + e.getMessage()); 
    	}
    	
    }	

	public static void evaluateOnRTE3EN(LAP_ImplBase lap, P1EDATemplate p1eda, boolean isXmiAlreadyPreprocessed) throws LAPException, EDAException, IOException 
	{
    	File rteTrainingXML = new File("../core/src/main/resources/data-set/English_dev.xml");  
		File rteTestingXML = new File("../core/src/main/resources/data-set/English_test.xml");
		
		evaluateOnRTEData(lap, p1eda, rteTrainingXML, rteTestingXML, isXmiAlreadyPreprocessed); 		
	}

	public static void evaluateOnRTE3DE(LAP_ImplBase lap, P1EDATemplate p1eda, boolean isXmiAlreadyPreprocessed) throws LAPException, EDAException, IOException 
	{
    	File rteTrainingXML = new File("../core/src/main/resources/data-set/German_dev.xml");  
		File rteTestingXML = new File("../core/src/main/resources/data-set/German_test.xml");
		
		evaluateOnRTEData(lap, p1eda, rteTrainingXML, rteTestingXML, isXmiAlreadyPreprocessed); 				
	}

	public static void evaluateOnRTE3IT(LAP_ImplBase lap, P1EDATemplate p1eda, boolean isXmiAlreadyPreprocessed) throws LAPException, EDAException, IOException 
	{
    	File rteTrainingXML = new File("../core/src/main/resources/data-set/Italian_dev.xml");  
		File rteTestingXML = new File("../core/src/main/resources/data-set/Italian_test.xml");
		
		evaluateOnRTEData(lap, p1eda, rteTrainingXML, rteTestingXML, isXmiAlreadyPreprocessed); 						
	}

	public static void evaluateOnRTEData(LAP_ImplBase lap, P1EDATemplate p1eda, File trainXML, File testXML, boolean xmiAlreadyPreprocessed) throws LAPException, EDAException, IOException 
	{				
    	File trainXmiDir = new File("target/trainingXmis/");    		
		File evalXmiDir = new File("target/testingXmis/"); 
		
		if (!xmiAlreadyPreprocessed)
		{
			runLAPForXmis(lap, trainXML, trainXmiDir); 
			runLAPForXmis(lap, testXML, evalXmiDir);
		}
		
		// Train the instance, and save model. 
		//File classifierModel = new File ("target/cModel.model"); 
		File classifierModel = new File (CLASSIFIER_MODEL_NAME); 
		p1eda.startTraining(trainXmiDir, classifierModel); 

		// evaluate with test(eval) data 
		List<Double> evalResult = p1eda.evaluateModelWithGoldXmis(evalXmiDir); 
		
		System.out.println("(accuracy, f1, prec, recall, true positive ratio, true negative ratio)"); 
		System.out.println(evalResult.toString()); 		
	}
		
    public static void runLAPForXmis(LAP_ImplBase lap, File rteInputXML, File xmiDir) throws LAPException, IOException
    {
    	
    	if (xmiDir.exists()) {
    		// delete all contents 
    		FileUtils.deleteDirectory(xmiDir); 
    	}
    	xmiDir.mkdirs(); 
    	
    	lap.processRawInputFormat(rteInputXML, xmiDir); 
    }
    
    static int unused; 
    static final String CLASSIFIER_MODEL_NAME = "target/temp.cmodel"; 
}
