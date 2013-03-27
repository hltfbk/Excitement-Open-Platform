package eu.excitementproject.eop.core.component.scoring;

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

		JCas aCas = null;
		LAPAccess lap = null;

		try {
       	lap = new MaltParserEN("poly");
       	// Entailment
			aCas = lap.generateSingleTHPairCAS(
					"The person is hired as a postdoc.",
					"The person must have a PhD.");
			Vector<Double> scoresVector1 = bds.calculateScores(aCas);
			logger.info("The bag of deps scores:");
			for (Double score : scoresVector1) {
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
