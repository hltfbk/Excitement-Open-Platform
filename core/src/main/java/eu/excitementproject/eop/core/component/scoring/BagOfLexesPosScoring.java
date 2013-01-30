package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;

public class BagOfLexesPosScoring extends BagOfLexesScoring {

	public BagOfLexesPosScoring(boolean useGDS, boolean useGNWCau,
			boolean useGNWEnt, boolean useGNWHyn, boolean useGNWSyn)
			throws LexicalResourceException {
		super(useGDS, useGNWCau, useGNWEnt, useGNWHyn, useGNWSyn);
	}
	
	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		// 1) how many words of H (extended with multiple relations) can be found in T divided by the length of H
		Vector<Double> scoresVector = new Vector<Double>();
			
		try {
			JCas tView = aCas.getView("TextView");
			HashMap<String, Integer> tBag = countTokens(tView);

			JCas hView = aCas.getView("HypothesisView");
			HashMap<String, Integer> hBag = countTokens(hView);
			
			if (moduleFlags[0]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gds));
			}
			if (moduleFlags[1]) {
				scoresVector.add(calculateSingleLexScoreWithGermaNetRelation(tBag, hBag, GermaNetRelation.has_hypernym));
			}
			if (moduleFlags[2]) {
				scoresVector.add(calculateSingleLexScoreWithGermaNetRelation(tBag, hBag, GermaNetRelation.causes));
			}
			if (moduleFlags[3]) {
				scoresVector.add(calculateSingleLexScoreWithGermaNetRelation(tBag, hBag, GermaNetRelation.entails));
			}
			if (moduleFlags[4]) {
				scoresVector.add(calculateSingleLexScoreWithGermaNetRelation(tBag, hBag, GermaNetRelation.has_synonym));
			}
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}
	
	/**
	 * Count the lemmas and POSes contained in a text and store the counts in a HashMap
	 * 
	 * @param text
	 *            the input text represented in a JCas
	 * @return a HashMap represents the bag of lemmas and POSes contained in the text, in
	 *         the form of <Lemma ### POS, Frequency>
	 */
	protected HashMap<String, Integer> countTokens(JCas text) {
		HashMap<String, Integer> tokenNumMap = new HashMap<String, Integer>();
		Iterator<Annotation> tokenIter = text.getAnnotationIndex(Token.type)
				.iterator();
		while (tokenIter.hasNext()) {
			Token curr = (Token) tokenIter.next();
			String tokenText = curr.getLemma().getValue() + " ### " + curr.getPos().getPosValue();
			Integer num = tokenNumMap.get(tokenText);
			if (null == num) {
				tokenNumMap.put(tokenText, 1);
			} else {
				tokenNumMap.put(tokenText, num + 1);
			}
		}
		return tokenNumMap;
	}
	
	protected double calculateSingleLexScoreWithGermaNetRelation(HashMap<String, Integer> tBag, HashMap<String, Integer> hBag, GermaNetRelation gnr) throws ScoringComponentException{
		if (null == gnw) {
			throw new ScoringComponentException("WARNING: the specified lexical resource has not been properly initialized!");
		}
		
		double score = 0.0d;
		HashMap<String, Integer> tWordBag = new HashMap<String, Integer>();
		
		for (String word : tBag.keySet()) {
			int counts = tBag.get(word);
			try {
				tWordBag.put(word, counts);
				String POS = word.split(" ### ")[1];
				for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForLeft(word.split(" ### ")[0], new GermanPartOfSpeech(POS), gnr)) {
					String tokenText = rule.getRLemma() + " ### " + POS;
					if (tWordBag.containsKey(tokenText)) {
						int tmp = tWordBag.get(tokenText);
						tWordBag.put(tokenText, tmp + counts);
					} else {
						tWordBag.put(tokenText, counts);
					}
				}
			} catch (LexicalResourceException e) {
				throw new ScoringComponentException(e.getMessage());
			} catch (UnsupportedPosTagStringException e) {
				throw new ScoringComponentException(e.getMessage());
			}
		}
		
		score = calculateSimilarity(tWordBag, hBag).get(0);
		
		return score;
	}

}
