package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.core.component.lexicalknowledge.transDm.GermanTransDmException;
import eu.excitementproject.eop.core.component.lexicalknowledge.transDm.GermanTransDmResource;

/**
 * The <code>BagOfLexesScoringDE</code> class extends
 * <code>BagOfLemmasScoring</code>. It supports (currently)
 * <code>GermanDistSim</code>, <code>GermaNetWrapper</code> 
 * and <code>GermanTransDmResource<code>, three lexical resources.
 * 
 * @author Rui Wang, Julia Kreutzer
 * @since November 2012
 */
public class BagOfLexesScoringDE extends BagOfLemmasScoring {

	static Logger logger = Logger.getLogger(BagOfLexesScoringDE.class.getName());

	/**
	 * the number of features
	 */
	private int numOfFeats = 0;

	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}

	protected boolean[] moduleFlags = new boolean[3];

	protected GermanDistSim gds = null;

	protected GermaNetWrapper gnw = null;
	
	protected GermanTransDmResource gtdm = null;

	/**
	 * the constructor using parameters
	 * 
	 * @param isDS whether to use <code>GermanDistSim</code>
	 * @param isTDm whether to use <code>GermanTransDmResource</code>
	 * @param simMeasure the similarity measure used for GermanTransDm, either "cosine", "balapinc" or "all"
	 * @param isGN whether to use <code>GermaNetWrapper</code>
	 * @param germaNetRelations the array of GermaNet relations
	 * @param germaNetFilePath the file path to GermaNet
	 * @param isDB whether to use <code>DerivBaseResource</code>
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	public BagOfLexesScoringDE(boolean isDS, boolean isTDm, String simMeasure, boolean isGN, String[] germaNetRelations, String germaNetFilePath, boolean isDB) throws ConfigurationException, LexicalResourceException{
		for (int i = 0; i < moduleFlags.length; i++) {
			moduleFlags[i] = false;
		}
		
		if (!isDS && !isGN && !isDB && !isTDm) {
			throw new ConfigurationException(
					"Wrong configuation: didn't find any lexical resources for the BagOfLexesScoringDE component");
		}
		
		// initialize GermanDistSim
		if (isDS) {
			try {
				gds = new GermanDistSim();
				numOfFeats++;
				moduleFlags[0] = true;
			} catch (GermanDistSimNotInstalledException e) {
				logger.warning("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
			logger.info("Load GermanDistSim done.");
		}
		
		// initialize GermanTransDmResource
		if (isTDm) {
			try {
				gtdm = new GermanTransDmResource(simMeasure);
				numOfFeats++;
				moduleFlags[2] = true;
			} catch (GermanTransDmException e) {
				logger.warning("WARNING: GermanTransDmResource could not be loaded.");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
			logger.info("Load GermanTransDmResource  done.");
		}

		// initialize GermaNet
		if (isGN) {
			if (null == germaNetRelations || 0 == germaNetRelations.length) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find any relations for the GermaNet");
			}
			try {
				gnw = new GermaNetWrapper(germaNetFilePath);
				numOfFeats++;
				moduleFlags[1] = true;
			} catch (GermaNetNotInstalledException e) {
				logger.warning("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
			logger.info("Load GermaNet done.");
		}
	}
	
	/**
	 * the constructor using the configuration
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	public BagOfLexesScoringDE(CommonConfig config)
			throws ConfigurationException, LexicalResourceException {
		for (int i = 0; i < moduleFlags.length; i++) {
			moduleFlags[i] = false;
		}

		NameValueTable comp = config.getSection("BagOfLexesScoring");

		if (null == comp.getString("GermanDistSim")
				&& null == comp.getString("GermaNetWrapper")
				&& null == comp.getString("GermanTransDmResource")
				&& null == comp.getString("DerivBaseResource")) {
			throw new ConfigurationException(
					"Wrong configuation: didn't find any lexical resources for the BagOfLexesScoring component");
		}
		
		// initialize GermanDistSim
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
			logger.info("Load GermanDistSim done.");
		}
		
		//initialize GermanTransDmResource
		if (null!= comp.getString("GermanTransDmResource")){
			try {
				gtdm = new GermanTransDmResource(config); //load gtdm with config
				numOfFeats++; 
				moduleFlags[2] = true;
			} catch (GermanTransDmException e) {
				logger.warning("GermanTransDmResource could not be loaded.");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e){
				throw new LexicalResourceException(e.getMessage());
			}
			logger.info("Load GermanTransDmResource done.");
		}

		// initialize GermaNet
		if (null != comp.getString("GermaNetWrapper")) {
			String[] GermaNetRelations = comp.getString("GermaNetWrapper")
					.split(",");
			if (null == GermaNetRelations || 0 == GermaNetRelations.length) {
				throw new ConfigurationException(
						"Wrong configuration: didn't find any relations for the GermaNet");
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
			logger.info("Load GermaNet done.");
		}
	}
	
	@Override
	public String getComponentName() {
		return "BagOfLexesScoring";
	}

	/**
	 * close the component by closing the lexical resources.
	 */
	@Override
	public void close() throws ScoringComponentException {
		try {
			if (null != gds) {
				gds.close();
			}
			if (null != gnw) {
				gnw.close();
			}
			if (null != gtdm) {
				gtdm.close();
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

			if (moduleFlags[0]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gds));
			}
			if (moduleFlags[1]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gnw)); 
			}
			if (moduleFlags[2]) {
				scoresVector.add(calculateSingleLexScore(tBag,hBag, gtdm)); 
			}
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	/**
	 * calculate the similarity score between T and H based on one lexical
	 * resource
	 * 
	 * @param tBag
	 *            the bag of words of T
	 * @param hBag
	 *            the bag of words of H
	 * @param lex
	 *            the lexical resource used
	 * @return the similarity score
	 * @throws ScoringComponentException
	 */
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

		for (final Iterator<Entry<String, Integer>> iter = tBag.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Integer> entry = iter.next();
			final String word = entry.getKey();
			final int counts = entry.getValue().intValue();
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

	/**
	 * calculate the similarity score between T and H based on GermaNet
	 * relations
	 * 
	 * @param tBag
	 *            the bag of words of T
	 * @param hBag
	 *            the bag of words of H
	 * @param gnr
	 *            the GermaNet
	 * @return the similarity score
	 * @throws ScoringComponentException
	 */
	protected double calculateSingleLexScoreWithGermaNetRelation(
			HashMap<String, Integer> tBag, HashMap<String, Integer> hBag,
			GermaNetRelation gnr) throws ScoringComponentException {
		if (null == gnw) {
			throw new ScoringComponentException(
					"WARNING: the specified lexical resource has not been properly initialized!");
		}

		double score = 0.0d;
		HashMap<String, Integer> tWordBag = new HashMap<String, Integer>();

		for (final Iterator<Entry<String, Integer>> iter = tBag.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Integer> entry = iter.next();
			final String word = entry.getKey();
			final int counts = entry.getValue().intValue();
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
