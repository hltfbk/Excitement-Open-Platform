package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import static org.junit.Assert.*;
//import eu.excitementproject.eop.core.component.distance.CasCreation;
import java.util.List;
import java.util.ArrayList;

import eu.excitementproject.eop.common.IEditDistanceTEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.ExampleLAP;

public class EditDistanceEDATest {

	@Test
	public void test() {
    	
		
        ExampleLAP lap = null; 
        try 
        {
        	lap = new ExampleLAP(); 
        }
        catch (LAPException e)
        {
        	System.err.println(e.getMessage()); 
        }
        
		List<JCas> casList = new ArrayList<JCas>(2);
		
		try {
			JCas jcas1 = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			JCas jcas2 = lap.generateSingleTHPairCAS("The train was uncomfortable", "The train was comfortable", "NONENTAILMENT"); 
			casList.add(jcas1); 
			casList.add(jcas2); 
		} catch (LAPException e)
		{
			e.printStackTrace(); 
		}
		
		EditDistanceEDA<IEditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<IEditDistanceTEDecision>();
		
		CommonConfig config = null;
		
		try {
			
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
			IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(casList.get(0));
			// System.err.println(teDecision1.getDecision().toString()) ;
			assertTrue(teDecision1.getDecision().toString().equals("Entailment"));
			IEditDistanceTEDecision teDecision2 = editDistanceEDA.process(casList.get(1));
			// System.err.println(teDecision2.getDecision().toString()) ;
			assertTrue(teDecision2.getDecision().toString().equals("NonEntailment"));
		
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
    }
	
}

