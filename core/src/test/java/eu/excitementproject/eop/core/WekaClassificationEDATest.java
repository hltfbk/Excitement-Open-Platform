package eu.excitementproject.eop.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.OpenNLPTaggerDE;
import eu.excitementproject.eop.lap.lappoc.OpenNLPTaggerEN;

public class WekaClassificationEDATest {
	static Logger logger = Logger.getLogger(WekaClassificationEDATest.class.getName());

	@Test
	public void test() {
		
		boolean isEN = true;
		
		File inputFile = new File("./src/test/resources/small.xml"); 
		
		File outputDir = new File("./target/EN/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		OpenNLPTaggerEN lap = null;
		
        try 
        {
        	// LAP
        	if (isEN) {
            	lap = new OpenNLPTaggerEN();
        	} else {
        		lap = new OpenNLPTaggerDE();
        	}
			lap.processRawInputFormat(inputFile, outputDir);
        }
        catch(LAPException e)
        {
        	logger.info(e.getMessage()); 
        }
		
		WekaClassificationEDA wceda = new WekaClassificationEDA();
		
		CommonConfig config = null;
		
		try {
			// training
			wceda.initialize(config);
			wceda.startTraining(config);
			File modelFile = new File(wceda.getModelFile());
			assertTrue(modelFile.exists());
			logger.info("training done");
			
			// testing
	        try 
	        {
	        	if (isEN) {
	            	lap = new OpenNLPTaggerEN();
	        	} else {
	        		lap = new OpenNLPTaggerDE();
	        	}
	        }
	        catch (LAPException e)
	        {
	        	System.err.println(e.getMessage()); 
	        }
	        logger.info("build CASes for input sentence pairs:");
			JCas aCas = lap.generateSingleTHPairCAS("The train was uncomfortable", "the train was comfortable", "NONENTAILMENT"); 
			JCas bCas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			
			logger.info("Answers are:");
			ClassificationTEDecision decision1 = wceda.process(aCas);
			System.out.println(decision1.getDecision().toString());
			ClassificationTEDecision decision2 = wceda.process(bCas);
			System.out.println(decision2.getDecision().toString());
		
		} catch(Exception e) {
			logger.info(e.getMessage());
		}
		
	}
}
