package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;

/**
 * The <code>BagOfLexesScoring</code> class extends <code>BagOfWordsScoring</code>.
 * 
 * ToDo: for the moment, I use the word forms; in the future, may consider to use lemmas (<code>BagOfLemmasScoring</code>).
 * 
 * @author  Rui
 */
public class BagOfLexesScoring extends BagOfWordsScoring {
	
	static Logger logger = Logger.getLogger(BagOfLexesScoring.class.getName());
	
//	the number of features
	protected int numOfFeats = 0;
	
	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}
	
	private GermanDistSim gds = null;
	
	private GermaNetWrapper gnw = null;

	public BagOfLexesScoring(boolean useGDS, boolean useGNWHyn, boolean useGNWSyn) {
		if (useGDS) {
			try {
				gds = new GermanDistSim("src/main/resources/dewakdistributional-data");
				numOfFeats ++;
			}
			catch (GermanDistSimNotInstalledException e) {
				logger.warning("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
				//throw e;
			}
			catch (BaseException e)
			{
				logger.info(e.getMessage());
			}
		}
		if (useGNWHyn || useGNWSyn) {
			try {
				gnw = new GermaNetWrapper("./src/main/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/");
				numOfFeats ++;
				if (useGNWHyn && useGNWSyn) {
					numOfFeats ++;
				}
			}
			catch (GermaNetNotInstalledException e) {
				logger.warning("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
				//throw e;
			}
			catch (BaseException e) {
				logger.info(e.getMessage());
			}
		}
	}
	
	@Override
	public String getComponentName() {
		return "BagOfLexesScoring";
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
			
			int hSize = 0;
			for (String hWord : hBag.keySet()) {
				hSize += hBag.get(hWord).intValue();
			}
			
			scoresVector.add(calculateSingleLexScore(tBag, hBag, gds) / hSize);
			
			scoresVector.add(calculateSingleLexScoreWithGermaNetRelation(tBag, hBag, GermaNetRelation.has_hypernym, false, false) / hSize);
			
			scoresVector.add(calculateSingleLexScoreWithGermaNetRelation(tBag, hBag, GermaNetRelation.has_synonym, true, true) / hSize);
		
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}
	
	protected double calculateSingleLexScore(HashMap<String, Integer> tBag, HashMap<String, Integer> hBag, LexicalResource<? extends RuleInfo> lex) throws ScoringComponentException{
		if (null == lex) {
			throw new ScoringComponentException("WARNING: the specified lexical resource has not been properly initialized!");
		}
		
		double score = 0.0d;
		
		for (String word : hBag.keySet()) {
			int counts = hBag.get(word);
			HashMap<String, Integer> hWordBag = new HashMap<String, Integer>();
			try {
				hWordBag.put(word, counts);
					for (LexicalRule<? extends RuleInfo> rule : lex.getRulesForLeft(word, null)) {
						hWordBag.put(rule.getRLemma(), counts);
					}
			}
			catch (LexicalResourceException e)
			{
				throw new ScoringComponentException(e.getMessage());
			}
			score += Math.min(counts, calculateSimilarity(tBag, hWordBag).get(0) * hWordBag.size());
		}
		
		return score;
	}
	
	protected double calculateSingleLexScoreWithGermaNetRelation(HashMap<String, Integer> tBag, HashMap<String, Integer> hBag, GermaNetRelation gnr, boolean both, boolean forLeft) throws ScoringComponentException{
		if (null == gnw) {
			throw new ScoringComponentException("WARNING: the specified lexical resource has not been properly initialized!");
		}
		
		double score = 0.0d;
		
		for (String word : hBag.keySet()) {
			int counts = hBag.get(word);
			HashMap<String, Integer> hWordBag = new HashMap<String, Integer>();
			try {
				hWordBag.put(word, counts);
				if (both) {
					for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForLeft(word, null, gnr)) {
						hWordBag.put(rule.getRLemma(), counts);
					}
//					for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForRight(word, null, gnr)) {
//						hWordBag.put(rule.getRLemma(), counts);
//					}
				} else {
					if (forLeft) {
						for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForLeft(word, null, gnr)) {
							hWordBag.put(rule.getRLemma(), counts);
						}	
					} else {
//						for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForRight(word, null, gnr)) {
//							hWordBag.put(rule.getRLemma(), counts);
//						}
					}
				}
			}
			catch (LexicalResourceException e)
			{
				throw new ScoringComponentException(e.getMessage());
			}
			score += Math.min(counts, calculateSimilarity(tBag, hWordBag).get(0) * hWordBag.size());
		}
		
		return score;
	}
}
