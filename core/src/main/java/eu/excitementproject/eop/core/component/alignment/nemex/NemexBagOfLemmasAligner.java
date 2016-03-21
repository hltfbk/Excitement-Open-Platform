package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.dfki.lt.nemex.a.NEMEX_A;
import de.dfki.lt.nemex.a.data.GazetteerNotLoadedException;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.nemex.NemexType;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

/**
 * <code>NemexBagOfLemmasAligner</code> approximately aligns word lemmas in T
 * and H.
 * 
 * Based on <code>NemexA</code> tool developed by DFKI.
 * 
 * NemexType annotations are created on T and H, and a link is added between
 * them to represent alignment.
 * 
 * @author Madhumita
 * 
 */
public class NemexBagOfLemmasAligner extends NemexAligner {

	/**
	 * Constructor, indicates configuration settings for NemexBagOfLemmas
	 * alignment using NemexA. Loads external dictionaries if required.
	 * 
	 * @param comp
	 *            NameValue table with configuration parameters for
	 *            <code>NemexBagOfLemmasAligner</code>.
	 * @param removeStopWords
	 *            remove stop words while processing data or not.
	 * @param stopWords
	 *            set of stop-words in English
	 * @throws ConfigurationException
	 */
	public NemexBagOfLemmasAligner(CommonConfig config,
			boolean removeStopWords, Set<String> stopWords)
			throws ConfigurationException {

		super(config, "NemexBagOfLemmasScoring");

		NameValueTable comp = config.getSection("NemexBagOfLemmasScoring");
		this.removeStopWords = removeStopWords;
		this.stopWords = stopWords;

		// Load WordNet if required
		this.isWN = Boolean.valueOf(comp.getString("isWN"));

		if (this.isWN) {

			String wnPath = comp.getString("wnPath");
			String WNRelations = comp.getString("WNRelations");
			boolean isWNCollapsed = Boolean.valueOf(comp
					.getString("isWNCollapsed"));
			boolean useFirstSenseOnlyLeft = Boolean.valueOf(comp
					.getString("useFirstSenseOnlyLeft"));
			boolean useFirstSenseOnlyRight = Boolean.valueOf(comp
					.getString("useFirstSenseOnlyRight"));

			this.wnlr = loadWordnet(WNRelations, wnPath, isWNCollapsed,
					useFirstSenseOnlyLeft, useFirstSenseOnlyRight);

		}
	}

	protected List<WordnetLexicalResource> loadWordnet(String WNRel,
			String wnPath, boolean isWNCollapsed,
			boolean useFirstSenseOnlyLeft, boolean useFirstSenseOnlyRight) {
		try {
			logger.info("Loading wordnet");
			String[] WNRelations = WNRel.split(",");
			if (null == WNRelations || 0 == WNRelations.length) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find any relations for the WordNet");
			}
			Set<WordNetRelation> wnRelSet = new HashSet<WordNetRelation>();
			for (String relation : WNRelations) {
				if (relation.equalsIgnoreCase("HYPERNYM")) {
					wnRelSet.add(WordNetRelation.HYPERNYM);
				} else if (relation.equalsIgnoreCase("SYNONYM")) {
					wnRelSet.add(WordNetRelation.SYNONYM);
				} else if (relation.equalsIgnoreCase("PART_HOLONYM")) {
					wnRelSet.add(WordNetRelation.PART_HOLONYM);
				} else {
					logger.info("Warning: wrong relation names for the WordNet");
				}
			}
			if (wnRelSet.isEmpty()) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find any (correct) relations for the WordNet");
			}

