package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.ExampleLAP;

public class ClassificationEDATest {

	@Test
	public void test() {
		
		ClassificationEDA ceda = new ClassificationEDA();

		CommonConfig config = null;
		
		try {
			ceda.initialize(config);
			ceda.startTraining(config);
			System.out.println("training done");
			
	        ExampleLAP lap = null; 
	        try 
	        {
	        	lap = new ExampleLAP(); 
	        }
	        catch (LAPException e)
	        {
	        	System.err.println(e.getMessage()); 
	        }
			JCas aCas = lap.generateSingleTHPairCAS("The train was uncomfortable", "The train was comfortable", "NONENTAILMENT"); 
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
