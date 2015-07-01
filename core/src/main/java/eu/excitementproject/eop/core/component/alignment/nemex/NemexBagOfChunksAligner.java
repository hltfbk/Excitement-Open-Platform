package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.dfki.lt.nemex.a.NEMEX_A;
import de.dfki.lt.nemex.a.data.GazetteerNotLoadedException;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitement.type.nemex.NemexType;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
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

public class NemexBagOfChunksAligner implements AlignmentComponent {

	/**
	 * 
	 * @param comp
	 * @param direction
	 * @throws ConfigurationException
	 */
	public NemexBagOfChunksAligner(CommonConfig config, String direction) throws ConfigurationException {
		
		NameValueTable comp = config.getSection("NemexBagOfChunksScoring");
		
		this.numOfExtDicts = Integer.parseInt(comp.getString("numOfExtDicts"));

		if (0 == this.numOfExtDicts) {

			logger.info("No external dictionaries to load.");

		} else {

			this.extDict = comp.getString("extDicts").split(",");

			String[] delimiterExtLookup = comp.getString("delimiterExtLookup")
					.split(",");

			String[] delimSwitchOffExtLookupStrings = comp.getString(
					"delimSwitchOffExtLookup").split(",");
			boolean[] delimiterSwitchOffExtLookup = new boolean[delimSwitchOffExtLookupStrings.length];
			for (int i = 0; i < delimSwitchOffExtLookupStrings.length; i++)
				delimiterSwitchOffExtLookup[i] = Boolean
						.valueOf(delimSwitchOffExtLookupStrings[i]);

			String[] nGramSizeExtLookupStrings = comp.getString(
					"nGramSizeExtLookup").split(",");
			int[] nGramSizeExtLookup = new int[nGramSizeExtLookupStrings.length];
			for (int i = 0; i < nGramSizeExtLookupStrings.length; i++)
				nGramSizeExtLookup[i] = Integer
						.valueOf(nGramSizeExtLookupStrings[i]);

			String[] ignoreDuplicateNGramsExtLookupStrings = comp.getString(
					"ignoreDuplicateNGramsExtLookup").split(",");
			boolean[] ignoreDuplicateNGramsExtLookup = new boolean[ignoreDuplicateNGramsExtLookupStrings.length];
			for (int i = 0; i < ignoreDuplicateNGramsExtLookupStrings.length; i++)
				ignoreDuplicateNGramsExtLookup[i] = Boolean
						.valueOf(ignoreDuplicateNGramsExtLookupStrings[i]);

			// load all the external dictionaries that are required for lookup
			NemexAlignerUtility.loadExternalDictionaries(numOfExtDicts,
					extDict, delimiterExtLookup, delimiterSwitchOffExtLookup,
					nGramSizeExtLookup, ignoreDuplicateNGramsExtLookup);

			this.simMeasureExtLookup = comp.getString("simMeasureExtLookup")
					.split(",");

			String[] thresholdStrings = comp.getString("simThresholdExtLookup")
					.split(",");
			this.simThresholdExtLookup = new double[thresholdStrings.length];
			for (int i = 0; i < thresholdStrings.length; i++)
				simThresholdExtLookup[i] = Double.valueOf(thresholdStrings[i]);
		}

		this.gazetteerAlignLookup = comp.getString("gazetteerAlignLookup");
		this.simMeasureAlignLookup = comp.getString("simMeasureAlignLookup");
		this.simThresholdAlignLookup = Double.valueOf(comp
				.getString("simThresholdAlignLookup"));
		this.delimiterAlignLookup = comp.getString("delimiterAlignLookup");
		this.delimiterSwitchOffAlignLookup = Boolean.valueOf(comp
				.getString("delimiterSwitchOffAlignLookup"));
		this.nGramSizeAlignLookup = Integer.valueOf(comp
				.getString("nGramSizeAlignLookup"));
		this.ignoreDuplicateNGramsAlignLookup = Boolean.valueOf(comp
				.getString("ignoreDuplicateNGramsAlignLookup"));

		this.direction = direction;


		// Load WordNet if required
		this.isWN = Boolean.valueOf(comp.getString("isWN"));
		;
		if (this.isWN) {

			String WNRelations = comp.getString("WNRelations");
			boolean isWNCollapsed = Boolean.valueOf(comp
					.getString("isWNCollapsed"));
			boolean useFirstSenseOnlyLeft = Boolean.valueOf(comp
					.getString("useFirstSenseOnlyLeft"));
			boolean useFirstSenseOnlyRight = Boolean.valueOf(comp
					.getString("useFirstSenseOnlyRight"));
			String wnPath = comp.getString("wnPath");

			this.wnlr = NemexAlignerUtility.loadWordnet(WNRelations, wnPath,
					isWNCollapsed, useFirstSenseOnlyLeft,
					useFirstSenseOnlyRight);

		}

		//Load the chunker model
		loadChunkerModel(comp.getString("chunkerModelPath"));
	}

