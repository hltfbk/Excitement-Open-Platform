package eu.excitementproject.eop.core.component.scoring;

import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class BagOfWordsScoringTest {

	static Logger logger = Logger.getLogger(BagOfWordsScoringTest.class
			.getName());

	@Test
	public void test() {
		boolean isEN = false;
		
		BagOfWordsScoring bows = new BagOfWordsScoring();
		 BagOfLemmasScoring bols = new BagOfLemmasScoring();
		BagOfLexesScoring bolexs = new BagOfLexesScoring(true, true, true, true, true);

		JCas aCas = null;
		LAPAccess lap = null;

		try {
        	if (isEN) {
            	lap = new TreeTaggerEN();
        	} else {
        		lap = new TreeTaggerDE();
        	}
//			aCas = lap.generateSingleTHPairCAS(
//					"The person is hired as a postdoc.",
//					"The person must have a PhD.");
			aCas = lap.generateSingleTHPairCAS(
					"Ich bin ein Student .",
					"Er ist kein Person .");
			Vector<Double> scoresVector1 = bows.calculateScores(aCas);
			 Vector<Double> scoresVector2 = bols.calculateScores(aCas);
			 Vector<Double> scoresVector3 = bolexs.calculateScores(aCas);
			logger.info("The bag of words scores:");
			for (Double score : scoresVector1) {
				logger.info(String.valueOf(score));
			}
			logger.info("The bag of lemmas scores:");
			for (Double score : scoresVector2) {
				logger.info(String.valueOf(score));
			}
			 logger.info("The bag of lexical scores:");
			 for (Double score : scoresVector3) {
			 logger.info(String.valueOf(score));
			 }
		} catch (LAPException e) {
			logger.info(e.getMessage());
		} catch (ScoringComponentException e) {
			logger.info(e.getMessage());
		}
	}
}
