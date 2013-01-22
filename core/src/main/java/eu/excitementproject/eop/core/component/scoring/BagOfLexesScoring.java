package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;

/**
 * The <code>BagOfLexesScoring</code> class extends <code>BagOfLemmasScoring</code>.
 * It supports (currently) <code>GermanDistSim</code> and <code>GermaNetWrapper</code> two lexical resources.
 * 
 * @author  Rui
 */
public class BagOfLexesScoring extends BagOfLemmasScoring {
	
	static Logger logger = Logger.getLogger(BagOfLexesScoring.class.getName());
	
	private static final String GDS_PATH = "./src/main/resources/dewakdistributional-data/";
	private static final String GNW_PATH = "./src/main/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/";
	
//	the number of features
	protected int numOfFeats = 0;
	
	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}
	
	protected boolean[] moduleFlags = new boolean[5];
	
	private GermanDistSim gds = null;
	
	private GermaNetWrapper gnw = null;

	public BagOfLexesScoring(boolean useGDS, boolean useGNWCau, boolean useGNWEnt, boolean useGNWHyn, boolean useGNWSyn) {
		if (useGDS) {
			try {
				gds = new GermanDistSim(GDS_PATH);
				numOfFeats ++;
				moduleFlags[0] = true;
			}
			catch (GermanDistSimNotInstalledException e) {
				logger.warning("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
				//throw e;
			}
			catch (BaseException e)
			{
				logger.info(e.getMessage());
			}
		} else {
			moduleFlags[0] = false;
		}
		if (useGNWCau || useGNWEnt || useGNWHyn || useGNWSyn) {
			try {
				gnw = new GermaNetWrapper(GNW_PATH);
				if (useGNWCau) {
					numOfFeats ++;
					moduleFlags[1] = true;
				} else {
					moduleFlags[1] = false;
				}
				if (useGNWEnt) {
					numOfFeats ++;
					moduleFlags[2] = true;
				} else {
					moduleFlags[2] = false;
				}
				if (useGNWHyn) {
					numOfFeats ++;
					moduleFlags[3] = true;
				} else {
					moduleFlags[3] = false;
				}
				if (useGNWSyn) {
					numOfFeats ++;
					moduleFlags[4] = true;
				} else {
					moduleFlags[3] = false;
				}
			}
			catch (GermaNetNotInstalledException e) {
				logger.warning("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
				//throw e;
			}
			catch (BaseException e) {
				logger.info(e.getMessage());
			}
		} else {
			moduleFlags[1] = false;
			moduleFlags[2] = false;
			moduleFlags[3] = false;
			moduleFlags[4] = false;
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
			
//			int hSize = 0;
//			for (String hWord : hBag.keySet()) {
//				hSize += hBag.get(hWord).intValue();
//			}
			
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
	
	protected double calculateSingleLexScore(HashMap<String, Integer> tBag, HashMap<String, Integer> hBag, LexicalResource<? extends RuleInfo> lex) throws ScoringComponentException{
		if (null == lex) {
			throw new ScoringComponentException("WARNING: the specified lexical resource has not been properly initialized!");
		}
		
		double score = 0.0d;
		HashMap<String, Integer> tWordBag = new HashMap<String, Integer>();
		
		for (String word : tBag.keySet()) {
			int counts = tBag.get(word);
			try {
				tWordBag.put(word, counts);
				for (LexicalRule<? extends RuleInfo> rule : lex.getRulesForLeft(word, null)) {
					if (tWordBag.containsKey(rule.getRLemma())) {
						int tmp = tWordBag.get(rule.getRLemma());
						tWordBag.put(rule.getRLemma(), tmp + counts);
					} else {
						tWordBag.put(rule.getRLemma(), counts);
					}
				}
			} catch (LexicalResourceException e) {
				throw new ScoringComponentException(e.getMessage());
			}
		}
		
		score = calculateSimilarity(tWordBag, hBag).get(0);
		
		return score;
		
//		for (String word : hBag.keySet()) {
//			int counts = hBag.get(word);
//			HashMap<String, Integer> hWordBag = new HashMap<String, Integer>();
//			try {
//				hWordBag.put(word, counts);
//					for (LexicalRule<? extends RuleInfo> rule : lex.getRulesForLeft(word, null)) {
//						hWordBag.put(rule.getRLemma(), counts);
//					}
//			}
//			catch (LexicalResourceException e)
//			{
//				throw new ScoringComponentException(e.getMessage());
//			}
//			score += Math.min(counts, calculateSimilarity(tBag, hWordBag).get(0) * hWordBag.size());
//		}
//		
//		return score;
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
				for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForLeft(word, null, gnr)) {
					if (tWordBag.containsKey(rule.getRLemma())) {
						int tmp = tWordBag.get(rule.getRLemma());
						tWordBag.put(rule.getRLemma(), tmp + counts);
					} else {
						tWordBag.put(rule.getRLemma(), counts);
					}
				}
			} catch (LexicalResourceException e) {
				throw new ScoringComponentException(e.getMessage());
			}
		}
		
		score = calculateSimilarity(tWordBag, hBag).get(0);
		
		return score;
		
//		for (String word : hBag.keySet()) {
//			int counts = hBag.get(word);
//			HashMap<String, Integer> hWordBag = new HashMap<String, Integer>();
//			try {
//				hWordBag.put(word, counts);
//				if (both) {
//					for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForLeft(word, null, gnr)) {
//						hWordBag.put(rule.getRLemma(), counts);
//					}
////					for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForRight(word, null, gnr)) {
////						hWordBag.put(rule.getRLemma(), counts);
////					}
//				} else {
//					if (forLeft) {
//						for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForLeft(word, null, gnr)) {
//							hWordBag.put(rule.getRLemma(), counts);
//						}	
//					} else {
////						for (LexicalRule<? extends RuleInfo> rule : gnw.getRulesForRight(word, null, gnr)) {
////							hWordBag.put(rule.getRLemma(), counts);
////						}
//					}
//				}
//			}
//			catch (LexicalResourceException e)
//			{
//				throw new ScoringComponentException(e.getMessage());
//			}
//			score += Math.min(counts, calculateSimilarity(tBag, hWordBag).get(0) * hWordBag.size());
//		}
//		
//		return score;
	}
}
