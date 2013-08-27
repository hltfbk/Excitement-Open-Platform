package eu.excitementproject.eop.core.component.scoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
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
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.derivbase.DerivBaseNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.derivbase.DerivBaseResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;

/**
 * The class <code>BagOfLexesPosScoring</code> extends
 * <code>BagOfLexesScoring</code>.
 * 
 * It adds POS tags into the queries to the lexical resources.
 * 
 * @author Rui Wang
 * @since January 2013
 */
public class BagOfLexesPosScoringDE extends BagOfLexesScoringDE {
	
	static Logger logger = Logger.getLogger(BagOfLexesPosScoringDE.class.getName());
	
	/**
	 * the number of features
	 */
	private int numOfFeats = 0;

	@Override
	public int getNumOfFeats() {
		return numOfFeats;
	}
	
	protected boolean[] moduleFlags = new boolean[1];
	
	protected DerivBaseResource dbr = null;

	/**
	 * the constructor using parameters
	 * 
	 * @param isDS whether to use <code>GermanDistSim</code>
	 * @param isGN whether to use <code>GermaNetWrapper</code>
	 * @param germaNetRelations the array of GermaNet relations
	 * @param germaNetFilePath the file path to GermaNet
	 * @param isDB whether to use <code>DerivBaseResource</code>
	 * @param useScores cf. <code>DerivBaseResource</code>
	 * @param derivSteps cf. <code>DerivBaseResource</code>
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	public BagOfLexesPosScoringDE(boolean isDS, boolean isGN, String[] germaNetRelations, String germaNetFilePath, boolean isDB, boolean useScores, Integer derivSteps) throws ConfigurationException, LexicalResourceException{
		super(isDS, isGN, germaNetRelations, germaNetFilePath, isDB);
		numOfFeats = super.getNumOfFeats();
		
		for (int i = 0; i < moduleFlags.length; i++) {
			moduleFlags[i] = false;
		}
		
		// initialize DerivBaseResource
		if (isDB) {
			try {
				dbr = new DerivBaseResource(useScores, derivSteps);
				numOfFeats++;
				moduleFlags[0] = true;
			} catch (DerivBaseNotInstalledException e) {
				logger.warning("WARNING: DErivBase file was not found in the given path.");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
			logger.info("Load DerivBaseResource done.");
		}
	}
	
	/**
	 * the constructor
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	public BagOfLexesPosScoringDE(CommonConfig config)
			throws ConfigurationException, LexicalResourceException {
		super(config);
		numOfFeats = super.getNumOfFeats();
		
		for (int i = 0; i < moduleFlags.length; i++) {
			moduleFlags[i] = false;
		}
		
		NameValueTable comp = config.getSection("BagOfLexesScoring");
		
		// initialize DerivBaseResource
		if (null != comp.getString("DerivBaseResource")) {
			try {
				dbr = new DerivBaseResource(config);
				numOfFeats++;
				moduleFlags[0] = true;
			} catch (DerivBaseNotInstalledException e) {
				logger.warning("WARNING: DErivBase file was not found in the given path.");
				throw new LexicalResourceException(e.getMessage());
			} catch (BaseException e) {
				throw new LexicalResourceException(e.getMessage());
			}
			logger.info("Load DerivBaseResource done.");
		}
	}

	@Override
	public String getComponentName() {
		return "BagOfLexesPosScoring";
	}

	/**
	 * close the component by closing the lexical resources.
	 */
	@Override
	public void close() throws ScoringComponentException {
		try {
			super.close();
			if (null != dbr) {
				dbr.close();
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
			HashMap<String, Integer> tBag = countTokenPoses(tView);

			JCas hView = aCas.getView("HypothesisView");
			HashMap<String, Integer> hBag = countTokenPoses(hView);

			if (super.moduleFlags[0]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gds));
			}
			if (super.moduleFlags[1]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, gnw));
			}
			if (moduleFlags[0]) {
				scoresVector.add(calculateSingleLexScore(tBag, hBag, dbr));
			}
		} catch (CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;
	}

	/**
	 * Count the lemmas and POSes contained in a text and store the counts in a
	 * HashMap
	 * 
	 * @param text
	 *            the input text represented in a JCas
	 * @return a HashMap represents the bag of lemmas and POSes contained in the
	 *         text, in the form of <Lemma ### POS, Frequency>
	 */
	protected HashMap<String, Integer> countTokenPoses(JCas text) {
		HashMap<String, Integer> tokenNumMap = new HashMap<String, Integer>();
		Iterator<Annotation> tokenIter = text.getAnnotationIndex(Token.type)
				.iterator();
		while (tokenIter.hasNext()) {
			Token curr = (Token) tokenIter.next();
			String tokenText = curr.getLemma().getValue() + " ### "
					+ curr.getPos().getPosValue();
			Integer num = tokenNumMap.get(tokenText);
			if (null == num) {
				tokenNumMap.put(tokenText, 1);
			} else {
				tokenNumMap.put(tokenText, num + 1);
			}
		}
		return tokenNumMap;
	}

	@Override
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
				String POS = word.split(" ### ")[1];
				for (LexicalRule<? extends RuleInfo> rule : lex
						.getRulesForLeft(word.split(" ### ")[0],
								new GermanPartOfSpeech(POS))) {
					String tokenText = rule.getRLemma() + " ### " + POS;
					if (tWordBag.containsKey(tokenText)) {
						int tmp = tWordBag.get(tokenText);
						tWordBag.put(tokenText, tmp + counts);
					} else {
						tWordBag.put(tokenText, counts);
					}
				}
			} catch (LexicalResourceException e) {
				throw new ScoringComponentException(e.getMessage());
			} catch (UnsupportedPosTagStringException e) {
				throw new ScoringComponentException(e.getMessage());
			}
		}

		score = calculateSimilarity(tWordBag, hBag).get(0);

		return score;
	}

	@Override
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
				String POS = word.split(" ### ")[1];
				for (LexicalRule<? extends RuleInfo> rule : gnw
						.getRulesForLeft(word.split(" ### ")[0],
								new GermanPartOfSpeech(POS), gnr)) {
					String tokenText = rule.getRLemma() + " ### " + POS;
					if (tWordBag.containsKey(tokenText)) {
						int tmp = tWordBag.get(tokenText);
						tWordBag.put(tokenText, tmp + counts);
					} else {
						tWordBag.put(tokenText, counts);
					}
				}
			} catch (LexicalResourceException e) {
				throw new ScoringComponentException(e.getMessage());
			} catch (UnsupportedPosTagStringException e) {
				throw new ScoringComponentException(e.getMessage());
			}
		}

		score = calculateSimilarity(tWordBag, hBag).get(0);

		return score;
	}

}
