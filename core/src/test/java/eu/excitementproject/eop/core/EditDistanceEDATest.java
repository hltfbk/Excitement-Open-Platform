package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import static org.junit.Assert.*;
import eu.excitementproject.eop.core.component.distance.CasCreation;
import java.util.List;
import java.util.ArrayList;
import eu.excitementproject.eop.common.configuration.CommonConfig;

public class EditDistanceEDATest {

	@Test
	public void test() {
    	
        EditDistanceEDATest editDistance
            = new EditDistanceEDATest();
        
		List<JCas> casList = new ArrayList<JCas>(2);
		CasCreation cas1 = new CasCreation("The person is hired as a postdoc.", 
					"The person is hired as a postdoc.", "ENTAILMENT");
		casList.add(cas1.create());
		CasCreation cas2 = new CasCreation("The train was unconfortable.", 
				    "The train was expensive.", "NONENTAILMENT");
		casList.add(cas2.create());
		
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

