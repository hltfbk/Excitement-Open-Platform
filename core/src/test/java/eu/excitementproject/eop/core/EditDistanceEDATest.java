package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import static org.junit.Assert.*;
//import eu.excitementproject.eop.core.component.distance.CasCreation;
import java.util.List;
import java.util.ArrayList;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.SampleLAP; 

public class EditDistanceEDATest {

	@Test
	public void test() {
    	
//        EditDistanceEDATest editDistance
//            = new EditDistanceEDATest();
        
        // CasCreation is basically a broken code (since it uses file path)  
        // I replaced the code to lap/SampleLAP  --- Gil 
        
        SampleLAP lap = null; 
        try 
        {
        	lap = new SampleLAP(); 
        }
        catch (LAPException e)
        {
        	System.err.println(e.getMessage()); 
        }
        
		List<JCas> casList = new ArrayList<JCas>(2);
		//CasCreation cas1 = new CasCreation("The person is hired as a postdoc.", 
		//			"The person is hired as a postdoc.", "ENTAILMENT");
		//casList.add(cas1.create());
		//CasCreation cas2 = new CasCreation("The train was unconfortable.", 
		//		    "The train was expensive.", "NONENTAILMENT");
		//casList.add(cas2.create());
		try {
			JCas jcas1 = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 
			JCas jcas2 = lap.generateSingleTHPairCAS("The train was uncomfortable", "The train was comfortable", "NONENTAILMENT"); 
			casList.add(jcas1); 
			casList.add(jcas2); 
		} catch (LAPException e)
		{
			e.printStackTrace(); 
		}
		
		EditDistanceEDA<IEditDistanceTEDecision> edit = 
				new EditDistanceEDA<IEditDistanceTEDecision>(casList);
		CommonConfig config = null;
		
		try {
			
			edit.initialize(config);
			edit.startTraining(config);
			IEditDistanceTEDecision teDecision1 = edit.process(casList.get(0));
			assertTrue(teDecision1.getDecision().toString().equals("Entailment"));
			IEditDistanceTEDecision teDecision2 = edit.process(casList.get(1));
			assertTrue(teDecision2.getDecision().toString().equals("NonEntailment"));
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
    }
	
}

