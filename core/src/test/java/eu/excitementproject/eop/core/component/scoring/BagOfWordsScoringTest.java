package eu.excitementproject.eop.core.component.scoring;

import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;

public class BagOfWordsScoringTest {

	static Logger logger = Logger.getLogger(BagOfWordsScoringTest.class
			.getName());

	@Test
	public void test() {
		BagOfWordsScoring bows = new BagOfWordsScoring();
		// BagOfLemmasScoring bols = new BagOfLemmasScoring();
		BagOfLexesScoring bols = new BagOfLexesScoring(true, true, true);

		JCas aCas = null;
		OpenNLPTaggerEN lap = null;

		try {
			lap = new OpenNLPTaggerEN();
//			aCas = lap.generateSingleTHPairCAS(
//					"The person is hired as a postdoc.",
//					"The person must have a PhD.");
			aCas = lap.generateSingleTHPairCAS(
					"Ich bin ein Student .",
					"Er ist kein Person .");
			Vector<Double> scoresVector1 = bows.calculateScores(aCas);
			 Vector<Double> scoresVector2 = bols.calculateScores(aCas);
			logger.info("The bag of words scores:");
			for (Double score : scoresVector1) {
				logger.info(String.valueOf(score));
			}
			 logger.info("The bag of lexical scores:");
			 for (Double score : scoresVector2) {
			 logger.info(String.valueOf(score));
			 }
		} catch (LAPException e) {
			logger.info(e.getMessage());
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}
	}
}
