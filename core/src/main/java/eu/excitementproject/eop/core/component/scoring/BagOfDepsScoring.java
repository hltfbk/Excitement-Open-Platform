package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;

/**
 * The <code>BagOfDepsScoring</code> class implements the
 * <code>ScoringComponent</code> interface. It is very similar to
 * <code>BagOfWordsScoring</code>, except it uses dependency triples instead of
 * word forms to calculate the scores.
 * 
 * There are four ways to calculate the dependency triple similarity (
 * <code>matchType</code>): 1) full match: child, dep_rel, parent 2) only
 * parent: dep_rel, parent 3) only child: child, dep_rel 4) parent and child:
 * child, parent
 * 
 * @author Rui Wang
 * @since March 2013
 */
public class BagOfDepsScoring extends BagOfWordsScoring {

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
		return "BagOfDepsScoring";
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

		Map<String, String> depMap = indexDepTree(text);

		for (final Iterator<Entry<String, String>> iter = depMap.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, String> entry = iter.next();
			final String dep = entry.getKey();
			String childToken = dep.split(" ### ")[1];
			String[] parentItems = entry.getValue().split(" ## ");
			String depRel = parentItems[0];
			String parentToken = parentItems[1].split(" ### ")[1];
			String matchDep = childToken + " ## " + depRel + " ## "
					+ parentToken;
			switch (matchType) {
			case 1:
				// full match
				break;
			case 2:
				// parent and dep relation match
				matchDep = depRel + " ## " + parentToken;
				break;
			case 3:
				// child and dep relation match
				matchDep = childToken + " ## " + depRel;
				break;
			case 4:
				// parent and child match
				matchDep = childToken + " ## " + parentToken;
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

	protected Map<String, Integer> countTokenPoses(JCas text) {
		Map<String, Integer> tokenNumMap = new HashMap<String, Integer>();
		Iterator<Annotation> tokenIter = text.getAnnotationIndex(Token.type)
				.iterator();
		while (tokenIter.hasNext()) {
			Token curr = (Token) tokenIter.next();
			String tokenText = curr.getLemma().getValue().replace("#", "\\#")
					+ " ### " + curr.getPos().getPosValue();
			Integer num = tokenNumMap.get(tokenText);
			if (null == num) {
				tokenNumMap.put(tokenText, 1);
			} else {
				tokenNumMap.put(tokenText, num + 1);
			}
		}
		return tokenNumMap;
	}

	protected Map<String, String> indexDepTree(JCas text) {
		Map<String, String> depTree = new HashMap<String, String>();

		// format: key: 1 ### word ### pos; value: dep_rel ## 2 ### word ### pos
		// escape: .replace("#", "\\#")
		// depTree.put("1 ### The ### Det", "DET ## 2 ### dog ### N");
		// depTree.put("2 ### dog ### N", "SUBJ ## 3 ### chases ### V");
		// depTree.put("3 ### chases ### V", "ROOT ## 0 ### NULL ### NULL");
		// depTree.put("4 ### The ### Det", "DET ## 5 ### cat ### N");
		// depTree.put("5 ### cat ### N", "OBJ ## 3 ### chases ### V");
		for (Dependency dep : JCasUtil.select(text, Dependency.class)) {
			Token child = dep.getDependent();
			Token parent = dep.getGovernor();
			depTree.put(child.getBegin() + " ### "
					+ child.getCoveredText().replace("#", "\\#") + " ### "
					+ child.getPos().getPosValue(), dep.getDependencyType()
					+ " ## " + parent.getBegin() + " ### "
					+ parent.getCoveredText().replace("#", "\\#") + " ### "
					+ parent.getPos().getPosValue());
		}

		return depTree;
	}

	protected Map<String, String> indexLemmaDepTree(JCas text) {
		Map<String, String> depTree = new HashMap<String, String>();

		for (Dependency dep : JCasUtil.select(text, Dependency.class)) {
			Token child = dep.getDependent();
			Token parent = dep.getGovernor();
			depTree.put(child.getBegin() + " ### "
					+ child.getLemma().getValue().replace("#", "\\#") + " ### "
					+ child.getPos().getPosValue(), dep.getDependencyType()
					+ " ## " + parent.getBegin() + " ### "
					+ parent.getLemma().getValue().replace("#", "\\#")
					+ " ### " + parent.getPos().getPosValue());
		}

		return depTree;
	}
}
