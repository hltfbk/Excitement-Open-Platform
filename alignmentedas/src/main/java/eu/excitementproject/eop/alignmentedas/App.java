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
import eu.excitementproject.eop.alignmentedas.p1eda.SimpleWordCoverageP1EDA;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * A simple (EOP)-RTE XML data runner for P1EDA configurations 
 *
 */
public class App 
{
    @SuppressWarnings("unused")
	public static void main( String[] args )
    {
    	try 
    	{
    		// Set Log4J setting with DEBUG
    		BasicConfigurator.configure(); 
    		Logger.getRootLogger().setLevel(Level.DEBUG);  // set INFO to hide Debug info 

    		// Prepare LAP and EDA 
    		LAP_ImplBase lap = new TreeTaggerEN(); 
    		P1EDATemplate p1eda = new SimpleWordCoverageP1EDA(); 

    		// Train the instance, and save model. 
        	File rteInputXML = new File("../core/src/main/resources/data-set/English_dev.xml");  
    		File classifierModel = new File ("target/cModel.model"); 
    		// uncomment the following for re-training ... 
    	    //trainEDAInstanceWith(p1eda, lap, rteInputXML, classifierModel); 

    	    // Okay, we have it done. 
    		p1eda = new SimpleWordCoverageP1EDA(); 
    		p1eda.initialize(classifierModel); 
    		
    		// now you can call process() 
    		JCas aPair = lap.generateSingleTHPairCAS("Claude Chabrol divorced Agnes, his first wife, to marry the actress Stéphane Audran. His third wife is Aurore Paquiss.", "Aurore Paquiss married Chabrol."); 
    		TEDecisionWithAlignment r = p1eda.process(aPair); 
    		
    		// you can access alignment result with confidence .. and also ... the underlying JCas with Alignments. 
    		System.out.println("The decision was: " + r.getDecision()); 
    		System.out.println("Confidence: " + r.getConfidence()); 
    		
    		// evaluate it.
    		File evalXmiDir = new File("target/testingXmis/"); 
    		File testingXml = new File("../core/src/main/resources/data-set/English_test.xml");  
    		runLAPForXmis(lap, testingXml, evalXmiDir); 
    		List<Double> evalResult = p1eda.evaluateModelWithGoldXmis(evalXmiDir); 
    		
    		System.out.println("(accuracy, f1, prec, recall, true positive ratio, true negative ratio)"); 
    		System.out.println(evalResult.toString()); 
    		
    	}
    	catch(Exception e) 
    	{
    		System.err.println("Run stopped with an exception " + e.getMessage()); 
    	}
    	
    }
    
    
    
    @SuppressWarnings("unused")
	private static void trainEDAInstanceWith(P1EDATemplate p1eda, LAP_ImplBase lap, File rteInputXML, File classifierModel) throws LAPException, IOException, EDAException
    {
		
		// pre-process training data 
    	File xmiDir = new File("target/trainingXmis/");
		runLAPForXmis(lap, rteInputXML, xmiDir); 
		
		// train a model, and store ...  
		p1eda.startTraining(xmiDir, classifierModel); 
		p1eda.shutdown();     	
    }
    
    
    
    private static void runLAPForXmis(LAP_ImplBase lap, File rteInputXML, File xmiDir) throws LAPException, IOException
    {
    	
    	if (xmiDir.exists()) {
    		// delete all contents 
    		FileUtils.deleteDirectory(xmiDir); 
    	}
    	xmiDir.mkdirs(); 
    	
    	lap.processRawInputFormat(rteInputXML, xmiDir); 
    }
    
}