	private void loadChunkerModel(String chunkerModelPath) {
		// initialize the chunker model file
		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = new FileInputStream(chunkerModelPath);
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			logger.error("Could not load Chunker model");
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		this.chunker = new opennlp.tools.chunker.ChunkerME(model);

	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {

		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null) {
			logger.info("Null JCas object");
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");
		}

		JCas textView = NemexAlignerUtility.readCas(aJCas, "text");
		JCas hypoView = NemexAlignerUtility.readCas(aJCas, "hypothesis");

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

			// Required for Chunking
			List<String> tokenTextArray = new ArrayList<String>();
			List<String> tagArray = new ArrayList<String>();
			List<String> tokenLemmaArray = new ArrayList<String>();
			List<Integer> tokenStartOffsetArray = new ArrayList<Integer>();
			List<Integer> tokenEndOffsetArray = new ArrayList<Integer>();

			for (Iterator<Annotation> tIter = tokenAnnots.iterator(); tIter
					.hasNext();) {

				Token curToken = (Token) tIter.next();

				tokenTextArray.add(curToken.getCoveredText().toLowerCase());
				tagArray.add(curToken.getPos().getPosValue());
				tokenLemmaArray.add(curToken.getLemma().getValue());
				tokenStartOffsetArray.add(curToken.getBegin());
				tokenEndOffsetArray.add(curToken.getEnd());

			}

			String originalQuery = new String(); // query generated directly
													// from chunk in given
													// data

			// chunking using openNLP chunker
			Span[] chunk = this.chunker.chunkAsSpans(
					tokenTextArray.toArray(new String[tokenTextArray.size()]),
					tagArray.toArray(new String[tagArray.size()]));

			// iterating over all chunks
			for (int i = 0; i < chunk.length; i++) {

				// Starting and Ending token ID for chunk, Chunk tag - NP,
				// VP, etc.
				int start = chunk[i].getStart();
				int end = chunk[i].getEnd();
				String tag = chunk[i].getType();

				// Chunks to be added as dictionary entries
				Set<String> entries = new HashSet<String>();

				originalQuery = "";
				entries.add("");

				// Iterating over all the tokens in the chunk
				for (int j = start; j < end; j++) {

					// To keep track of queries generated using ExtDicts and
					// Gazetteer
					Set<String> newQueries = new HashSet<String>();

					for (Iterator<String> entryIter = entries.iterator(); entryIter
							.hasNext();) {

						String curQuery = entryIter.next();

						// Add the delimiter at end of previous word
						if (curQuery != "") {
							curQuery = curQuery + this.delimiterAlignLookup;
						}

						if (originalQuery != "") {
							originalQuery += this.delimiterAlignLookup;
						}

						String curToken = tokenTextArray.get(j);
						String curPosTag = tagArray.get(j);

						originalQuery = originalQuery + curToken;
						newQueries.add(curQuery + curToken);

						// Find matching entries from external dictionaries
						// and Wordnet for nouns
						if (curPosTag.startsWith("NN")) {

							List<String> values = new ArrayList<String>();

							for (int n = 0; n < this.numOfExtDicts; n++) {
								values.addAll(NEMEX_A.checkSimilarity(
										curToken.toLowerCase(),
										this.extDict[n],
										this.simMeasureExtLookup[n],
										this.simThresholdExtLookup[n]));
							}

							if (isWN) {

								// Finding and adding wordnet entries to
								// generate new entries
								for (Iterator<WordnetLexicalResource> wnIter = wnlr
										.iterator(); wnIter.hasNext();) {
									for (LexicalRule<? extends RuleInfo> rule : wnIter
											.next()
											.getRulesForLeft(
													tokenLemmaArray.get(j),
													new BySimplerCanonicalPartOfSpeech(
															SimplerCanonicalPosTag.NOUN))) {
										values.add(rule
												.getRLemma()
												.toLowerCase()
												.replace(
														" ",
														this.delimiterAlignLookup));

									}
								}

							}

							// Appending new info to existing phrase so far
							for (int l = 0; l < values.size(); l++) {
								String newQuery = curQuery + values.get(l);
								newQueries.add(newQuery);
							}

						}
					}

					entries.clear();
					entries.addAll(newQueries);
					newQueries.clear();

				}

				// Once we have all the chunks (including the newly
				// generated ones), that need to be added as entries, add
				// them to entryMap and entryInvIndex

				for (Iterator<String> eIter = entries.iterator(); eIter
						.hasNext();) {

					String curEntry = eIter.next();
					int startOffset = tokenStartOffsetArray.get(start);
					int endOffset = tokenEndOffsetArray.get(end - 1);

					logger.info("Adding NemexType annotation on entry");
					List<String> nemexAnnotVals = Arrays.asList(curEntry);
					NemexAlignerUtility.addNemexAnnotation(view,
							nemexAnnotVals, startOffset, endOffset);
					logger.info("Finished adding NemexType annotation on entry");

					// Add he generated Chunk annotation to the
					// AnnotationIndex
					Chunk annot = new Chunk(view, startOffset, endOffset);
					annot.setChunkValue(curEntry);
					annot.addToIndexes();

					curEntry = curEntry.toLowerCase();

					EntryInfo curOffset;

					if (curEntry.equals(originalQuery.toLowerCase())) {
						curOffset = new EntryInfo(view, startOffset, endOffset,
								tag, false);
					} else {
						curOffset = new EntryInfo(view, startOffset, endOffset,
								tag, true);
					}

					ArrayList<EntryInfo> offsets = new ArrayList<EntryInfo>();

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

			NemexAlignerUtility.addEntryToDict(this.gazetteerAlignLookup,
					entryMap, entryInvIndex, totalNumOfGazetteerEntries);

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

		// Required for Chunking
		List<String> tokenTextArray = new ArrayList<String>();
		List<String> tagArray = new ArrayList<String>();
		List<String> tokenLemmaArray = new ArrayList<String>();
		List<Integer> tokenStartOffsetArray = new ArrayList<Integer>();
		List<Integer> tokenEndOffsetArray = new ArrayList<Integer>();

		// Iterate over all tokens in given view and use token text as queries.
		for (Iterator<Token> iter = tokenAnnots.iterator(); iter.hasNext();) {

			Token token = (Token) iter.next();

			tokenTextArray.add(token.getCoveredText().toLowerCase());
			tagArray.add(token.getPos().getPosValue());
			tokenLemmaArray.add(token.getLemma().getValue());
			tokenStartOffsetArray.add(token.getBegin());
			tokenEndOffsetArray.add(token.getEnd());
		}

		Span[] chunk = this.chunker.chunkAsSpans(
				tokenTextArray.toArray(new String[tokenTextArray.size()]),
				tagArray.toArray(new String[tagArray.size()]));

		String str = new String();

		for (int i = 0; i < chunk.length; i++) {
			int start = chunk[i].getStart(); // start index for tokens in
												// that chunk
			int end = chunk[i].getEnd(); // end index for tokens in that
											// chunk
			str = "";
			for (int j = start; j < end; j++) {
				if (str != "")
					str += this.delimiterAlignLookup;
				str += tokenTextArray.get(j);
			}

			int chunkStartOffset = tokenStartOffsetArray.get(start);
			int chunkEndOffset = tokenEndOffsetArray.get(end - 1);

			Chunk chunkAnnot = new Chunk(queryView, chunkStartOffset,
					chunkEndOffset);
			chunkAnnot.setChunkValue(str);
			chunkAnnot.addToIndexes();

			str = str.toLowerCase();

			try {
				values = NEMEX_A.checkSimilarity(str, gazetteerAlignLookup,
						this.simMeasureAlignLookup,
						this.simThresholdAlignLookup);

				if (values.size() > 0) {
					logger.info("Query text: " + str);
					logger.info("Similar entry: " + values);
					NemexType alignmentAnnot = NemexAlignerUtility
							.addNemexAnnotation(queryView, values,
									chunkStartOffset, chunkEndOffset);

					NemexAlignerUtility.addAlignmentLink(alignmentAnnot,
							queryView, chunkStartOffset, chunkEndOffset,
							entryMap, entryInvIndex, this.direction,
							this.simThresholdAlignLookup);
				}
			} catch (GazetteerNotLoadedException e) {
				logger.info("BOChunks gazetteer is not loaded");
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
		return "NemexBagOfChunksAligner";
	}

	/**
	 * @return Name for current instance.
	 */
	@Override
	public String getInstanceName() {
		return null;
	}

	private int numOfExtDicts;
	private String[] extDict;
	private double[] simThresholdExtLookup;
	private String[] simMeasureExtLookup;

	private String gazetteerAlignLookup;
	private double simThresholdAlignLookup;
	private String simMeasureAlignLookup;
	private String delimiterAlignLookup;
	private boolean delimiterSwitchOffAlignLookup;
	private int nGramSizeAlignLookup;
	private boolean ignoreDuplicateNGramsAlignLookup;

	private String direction;

	private boolean isWN;
	private List<WordnetLexicalResource> wnlr;

	private ChunkerME chunker;

	private final static Logger logger = Logger
			.getLogger(NemexBagOfChunksAligner.class.getName());

}
