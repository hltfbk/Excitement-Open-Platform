package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;

/**
 * The <code>BagOfLexesScoring</code> class extends <code>BagOfWordsScoring</code>.
 * 
 * ToDo: for the moment, I use the word forms; in the future, may consider to use lemmas (<code>BagOfLemmasScoring</code>).
 * 
 * @author  Rui
 */
public class BagOfLexesScoring extends BagOfWordsScoring {
	
	private GermanDistSim gds = null;
	
	public BagOfLexesScoring() {
		try {
			gds = new GermanDistSim("src/main/resources/dewakdistributional-data");
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
			//throw e;
		}
		catch (BaseException e)
		{
			e.printStackTrace(); 
		}
	}
	
	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		// 1) how many words of H (extended with multiple relations) can be found in T divided by the length of H
		Vector<Double> scoresVector = new Vector<Double>();
	
		double score = 0.0d;
		
		try {
			JCas tView = aCas.getView("TextView");
			HashMap<String, Integer> tBag = countTokens(tView);

			JCas hView = aCas.getView("HypothesisView");
			HashMap<String, Integer> hBag = countTokens(hView);
			
			for (String word : hBag.keySet()) {
				int counts = hBag.get(word);
				HashMap<String, Integer> hWordBag = new HashMap<String, Integer>();
				try {
					hWordBag.put(word, counts);
					for (LexicalRule<? extends GermanDistSimInfo> rule : gds.getRulesForLeft(word, null)) {
						hWordBag.put(rule.getRLemma(), counts);
					}
				}
				catch (LexicalResourceException e)
				{
					throw new ScoringComponentException(e.getMessage());
				}
				score += Math.min(counts, calculateSimilarity(tBag, hWordBag).get(0) * hWordBag.size());
			}
			
			int hSize = 0;
			for (String hWord : hBag.keySet()) {
				hSize += hBag.get(hWord).intValue();
			}
			
			scoresVector.add(score / hSize);
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}
}
