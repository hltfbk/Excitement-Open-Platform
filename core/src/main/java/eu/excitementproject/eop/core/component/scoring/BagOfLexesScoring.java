package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;

/**
 * The <code>BagOfLexesScoring</code> class extends
 * <code>BagOfLemmasScoring</code>. It supports (currently)
 * <code>GermanDistSim</code> and <code>GermaNetWrapper</code> two lexical
 * resources.
 * 
 * @author Rui
 */
public class BagOfLexesScoring extends BagOfLemmasScoring {

	static Logger logger = Logger.getLogger(BagOfLexesScoring.class.getName());

	// the number of features
	protected int numOfFeats = 0;

	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}

	protected boolean[] moduleFlags = new boolean[2];

	protected GermanDistSim gds = null;

	protected GermaNetWrapper gnw = null;

	public BagOfLexesScoring(CommonConfig config)
			throws ConfigurationException, LexicalResourceException {
		for (int i = 0; i < moduleFlags.length; i++) {
			moduleFlags[i] = false;
		}
		
		NameValueTable comp = config.getSection("BagOfLexesScoring");
		
		// initialize GermanDistSim
		if (null == comp.getString("GermanDistSim")
				&& null == comp.getString("GermaNetWrapper")) {
			throw new ConfigurationException(
					"Wrong configuation: didn't find any lexical resources for the BagOfLexesScoring component");
		}
		if (null != comp.getString("GermanDistSim")) {
			try {
				gds = new GermanDistSim(config);
				numOfFeats++;
				moduleFlags[0] = true;
			} catch (GermanDistSimNotInstalledException e) {
				logger.warning("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
		}
		
		// initialize GermaNet
		if (null != comp.getString("GermaNetWrapper")) {
			String[] GermaNetRelations = comp.getString("GermaNetWrapper").split(",");
			if (null == GermaNetRelations || 0 == GermaNetRelations.length) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find any relations for the GermaNet");
			}
			try {
				gnw = new GermaNetWrapper(config);
				numOfFeats++;
				moduleFlags[1] = true;
			} catch (GermaNetNotInstalledException e) {
				logger.warning("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
		}
	}

	@Override
	public String getComponentName() {
		return "BagOfLexesScoring";
	}
	
	public void close() throws ScoringComponentException {
		try {
			if (null != gds) {
				gds.close();
			}
			if (null != gnw) {
				gnw.close();
			}
		} catch (LexicalResourceCloseException e) {
			throw new ScoringComponentException(e.getMessage());
		}
	}

	@Override
	public Vector<Double> calculateScores(JCas aCas)
			throws ScoringComponentException {
		// 1) how many words of H (extended with multiple relations) can be
		// found in T divided by the length of H
		Vector<Double> scoresVector = new Vector<Double>();

		try {
			JCas tView = aCas.getView("TextView");
			HashMap<String, Integer> tBag = countTokens(tView);

			JCas hView = aCas.getView("HypothesisView");
			HashMap<String, Integer> hBag = countTokens(hView);

			// int hSize = 0;
			// for (String hWord : hBag.keySet()) {
			// hSize += hBag.get(hWord).intValue();
			// }

			if (moduleFlags[0]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gds));
			}
			if (moduleFlags[1]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gnw));
			}
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	protected double calculateSingleLexScore(HashMap<String, Integer> tBag,
			HashMap<String, Integer> hBag,
			LexicalResource<? extends RuleInfo> lex)
			throws ScoringComponentException {
		if (null == lex) {
			throw new ScoringComponentException(
					"WARNING: the specified lexical resource has not been properly initialized!");
		}

		double score = 0.0d;
		HashMap<String, Integer> tWordBag = new HashMap<String, Integer>();

		for (String word : tBag.keySet()) {
			int counts = tBag.get(word);
			try {
				tWordBag.put(word, counts);
				for (LexicalRule<? extends RuleInfo> rule : lex
						.getRulesForLeft(word, null)) {
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
	}

	protected double calculateSingleLexScoreWithGermaNetRelation(
			HashMap<String, Integer> tBag, HashMap<String, Integer> hBag,
			GermaNetRelation gnr) throws ScoringComponentException {
		if (null == gnw) {
			throw new ScoringComponentException(
					"WARNING: the specified lexical resource has not been properly initialized!");
		}

		double score = 0.0d;
		HashMap<String, Integer> tWordBag = new HashMap<String, Integer>();

		for (String word : tBag.keySet()) {
			int counts = tBag.get(word);
			try {
				tWordBag.put(word, counts);
				/*
				 * Britta: AFAIK, GermaNet doesn't accept "hitze" as noun, since
				 * it expects capitalisation for nouns ("Hitze"); it would
				 * return an empty result. Thus, ambiguity of verbs and nouns is
				 * not resolved with "null" POS. However, this idea works for
				 * the ambiguity of adjectives and past participle verbs:
				 * (gedeckt, V_pastPart) would not give a hit in GermaNet, but
				 * (gedeckt, null) returns values for (gedeckt, ADJ).
				 * 
				 * Rui: In the future, we may think about normalizing the word
				 * by toLowerCase(), to tackle the first case.
				 */
				for (LexicalRule<? extends RuleInfo> rule : gnw
						.getRulesForLeft(word, null, gnr)) {
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
	}
}
