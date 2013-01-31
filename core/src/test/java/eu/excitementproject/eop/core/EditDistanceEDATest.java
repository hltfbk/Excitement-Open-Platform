package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import eu.excitementproject.eop.core.component.distance.CasCreation;
//import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;


import org.apache.uima.cas.FSIterator;
//import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;

import eu.excitementproject.eop.common.IEditDistanceTEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.ExampleLAP;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class EditDistanceEDATest {

	//@Test
	public void test() {
    	
		/*
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
		*/
		
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<IEditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<IEditDistanceTEDecision>();
		
		try {
		
			File configFile = new File("./src/test/resources/ITA_EditDistance_TokenEditDistance_configuration_file.xml");
			CommonConfig config = new ImplCommonConfig(configFile);
		
			editDistanceEDA.setTrain(true);
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
			
			File testDir = null;
			testDir = new File("./target/IT/test/");
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, System.out);
				//JCas cas = PlatformCASProber.probeXmi(xmi, null);
				IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				// System.err.println(teDecision1.getDecision().toString()) ;
				//System.err.println(teDecision1.getDecision().toString());
				System.err.println(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			    
			}
			
			scorer(list);
			/*
			IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(casList.get(0));
			// System.err.println(teDecision1.getDecision().toString()) ;
			assertTrue(teDecision1.getDecision().toString().equals("Entailment"));
			IEditDistanceTEDecision teDecision2 = editDistanceEDA.process(casList.get(1));
			// System.err.println(teDecision2.getDecision().toString()) ;
			assertTrue(teDecision2.getDecision().toString().equals("NonEntailment"));
			*/
		
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
    }
	
	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return the pairID of the T-H pair
	 */
	protected String getPairID(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		return p.getPairID();
	}
	
	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return if the T-H pair contains the gold answer, return it; otherwise,
	 *         return null
	 */
	protected String getGoldLabel(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		if (null == p.getGoldAnswer() || p.getGoldAnswer().equals("")
				|| p.getGoldAnswer().equals("ABSTAIN")) {
			return null;
		} else {
			return p.getGoldAnswer();
		}
	}
	
	//taken for the code provided by RUI
	public void scorer(ArrayList<String> list) {
	
		float pos_corrt = 0f;
		float pos_wrong = 0f;
		float neg_corrt = 0f;
		float neg_wrong = 0f;
		
		
		Iterator<String> iterator = list.iterator();
		
		while(iterator.hasNext()) {	
		
			String line = iterator.next();
			
			String[] items = line.split("\t");
			//if (items.length != 4) {
			//	logger.warning("Wrong format! Ignore the line...");
			//	continue;
			//}
			if (items[1].equalsIgnoreCase("Entailment")) {
				if (items[2].equalsIgnoreCase("Entailment")) {
					pos_corrt += 1f;
				} else if (items[2].equalsIgnoreCase("NonEntailment")) {
					pos_wrong += 1f;
				} else {
					System.err.println("Wrong format! Ignore the line...");
					continue;
				}
			} else if (items[1].equalsIgnoreCase("NonEntailment")) {
				if (items[2].equalsIgnoreCase("NonEntailment")) {
					neg_corrt += 1f;
				} else if (items[2].equalsIgnoreCase("Entailment")) {
					neg_wrong += 1f;
				} else {
					System.err.println("Wrong format! Ignore the line...");
					continue;
				}
			} else {
				System.err.println("Wrong format! Ignore the line...");
				continue;
			}
		}
		
	//	logger.info(String.valueOf(pos_corrt));
	//	logger.info(String.valueOf(pos_wrong));
	//	logger.info(String.valueOf(neg_corrt));
	//	logger.info(String.valueOf(neg_wrong));
		
		float EntailmentGold = pos_corrt + pos_wrong;
		float NonEntailmentGold = neg_corrt + neg_wrong;
		float Sum = EntailmentGold + NonEntailmentGold;
		float EntailmentPrecision = pos_corrt / (pos_corrt + neg_wrong);
		float EntailmentRecall = pos_corrt / EntailmentGold;
		float EntailmentFMeasure = 2 * EntailmentPrecision * EntailmentRecall / (EntailmentPrecision + EntailmentRecall);
		float NonEntailmentPrecision = neg_corrt / (neg_corrt + pos_wrong);
		float NonEntailmentRecall = neg_corrt / NonEntailmentGold;
		float NonEntailmentFMeasure = 2 * NonEntailmentPrecision * NonEntailmentRecall / (NonEntailmentPrecision + NonEntailmentRecall);
		float Accuracy = (pos_corrt + neg_corrt) / Sum;
		
		System.out.println("EntailmentGold:" + EntailmentGold);
		System.out.println("NonEntailmentGold:" +  NonEntailmentGold);
		System.out.println("EntailmentPrecision:" + EntailmentPrecision);
		System.out.println("EntailmentRecall:" + EntailmentRecall);
		System.out.println("EntailmentFMeasure:" + EntailmentFMeasure);
		System.out.println("NonEntailmentPrecision:" + NonEntailmentPrecision);
		System.out.println("NonEntailmentRecall:" + NonEntailmentRecall);
		System.out.println("NonEntailmentFMeasure:" + NonEntailmentFMeasure);
		System.out.println("Accuracy:" + Accuracy);
		
	}
	
}

