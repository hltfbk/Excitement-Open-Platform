package eu.excitementproject.eop.core.component.distance;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import static org.junit.Assert.*;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.ExampleLAP;


public class FixedWeightTokenEditDistanceTest {

	@Test
	public void test() {
    	
        FixedWeightLemmaEditDistance fixedEd
            = new FixedWeightLemmaEditDistance();
        
        JCas mycas = null; 
        ExampleLAP lap = null; 
        try 
        {
        	lap = new ExampleLAP(); 
            mycas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.", "The person must have a PhD.", "ENTAILMENT"); 
        }
        catch(LAPException e)
        {
        	System.err.println(e.getMessage()); 
        }
        try {
        	
        	//System.out.println(fixedEd.calculation(mycas).getDistance());
        	assertTrue(fixedEd.calculation(mycas).getDistance() > -1.0);
        	
        } catch(Exception e) {
        	System.err.println(e.getMessage());
        }
        
    }
	
}

