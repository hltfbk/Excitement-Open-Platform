package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * The <code>BagOfWordsScoring</code> class implements the
 * <code>ScoringComponent</code> interface. It takes a T-H pair as input,
 * represents each text into a bag of tokens (store it in a HashMap), and
 * calculate several similarity scores of the pair.
 * 
 * The HashMap takes the token text as key, and its frequency in the sentence as
 * value.
 * 
 * The similarity scores include: 1) the ratio between the number of overlapping
 * tokens and the number of tokens in H; 2) the ratio between the number of
 * overlapping tokens and the number of tokens in T; 3) the product of the above
 * two
 * 
 * @author Rui
 */
public class BagOfWordsScoring implements ScoringComponent {
	
//	the number of features
	protected int numOfFeats = 3;

	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "BagOfWordsScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}
	
	public void close() throws ScoringComponentException{
		
	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {
		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> scoresVector = new Vector<Double>();

		try {
			JCas tView = cas.getView("TextView");
			HashMap<String, Integer> tBag = countTokens(tView);

			JCas hView = cas.getView("HypothesisView");
			HashMap<String, Integer> hBag = countTokens(hView);

			scoresVector.addAll(calculateSimilarity(tBag, hBag));
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	/**
	 * Count the tokens contained in a text and store the counts in a HashMap
	 * 
	 * @param text
	 *            the input text represented in a JCas
	 * @return a HashMap represents the bag of tokens contained in the text, in
	 *         the form of <Token, Frequency>
	 */
	protected HashMap<String, Integer> countTokens(JCas text) {
		HashMap<String, Integer> tokenNumMap = new HashMap<String, Integer>();
		Iterator<Annotation> tokenIter = text.getAnnotationIndex(Token.type)
				.iterator();
		while (tokenIter.hasNext()) {
			Token curr = (Token) tokenIter.next();
			String tokenText = curr.getCoveredText();
			Integer num = tokenNumMap.get(tokenText);
			if (null == num) {
				tokenNumMap.put(tokenText, 1);
			} else {
				tokenNumMap.put(tokenText, num + 1);
			}
		}
		return tokenNumMap;
	}

	/**
	 * Calculate the similarity between two bags of tokens
	 * 
	 * @param tBag
	 *            the bag of tokens of T stored in a HashMap
	 * @param hBag
	 *            the bag of tokens of H stored in a HashMap
	 * @return a vector of double values, which contains: 1) the ratio between
	 *         the number of overlapping tokens and the number of tokens in H;
	 *         2) the ratio between the number of overlapping tokens and the
	 *         number of tokens in T; 3) the product of the above two
	 */
	protected Vector<Double> calculateSimilarity(HashMap<String, Integer> tBag,
			HashMap<String, Integer> hBag) {
		double sum = 0.0d;
		int hSize = 0;
		int tSize = 0;
		for (String hToken : hBag.keySet()) {
			hSize += hBag.get(hToken).intValue();
			if (!tBag.keySet().contains(hToken)) {
				continue;
			}
			sum += Math.min(hBag.get(hToken).intValue(), tBag.get(hToken)
					.intValue());
		}
		for (String tToken : tBag.keySet()) {
			tSize += tBag.get(tToken).intValue();
		}
		Vector<Double> returnValue = new Vector<Double>();
		returnValue.add(sum / hSize);
		returnValue.add(sum / tSize);
		returnValue.add(sum * sum / hSize / tSize);
		return returnValue;
	}

}
