package eu.excitementproject.eop.core.component.distance;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import static org.junit.Assert.*;

public class FixedWeightTokenEditDistanceTest {

	@Test
	public void test() {
    	
        FixedWeightTokenEditDistance fixedEd
            = new FixedWeightTokenEditDistance();

        CasCreation  aCas = new CasCreation();
        JCas mycas = aCas.create();
        
        try {
        	
        	System.out.println(fixedEd.calculation(mycas).getDistance());
        	assertTrue(fixedEd.calculation(mycas).getDistance() == 0.19230769230769232);
        	
        } catch(Exception e) {
        	System.err.println(e.getMessage());
        }
        
    }
	
}

