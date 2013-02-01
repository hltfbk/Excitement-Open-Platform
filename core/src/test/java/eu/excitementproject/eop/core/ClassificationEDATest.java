package eu.excitementproject.eop.core;

import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;

public class ClassificationEDATest {
	
	static Logger logger = Logger.getLogger(ClassificationEDA.class.getName());

	@Test
	public void test() {
		
		ClassificationEDA ceda = new ClassificationEDA();
		
		CommonConfig config = null;
		
		try {
			// training
			ceda.initialize(config);
//			ceda.startTraining(config);
			File modelFile = new File(ceda.getModelFile());
			logger.info("training done");
			Assume.assumeTrue(modelFile.exists());
			
			// testing
			OpenNLPTaggerEN lap = null; 
	        try 
	        {
	        	lap = new OpenNLPTaggerEN();
	        }
	        catch (LAPException e)
	        {
	        	System.err.println(e.getMessage()); 
	        }
	        logger.info("build CASes for input sentence pairs:");
			JCas aCas = lap.generateSingleTHPairCAS("The train was uncomfortable", "the train was comfortable", "NONENTAILMENT"); 
			JCas bCas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			
			logger.info("Answers are:");
			ClassificationTEDecision decision1 = ceda.process(aCas);
			System.out.println(decision1.getDecision().toString());
			ClassificationTEDecision decision2 = ceda.process(bCas);
			System.out.println(decision2.getDecision().toString());
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
    }

}
