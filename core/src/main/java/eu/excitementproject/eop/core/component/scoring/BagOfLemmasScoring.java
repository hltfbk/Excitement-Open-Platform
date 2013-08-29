package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * The <code>BagOfWordsScoring</code> class implements the
 * <code>ScoringComponent</code> interface. It is very similar to
 * <code>BagOfWordsScoring</code>, except it uses lemmas instead of word forms
 * to calculate the scores.
 * 
 * @author Rui Wang
 * @since November 2012
 */
public class BagOfLemmasScoring extends BagOfWordsScoring {

	/**
	 * the number of features
	 */
	private int numOfFeats = 3;

	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "BagOfLemmasScoring";
	}

	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> scoresVector = new Vector<Double>();

		try {
			JCas tView = aCas.getView("TextView");
			HashMap<String, Integer> tBag = countTokens(tView);

			JCas hView = aCas.getView("HypothesisView");
			HashMap<String, Integer> hBag = countTokens(hView);

			scoresVector.addAll(calculateSimilarity(tBag, hBag));
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	/**
	 * Count the lemmas contained in a text and store the counts in a HashMap
	 * 
	 * @param text
	 *            the input text represented in a JCas
	 * @return a HashMap represents the bag of lemmas contained in the text, in
	 *         the form of <Lemma, Frequency>
	 */
	protected HashMap<String, Integer> countTokens(JCas text) {
		HashMap<String, Integer> tokenNumMap = new HashMap<String, Integer>();
		Iterator<Annotation> tokenIter = text.getAnnotationIndex(Token.type)
				.iterator();
		while (tokenIter.hasNext()) {
			Token curr = (Token) tokenIter.next();
			String tokenText = curr.getLemma().getValue();
			Integer num = tokenNumMap.get(tokenText);
			if (null == num) {
				tokenNumMap.put(tokenText, 1);
			} else {
				tokenNumMap.put(tokenText, num + 1);
			}
		}
		return tokenNumMap;
	}
}