			File wnFile = new File(wnPath);
			if (!wnFile.exists()) {
				throw new ConfigurationException("cannot find WordNet at: "
						+ wnPath);
			}
			if (isWNCollapsed) {
				List<WordnetLexicalResource> wnlrList = new ArrayList<WordnetLexicalResource>();
				WordnetLexicalResource wnlr = new WordnetLexicalResource(
						wnFile, useFirstSenseOnlyLeft, useFirstSenseOnlyRight,
						wnRelSet);
				logger.info("Load WordNet done.");
				wnlrList.add(wnlr);
				return wnlrList;

			} else {
				List<WordnetLexicalResource> wnlrList = new ArrayList<WordnetLexicalResource>();
				for (WordNetRelation wnr : wnRelSet) {
					WordnetLexicalResource wnlr = new WordnetLexicalResource(
							wnFile, useFirstSenseOnlyLeft,
							useFirstSenseOnlyRight, Collections.singleton(wnr));
					wnlrList.add(wnlr);

				}
				logger.info("Load WordNet done.");
				return wnlrList;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	
	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {

		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null) {
			logger.info("Null JCas object");
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");
		}

		JCas textView = readCas(aJCas, "text");
		JCas hypoView = readCas(aJCas, "hypothesis");

		if (textView != null && hypoView != null) {
			if (direction == null) {
				logger.info("Direction not specified. Setting direction as TtoH by default");
				this.direction = "TtoH";
			}

			/*
			 * contains queryID and queryText for all unique queries in T or H,
			 * depending on direction
			 */
			HashMap<Integer, String> queryMap = new HashMap<Integer, String>();

			// queryText with JCas ID and offsets for queryText
			HashMap<String, ArrayList<EntryInfo>> queryInvIndex = new HashMap<String, ArrayList<EntryInfo>>();

			/*
			 * Direction TtoH: dictionary creation from T, lookup from H terms
			 * Direction HtoT: vice-versa
			 */
			if (direction.equalsIgnoreCase("TtoH")) {
				createDictionary(textView, queryMap, queryInvIndex);
				annotateSubstring(hypoView, queryMap, queryInvIndex);
			} else {
				createDictionary(hypoView, queryMap, queryInvIndex);
				annotateSubstring(textView, queryMap, queryInvIndex);
			}
		}
	}

