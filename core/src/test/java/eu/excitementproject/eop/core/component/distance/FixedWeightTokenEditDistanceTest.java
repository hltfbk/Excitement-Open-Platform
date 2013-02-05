package eu.excitementproject.eop.core.component.distance;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import static org.junit.Assert.*;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.ExampleLAP;


public class FixedWeightTokenEditDistanceTest {

	@Test
	public void test() {
    	
        FixedWeightTokenEditDistance fixedEd
            = new FixedWeightTokenEditDistance();
        
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
        	assertTrue(fixedEd.calculation(mycas).getDistance() == 0.19230769230769232);
        	
        } catch(Exception e) {
        	System.err.println(e.getMessage());
        }
        
    }
	
}

