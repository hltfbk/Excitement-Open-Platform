package eu.excitementproject.eop.core.component.distance;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.ExampleLAP;

public class BagOfWordsSimilarityTest {
	@Test
	public void test() {
		
		BagOfWordsSimilarity bs = new BagOfWordsSimilarity();
		
		JCas cas = null;
		ExampleLAP lap = null;

        try 
        {
        	lap = new ExampleLAP(); 
        	cas = lap.generateSingleTHPairCAS("I'm a student .", "I am a person .", "ENTAILMENT"); 
//        	cas = lap.generateSingleTHPairCAS("a a", "a a b a a", "ENTAILMENT"); 
//        	cas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.", "The person must have a PhD.", "ENTAILMENT"); 
        }
        catch(LAPException e)
        {
        	System.err.println(e.getMessage()); 
        }
        try {
    		DistanceValue bowsim = bs.calculation(cas);
    		System.out.println(bowsim.getDistance());
    		System.out.println(bowsim.getUnnormalizedValue());
    		System.out.println(bowsim.getDistanceVector());
        } catch(Exception e) {
        	System.err.println(e.getMessage());
        }
        
    }
}
