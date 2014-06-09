package eu.excitementproject.eop.core.component.scoring;

//import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;

public class BagOfDepsScoringTest {
	
	static Logger logger = Logger.getLogger(BagOfDepsScoringTest.class
			.getName());
	
	@Test
	public void test() throws LexicalResourceException {
		testEN();
	}
	
	public void testEN() {
		BagOfDepsScoring bds = new BagOfDepsScoring();
		BagOfDepsPosScoring bdps = new BagOfDepsPosScoring();

		JCas aCas = null;
		LAPAccess lap = null;

		try {
			// this is old code (that doesn't load poly, but only default) 
			// lap = new MaltParserEN("poly");

			// New usage for MaltParserEN passing parameter for Model variant. 
//			HashMap<String, String> descArgs = new HashMap<String,String>(); 
//			descArgs.put("PARSER_MODEL_VARIANT", "poly"); 
			lap = new MaltParserEN("poly"); 

			// Entailment
			aCas = lap.generateSingleTHPairCAS(
					"The person is hired as a postdoc.",
					"The person must have a PhD.");
			Vector<Double> scoresVector1 = bds.calculateScores(aCas);
			Vector<Double> scoresVector2 = bdps.calculateScores(aCas);
			logger.info("The bag of deps scores:");
			for (Double score : scoresVector1) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of deps scores:");
			for (Double score : scoresVector2) {
				logger.info(String.valueOf(score));
			}
		} catch (LAPException e) {
			logger.info(e.getMessage());
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}
		
		try {
			bds.close();
			logger.info("Components and lexical resources are closed.");
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}
	}
}
