/**
 * This package contains all the scoring components implements <code>ScoringComponent</code>.
 */
package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.entailment.EntailmentMetadata;
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
 * @author Rui Wang
 * @since November 2012
 */
public class BagOfWordsScoring implements ScoringComponent {
	
	/**
	 * the number of features
	 */
	private int numOfFeats = 7;

	/**
	 * get the number of features
	 * @return
	 */
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
	
	/**
	 * close the component
	 * @throws ScoringComponentException
	 */
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
			
			String task = JCasUtil.select(cas, EntailmentMetadata.class).iterator().next().getTask();
			if (null == task) {
				scoresVector.add(0d);
				scoresVector.add(0d);
				scoresVector.add(0d);
				scoresVector.add(0d);				
			} else {
				scoresVector.add(isTaskIE(task));
				scoresVector.add(isTaskIR(task));
				scoresVector.add(isTaskQA(task));
				scoresVector.add(isTaskSUM(task));
			}
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
		
		for (final Iterator<Entry<String, Integer>> iter = hBag.entrySet().iterator(); iter.hasNext();) {
			Entry<String, Integer> entry = iter.next();
			final String hToken = entry.getKey();
			hSize += entry.getValue().intValue();
			if (!tBag.keySet().contains(hToken)) {
				continue;
			}
			sum += Math.min(hBag.get(hToken).intValue(), tBag.get(hToken)
						.intValue());
		}
		for (final Iterator<Entry<String, Integer>> iter = tBag.entrySet().iterator(); iter.hasNext();) {
			tSize += iter.next().getValue().intValue();
		}
		Vector<Double> returnValue = new Vector<Double>();
		returnValue.add(sum / hSize);
		returnValue.add(sum / tSize);
		returnValue.add(sum * sum / hSize / tSize);
		return returnValue;
	}
	
	/**
	 * check whether the task is IE
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	protected double isTaskIE(String task) {
		if (task.equalsIgnoreCase("IE")) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * check whether the task is IR
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	protected double isTaskIR(String task) {
		if (task.equalsIgnoreCase("IR")) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * check whether the task is QA
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	protected double isTaskQA(String task) {
		if (task.equalsIgnoreCase("QA")) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * check whether the task is SUM
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	protected double isTaskSUM(String task) {
		if (task.equalsIgnoreCase("SUM")) {
			return 1;
		}
		return 0;
	}

}
