package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * The <code>BagOfDepsPosScoring</code> class is very similar to
 * <code>BagOfDepsPosScoring</code>, except it uses dependency triples
 * containing lemma+POS(coarse-grained) instead of word forms to calculate the
 * scores.
 * 
 * @author Rui Wang
 * @since March 2013
 */
public class BagOfDepsPosScoring extends BagOfDepsScoring {

	/**
	 * the number of features
	 */
	private int numOfFeats = 12;

	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "BagOfDepsPosScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T)), with four
		// different matching types
		Vector<Double> scoresVector = new Vector<Double>();

		try {
			JCas tView = aCas.getView("TextView");
			JCas hView = aCas.getView("HypothesisView");

			for (int i = 1; i < 5; i++) {
				HashMap<String, Integer> tBag = countDeps(tView, i);
				HashMap<String, Integer> hBag = countDeps(hView, i);
				scoresVector.addAll(calculateSimilarity(tBag, hBag));
			}

		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	protected HashMap<String, Integer> countDeps(JCas text, int matchType) {
		HashMap<String, Integer> tokenNumMap = new HashMap<String, Integer>();

		Map<String, String> depMap = indexLemmaDepTree(text);

		for (final Iterator<Entry<String, String>> iter = depMap.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, String> entry = iter.next();
			final String dep = entry.getKey();
			String[] childItems = dep.split(" ### ");
			String child = childItems[1].toLowerCase() + " ### "
					+ childItems[2].charAt(0);
			String[] items = entry.getValue().split(" ## ");
			String depRel = items[0];
			String[] parentItems = items[1].split(" ### ");
			String parent = parentItems[1].toLowerCase() + " ### "
					+ parentItems[2].charAt(0);
			String matchDep = child + " ## " + depRel + " ## " + parent;
			switch (matchType) {
			case 1:
				// full match
				break;
			case 2:
				// parent and dep relation match
				matchDep = depRel + " ## " + parent;
				break;
			case 3:
				// child and dep relation match
				matchDep = child + " ## " + depRel;
				break;
			case 4:
				// parent and child match
				matchDep = child + " ## " + parent;
				break;
			default:
				break;
			}

			// count the frequency
			Integer num = tokenNumMap.get(matchDep);
			if (null == num) {
				tokenNumMap.put(matchDep, 1);
			} else {
				tokenNumMap.put(matchDep, num + 1);
			}
		}

		return tokenNumMap;
	}
}
