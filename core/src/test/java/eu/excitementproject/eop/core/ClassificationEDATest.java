package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.OpenNLPTaggerEN;

public class ClassificationEDATest {

	@Test
	public void test() {
		
		ClassificationEDA ceda = new ClassificationEDA();

		CommonConfig config = null;
		
		try {
			// training
			ceda.initialize(config);
			ceda.startTraining(config);
			File modelFile = new File(ceda.getModelFile());
			assertTrue(modelFile.exists());
			System.out.println("training done");
			
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
			JCas aCas = lap.generateSingleTHPairCAS("The train was uncomfortable", "the train was comfortable", "NONENTAILMENT"); 
			JCas bCas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			
			ClassificationTEDecision decision1 = ceda.process(aCas);
			System.out.println(decision1.getDecision().toString());
			ClassificationTEDecision decision2 = ceda.process(bCas);
			System.out.println(decision2.getDecision().toString());

		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
    }

}
