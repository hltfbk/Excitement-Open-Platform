package eu.excitementproject.eop.core.component.alignment.nemex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.fit.util.JCasUtil;

import de.dfki.lt.nemex.a.NEMEX_A;
import de.dfki.lt.nemex.a.data.GazetteerNotLoadedException;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.nemex.NemexType;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * <code>NemexBagOfWordsAligner</code> approximately aligns words in T and H.
 * 
 * Based on <code>NemexA</code> tool developed by DFKI.
 * 
 * NemexType annotations are created on T and H, and a link is added between
 * them to represent alignment.
 * 
 * @author Madhumita
 * 
 */
public class NemexBagOfWordsAligner extends NemexAligner {

	/**
	 * Constructor, indicates configuration settings for NemexBagOfWords
	 * alignment using NemexA. Loads external dictionaries if required.
	 * 
	 * @param comp
	 *            NameValue table with configuration parameters for
	 *            <code>NemexBagOfWordsAligner</code>.
	 * @param removeStopWords
	 *            remove stop words while processing data or not.
	 * @param stopWords
	 *            set of stop-words in English
	 * 
	 */
	public NemexBagOfWordsAligner(CommonConfig config, boolean removeStopWords,
			Set<String> stopWords) throws ConfigurationException {

		super(config, "NemexBagOfWordsScoring");

		this.removeStopWords = removeStopWords;
		this.stopWords = stopWords;

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
				String curPOS = curToken.getPos().getPosValue();
				int curStartOffset = curToken.getBegin();
				int curEndOffset = curToken.getEnd();

				if (removeStopWords) {
					if (stopWords.contains(curTokenText.toLowerCase())) {
						continue; // skip the token if it is a stop word
					}
				}

				logger.info("Adding NemexType annotation on entry");
				List<String> nemexAnnotVals = Arrays.asList(curTokenText);
				addNemexAnnotation(view, nemexAnnotVals, curStartOffset,
						curEndOffset);
				logger.info("Finished adding NemexType annotation on entry");

				ArrayList<EntryInfo> offsets = new ArrayList<EntryInfo>();

				// Add all the entries (words) to entryMap and entryInvIndex
				EntryInfo curOffset = new EntryInfo(view, curStartOffset,
						curEndOffset, curPOS, false);

				if (entryMap.containsValue(curTokenText)) {
					offsets = entryInvIndex.get(curTokenText);
				} else {
					index++;
					entryMap.put(index, curTokenText);
				}

				totalNumOfGazetteerEntries++;
				offsets.add(curOffset);

				entryInvIndex.put(curTokenText, offsets);
			}

			addEntryToDict(this.gazetteerAlignLookup, entryMap, entryInvIndex,
					totalNumOfGazetteerEntries);

		} catch (Exception e) {
			logger.info("Error updating the Gazetteer file");
			e.printStackTrace();
		}
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

			String queryText = token.getCoveredText().toLowerCase();
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

	@Override
	public String getComponentName() {
		return "NemexBagOfWordsAligner";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	private Set<String> stopWords;
	private boolean removeStopWords;

	private final static Logger logger = Logger
			.getLogger(NemexBagOfWordsAligner.class.getName());

}
