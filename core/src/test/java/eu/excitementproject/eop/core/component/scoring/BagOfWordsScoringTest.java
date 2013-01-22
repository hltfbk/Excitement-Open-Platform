package eu.excitementproject.eop.core.component.scoring;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class BagOfWordsScoringTest {

	static Logger logger = Logger.getLogger(BagOfWordsScoringTest.class
			.getName());
	
	@Test
	public void test() throws LexicalResourceException {
		testDE();
		testEN();
	}

	public void testDE() throws LexicalResourceException {
		BagOfWordsScoring bows = new BagOfWordsScoring();
		 BagOfLemmasScoring bols = new BagOfLemmasScoring();
		BagOfLexesScoring bolexs = new BagOfLexesScoring(true, true, true, true, true);

		JCas aCas = null;
		LAPAccess lap = null;

		try {
        	lap = new TreeTaggerDE();
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
	
	public void testEN() throws LexicalResourceException {
		BagOfWordsScoring bows = new BagOfWordsScoring();
		 BagOfLemmasScoring bols = new BagOfLemmasScoring();
		 
		 Set<WordNetRelation> wnRelSet = new HashSet<WordNetRelation>();
		 wnRelSet.add(WordNetRelation.HYPERNYM);
		 wnRelSet.add(WordNetRelation.SYNONYM);
		 
		 Set<RelationType> voRelSet = new HashSet<RelationType>();
		 voRelSet.add(RelationType.STRONGER_THAN);
		 voRelSet.add(RelationType.CAN_RESULT_IN);
		 voRelSet.add(RelationType.SIMILAR);
		 
		BagOfLexesScoringEN bolexs = new BagOfLexesScoringEN(wnRelSet, voRelSet);

		JCas aCas = null;
		LAPAccess lap = null;

		try {
       	lap = new TreeTaggerEN();
       	// Entailment
			aCas = lap.generateSingleTHPairCAS(
					"The person is hired as a postdoc.",
					"The person must have a PhD.");
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