	/**
	 * Add NemexType annotation to all words in view. Creates Nemex gazetteer
	 * from all generated entries (words).
	 * 
	 * @param view
	 *            T/H JCas object to create Nemex gazetteer from.
	 * @param entryMap
	 *            Map to store all words in view as gazetteer entries.
	 * @param entryInvIndex
	 *            Inverted index of all entry words and corresponding views and
	 *            offsets generating the entry.
	 */
	public void createDictionary(JCas view, HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex) {
		try {

			// get all tokens in given view
			AnnotationIndex<Annotation> tokenAnnots = view
					.getAnnotationIndex(Token.type);

			double totalNumOfGazetteerEntries = 0;
			int index = 0; // id of Entry in Entry map

			// Iterate over all tokens
			for (Iterator<Annotation> tIter = tokenAnnots.iterator(); tIter
					.hasNext();) {

				Token curToken = (Token) tIter.next();
				String curTokenText = curToken.getCoveredText().toLowerCase();
				String curLemma = curToken.getLemma().getValue();
				String curPOS = curToken.getPos().getPosValue();
				int curStartOffset = curToken.getBegin();
				int curEndOffset = curToken.getEnd();

				if (removeStopWords) {
					if (stopWords.contains(curTokenText.toLowerCase())) {
						continue; // skip the token if it is a stop word
					}
				}

				logger.info("Adding NemexType annotation on entry");
				List<String> nemexAnnotVals = Arrays.asList(curLemma);
				addNemexAnnotation(view, nemexAnnotVals, curStartOffset,
						curEndOffset);
				logger.info("Finished adding NemexType annotation on entry");

				ArrayList<EntryInfo> offsets = new ArrayList<EntryInfo>();

				// Add all the entries (words) to entryMap and entryInvIndex
				EntryInfo curOffset = new EntryInfo(view, curStartOffset,
						curEndOffset, curPOS, false);

				if (entryMap.containsValue(curLemma)) {
					offsets = entryInvIndex.get(curLemma);
				} else {
					index++;
					entryMap.put(index, curLemma);
				}

				totalNumOfGazetteerEntries++;
				offsets.add(curOffset);

				entryInvIndex.put(curLemma, offsets);

				if (isWN) {
					addWnRelations(curPOS, entryMap, entryInvIndex);
					BySimplerCanonicalPartOfSpeech curTag = null;

					if (curPOS.startsWith("NN")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.NOUN);
					} else if (curPOS.startsWith("VB")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.VERB);
					} else if (curPOS.startsWith("RB")
							|| curPOS.equalsIgnoreCase("WRB")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.ADVERB);
					} else if (curPOS.startsWith("JJ")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.ADJECTIVE);
					} else if (curPOS.startsWith("DT") || curPOS.equals("WDT")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.DETERMINER);
					} else if (curPOS.startsWith("PR")
							|| curPOS.startsWith("WP")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.PRONOUN);
					} else if (curPOS.startsWith("IN")) {
						curTag = new BySimplerCanonicalPartOfSpeech(
								SimplerCanonicalPosTag.PREPOSITION);
					}

					for (Iterator<WordnetLexicalResource> wnIter = wnlr
							.iterator(); wnIter.hasNext();) {
						for (LexicalRule<? extends RuleInfo> rule : wnIter
								.next().getRulesForLeft(curLemma, curTag)) {

							String curEntry = rule.getRLemma().toLowerCase()
									.replace(" ", this.delimiterAlignLookup);
							curOffset = new EntryInfo(view, curStartOffset,
									curEndOffset, curPOS, true);
							if (entryMap.containsValue(curEntry)) {
								offsets = entryInvIndex.get(curEntry);
							} else {
								index++;
								entryMap.put(index, curEntry);
							}

							totalNumOfGazetteerEntries++;
							offsets.add(curOffset);

							entryInvIndex.put(curEntry, offsets);

						}

					}
				}
			}

			addEntryToDict(this.gazetteerAlignLookup, entryMap, entryInvIndex,
					totalNumOfGazetteerEntries);

		} catch (Exception e) {
			logger.info("Error updating the Gazetteer file");
			e.printStackTrace();
		}
	}

	private void addWnRelations(String curPOS,
			HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex) {
		// TODO Auto-generated method stub

	}

	/**
	 * (Direction TtoH) Annotates words in H as NemexType based on matching
	 * words in T, and adds alignment link between corresponding annotations.
	 * Vice versa for direction HtoT.
	 * 
	 * @param queryView
	 *            view to generate query words from.
	 * @param entryMap
	 *            map of entry ID and words from the other view.
	 * @param entryInvIndex
	 *            Index of entry words and corresponding views and offsets
	 *            generating the entry.
	 */
	private void annotateSubstring(JCas queryView,
			HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex) {
		List<String> values = new ArrayList<String>();
		Collection<Token> tokenAnnots = JCasUtil.select(queryView, Token.class);

		logger.info("Loading the gazetteer");

		try {
			NEMEX_A.loadNewGazetteer(this.gazetteerAlignLookup,
					this.delimiterAlignLookup,
					this.delimiterSwitchOffAlignLookup,
					this.nGramSizeAlignLookup,
					this.ignoreDuplicateNGramsAlignLookup);
		} catch (Exception e) {
			logger.error("Could not load the gazetteer");
		}

		// Iterate over all tokens in given view and use token text as queries.
		for (Iterator<Token> iter = tokenAnnots.iterator(); iter.hasNext();) {

			Token token = (Token) iter.next();

			String queryText = token.getLemma().getValue().toLowerCase();
			int queryStartOff = token.getBegin();
			int queryEndOff = token.getEnd();

			try {

				// Find all similar entries to current word
				values = NEMEX_A.checkSimilarity(queryText,
						this.gazetteerAlignLookup, this.simMeasureAlignLookup,
						this.simThresholdAlignLookup);

				/*
				 * Add NemexType annotation if matching entries found. Add
				 * alignment link between query and retrieved entries.
				 */
				if (values.size() > 0) {
					logger.info("Query text: " + queryText);
					logger.info("Similar entry: " + values);
					NemexType queryAnnot = addNemexAnnotation(queryView,
							values, queryStartOff, queryEndOff);

					addAlignmentLink(queryAnnot, queryView, queryStartOff,
							queryEndOff, entryMap, entryInvIndex,
							this.direction, this.simThresholdAlignLookup);
				}
			} catch (GazetteerNotLoadedException e) {
				logger.error("Gazetteer is not loaded");
				e.printStackTrace();
			}

		}

		logger.info("Unloading the gazetteer");
		NEMEX_A.unloadGazetteer(gazetteerAlignLookup);

	}

	/**
	 * @return Name for current component.
	 */

	@Override
	public String getComponentName() {
		return "NemexBagOfLemmasAligner";
	}

	/**
	 * @return Name for current instance.
	 */
	@Override
	public String getInstanceName() {
		return null;
	}

	// private double[] similarityThresholdExtLookup;
	// private String[] similarityMeasureExtLookup;

	// private String gazetteerAlignLookup;
	// private double simThresholdAlignLookup;
	// private String simMeasureAlignLookup;
	// private String delimiterAlignLookup;
	// private boolean delimiterSwitchOffAlignLookup;
	// private int nGramSizeAlignLookup;
	// private boolean ignoreDuplicateNGramsAlignLookup;
	//
	// private String direction;

	private Set<String> stopWords;
	private boolean removeStopWords;

	private boolean isWN;
	private List<WordnetLexicalResource> wnlr;

	private final static Logger logger = Logger
			.getLogger(NemexBagOfLemmasAligner.class.getName());

}
