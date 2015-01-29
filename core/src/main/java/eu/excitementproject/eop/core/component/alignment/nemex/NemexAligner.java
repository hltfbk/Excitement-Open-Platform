/**
 * 
 */
package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitement.type.alignment.Target;
import eu.excitement.type.nemex.*;
import de.dfki.lt.nemex.a.*;
import de.dfki.lt.nemex.a.data.GazetteerNotLoadedException;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.*;
import opennlp.tools.chunker.*;
import opennlp.tools.util.Span;

/**
 * 
 * This class provides nemex.NemexType annotations for a given JCas pair and
 * adds alignment.Link on nemex.NemexType.
 * 
 * Adds alignment from hypothesis to text annotations. An online Gazetteer is
 * created based on queries from the hypothesis. The queries in text are
 * annotated based on the online Gazetteer.
 * 
 * (This is the first version of the aligner and does not do efficient query
 * generation and disambiguation. It simply creates all possible substrings as
 * queries from hypothesis and text and adds all possible approximate
 * alignments.)
 * 
 * Resource it is based on: Nemex-A tool developed at DFKI.
 * 
 * 
 * 
 * @author Madhumita
 * @since June 2014
 * 
 */

public class NemexAligner implements AlignmentComponent {

	public NemexAligner(Boolean isBOW, Boolean isBOL, Boolean isBOChunks,
			int numOfExtDicts, String[] externalDictPath,
			String[] similarityMeasureExtLookup,
			double[] similarityThresholdExtLookup, String gazetteerFilePath,
			String similarityMeasureAlignmentLookup,
			double similarityThresholdAlignmentLookup, String delimiter,
			Boolean delimiterSwitchOff, int nGramSize,
			Boolean ignoreDuplicateNgrams, String chunkerModelPath,
			String direction, boolean isWN, String WNRel,
			boolean isWNCollapsed, boolean useFirstSenseOnlyLeft,
			boolean useFirstSenseOnlyRight, String wnPath) {

		this.isBOW = isBOW;
		this.isBOL = isBOL;
		this.isBOChunks = isBOChunks;
		this.isWN = isWN;

		this.numOfExtDicts = numOfExtDicts;
		this.externalDictPath = externalDictPath;
		this.similarityMeasureExtLookup = similarityMeasureExtLookup;
		this.similarityThresholdExtLookup = similarityThresholdExtLookup;

		this.gazetteerFilePath = gazetteerFilePath;
		this.similarityMeasureAlignmentLookup = similarityMeasureAlignmentLookup;
		this.similarityThresholdAlignmentLookup = similarityThresholdAlignmentLookup;

		this.delimiter = delimiter;
		this.delimiterSwitchOff = delimiterSwitchOff;
		this.nGramSize = nGramSize;
		this.ignoreDuplicateNgrams = ignoreDuplicateNgrams;

		this.direction = direction;
		
		this.wnlr = null;

		if (this.numOfExtDicts == 0) {
			logger.info("No external dictionaries to load");
		} else {
			try {
				for (int i = 0; i < this.numOfExtDicts; i++) {
					NEMEX_A.loadNewGazetteer(this.externalDictPath[i],
							this.delimiter, this.delimiterSwitchOff,
							this.nGramSize, this.ignoreDuplicateNgrams);
				}
			} catch (Exception e) {
				logger.error("Error in loading the external Nemex Dictionaries");
			}

			logger.info("Loading external Nemex Dictionaries done");
		}

		if (isBOChunks) {

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

		try {
			if (isWN) {
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
					this.wnlr = new WordnetLexicalResource(wnFile,
							useFirstSenseOnlyLeft, useFirstSenseOnlyRight,
							wnRelSet);

				} else {
					for (WordNetRelation wnr : wnRelSet) {
						this.wnlr = new WordnetLexicalResource(wnFile,
								useFirstSenseOnlyLeft, useFirstSenseOnlyRight,
								Collections.singleton(wnr));

					}
				}
				logger.info("Load WordNet done.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method adds nemex.NemexType annotations between text and hypothesis
	 * for a given JCas pair and then adds alignment.Link between two
	 * nemex.NemexType targets.
	 * 
	 * @param JCas
	 *            aJCas The view, that holds the sentence(s) to be analyzed.
	 * @return
	 */

	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {

		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null) {
			logger.info("Null JCas object");
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");
		}

		JCas textView = null;
		JCas hypoView = null;

		// contains queryID and queryText for all unique queries in T or H,
		// depending on direction
		HashMap<Integer, String> queryMap = new HashMap<Integer, String>();

		// queryText with JCas ID and offsets for queryText
		HashMap<String, ArrayList<EntryInfo>> queryInvIndex = new HashMap<String, ArrayList<EntryInfo>>();

		try {
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			logger.info("TEXT: " + textView.getDocumentText());
		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the text view", e);
		}

		try {
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			logger.info("HYPOTHESIS: " + hypoView.getDocumentText());
		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the hypothesis view", e);
		}

		if (textView != null && hypoView != null) {
			if (direction == null) {
				logger.info("Direction not specified. Setting direction as HtoT by default");
				this.direction = "HtoT";
			}

			if (direction == "HtoT") {

				createDictionary(hypoView, queryMap, queryInvIndex);
				annotateSubstring(textView, queryMap, queryInvIndex);

			}

			else {
				createDictionary(textView, queryMap, queryInvIndex);
				annotateSubstring(hypoView, queryMap, queryInvIndex);
			}
		}

	}

	/**
	 * This method creates an online Gazetteer from queries in H, which is used
	 * to lookup aligned T queries.
	 * 
	 * After creating the Gazetteer, it loads it to the NemexA system.
	 * 
	 * It also annotates the H queries with nemex.NemexType.
	 * 
	 * @param JCas
	 *            hypoView The hypothesis view, that holds the hypothesis to be
	 *            analyzed.
	 * @param HashMap
	 *            <Integer, String> queryMap The map of unique query Id and
	 *            query (dictionary entry) String
	 * @param HashMap
	 *            <String, ArrayList<QueryOffset>> queryIndex Inverted index
	 *            from queries to the hypotheses and offsets which generate the
	 *            query.
	 * @return
	 */
	public void createDictionary(JCas view, HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex)
			throws PairAnnotatorComponentException {
		try {

			if (!this.isBOW && !this.isBOL && !this.isBOChunks) {
				logger.info("Setting the configuration to BOW by default");
				this.isBOW = true;
			}

			AnnotationIndex<Annotation> tokenAnnots = view
					.getAnnotationIndex(Token.type);

			double totalNumOfGazetteerEntries = 0;
			int index = 0; // id of Entry in Entry map
			

			// int numOfTokens = tokenAnnots.size();

			// Required for Chunking
			List<String> tokenTextArray = new ArrayList<String>();
			List<String> tagArray = new ArrayList<String>();
			List<String> tokenLemmaArray = new ArrayList<String>();
			List<Integer> tokenStartOffsetArray = new ArrayList<Integer>();
			List<Integer> tokenEndOffsetArray = new ArrayList<Integer>();

			Iterator<Annotation> tIter = tokenAnnots.iterator();

			while (tIter.hasNext()) {
				Token token = (Token) tIter.next();
				String curToken = token.getCoveredText().toLowerCase();
				String curLemma = token.getLemma().getValue();
				String curPOS = token.getPos().getPosValue();
				int curStartOffset = token.getBegin();
				int curEndOffset = token.getEnd();

				ArrayList<EntryInfo> offsets = new ArrayList<EntryInfo>();

				if (isBOW) {

					// Add all the entries to entryMap and entryInvIndex
					EntryInfo curOffset = new EntryInfo(view, curStartOffset,
							curEndOffset, curPOS, false);

					if (entryMap.containsValue(curToken)) {
						
						offsets = entryInvIndex.get(curToken);
					} else {
						index++;
						entryMap.put(index, curToken);
					}

					totalNumOfGazetteerEntries++;
					offsets.add(curOffset);

					entryInvIndex.put(curToken, offsets);
				}

				if (isBOL) {

					EntryInfo curOffset = new EntryInfo(view, curStartOffset,
							curEndOffset, curPOS, false);

					if (entryMap.containsValue(curLemma.toLowerCase())) {
						offsets = entryInvIndex.get(curLemma.toLowerCase());
					} else {
						index++;
						entryMap.put(index, curLemma.toLowerCase());
					}

					totalNumOfGazetteerEntries++;
					offsets.add(curOffset);

					entryInvIndex.put(curLemma.toLowerCase(), offsets);

					if (isWN) {

						for (LexicalRule<? extends RuleInfo> rule : wnlr
								.getRulesForLeft(curLemma, null)) {

							String curEntry = rule.getRLemma().toLowerCase()
									.replace(" ", this.delimiter);
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

				if (isBOChunks) {
					tokenTextArray.add(curToken);
					tagArray.add(curPOS);
					tokenLemmaArray.add(curLemma);
					tokenStartOffsetArray.add(curStartOffset);
					tokenEndOffsetArray.add(curEndOffset);
				}

			}

			if (isBOChunks) {

				String originalQuery = new String(); // query generated directly
														// from chunk in given
														// data

				// chunking using openNLP chunker
				Span[] chunk = this.chunker.chunkAsSpans(tokenTextArray
						.toArray(new String[tokenTextArray.size()]), tagArray
						.toArray(new String[tagArray.size()]));

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

						Iterator<String> entryIter = entries.iterator();

						while (entryIter.hasNext()) {

							String curQuery = entryIter.next();

							// Add the delimiter at end of previous word
							if (curQuery != "") {
								curQuery = curQuery + this.delimiter;
							}

							if (originalQuery != "") {
								originalQuery += this.delimiter;
							}

							String curToken = tokenTextArray.get(j);
							String curPosTag = tagArray.get(j);

							originalQuery = originalQuery + curToken;
							newQueries.add(curQuery + curToken);

							// Find matching entries from external dictionaries
							// and Wordnet for nouns
							if (curPosTag.equals("NN")
									|| curPosTag.equals("NNS")
									|| curPosTag.equals("NNP")
									|| curPosTag.equals("NNPS")) {

								List<String> values = new ArrayList<String>();

								for (int n = 0; n < this.numOfExtDicts; n++) {
									values.addAll(NEMEX_A.checkSimilarity(
											curToken.toLowerCase(),
											this.externalDictPath[n],
											this.similarityMeasureExtLookup[n],
											this.similarityThresholdExtLookup[n]));
								}

								if (isWN) {

									// Finding and adding wordnet entries to
									// generate new entries

									for (LexicalRule<? extends RuleInfo> rule : wnlr
											.getRulesForLeft(
													tokenLemmaArray.get(j),
													new BySimplerCanonicalPartOfSpeech(
															SimplerCanonicalPosTag.NOUN))) {
										values.add(rule.getRLemma()
												.toLowerCase()
												.replace(" ", this.delimiter));

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
					Iterator<String> eIter = entries.iterator();

					while (eIter.hasNext()) {

						String curEntry = eIter.next();
						int startOffset = tokenStartOffsetArray.get(start);
						int endOffset = tokenEndOffsetArray.get(end - 1);

						//Add he generated Chunk annotation to the AnnotationIndex
						Chunk annot = new Chunk(view, startOffset, endOffset);
						annot.setChunkValue(curEntry);
						annot.addToIndexes();

						curEntry = curEntry.toLowerCase();

						EntryInfo curOffset;
						
						if (curEntry.equals(originalQuery.toLowerCase())) {
							curOffset = new EntryInfo(view, startOffset,
									endOffset, tag, false);
						} else {
							curOffset = new EntryInfo(view, startOffset,
									endOffset, tag, true);
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
			}

			logger.info("Unloading Gazetteer for updating dictionary");
			NEMEX_A.unloadGazetteer(gazetteerFilePath);

			logger.info("Adding entries to dictionary");
			Iterator<Entry<Integer, String>> iter = entryMap.entrySet()
					.iterator();

			PrintWriter fw;

			fw = new PrintWriter(new FileWriter(this.gazetteerFilePath));
			fw.println("0 utf-8 EN " + (int) totalNumOfGazetteerEntries + " "
					+ entryMap.size());
			fw.close();

			fw = new PrintWriter(new FileWriter(this.gazetteerFilePath, true));

			HashMap<String, Integer> querySenseMap = new HashMap<String, Integer>();

			while (iter.hasNext()) {

				Map.Entry<Integer, String> queryEntry = (Map.Entry<Integer, String>) iter
						.next();
				int idx = (int) queryEntry.getKey();
				String queryText = (String) queryEntry.getValue();

				ArrayList<EntryInfo> value = (ArrayList<EntryInfo>) entryInvIndex
						.get(queryText);

				logger.info("Creating dictionary entry string from query");

				List<String> values = new ArrayList<String>();
				values.add(queryText);

				Iterator<EntryInfo> queryIter = value.iterator();

				while (queryIter.hasNext()) {

					EntryInfo hQuery = (EntryInfo) queryIter.next();
					int start = hQuery.getStartOffset();
					int end = hQuery.getEndOffset();
					String tag = hQuery.getPosTag();
					if (querySenseMap.containsKey(tag)) {
						querySenseMap.put(tag, querySenseMap.get(tag) + 1);
					} else
						querySenseMap.put(tag, 1);
					logger.info("Adding NemexType annotation on query");
					addNemexAnnotation(view, values, start, end);
					logger.info("Finished adding NemexType annotation on query");
				}

				String entry = new String();
				entry = new String(idx + " "
						+ Math.log(value.size() / totalNumOfGazetteerEntries)
						+ " " + queryText);

				Iterator<Entry<String, Integer>> senseIter = querySenseMap
						.entrySet().iterator();
				while (senseIter.hasNext()) {
					Map.Entry<String, Integer> sense = (Map.Entry<String, Integer>) senseIter
							.next();
					entry = entry
							+ " "
							+ sense.getKey()
							+ ":"
							+ sense.getValue()
							+ ":"
							+ Math.log(sense.getValue()
									/ totalNumOfGazetteerEntries);
				}
				logger.info("Adding entry to dictionary," + entry);

				fw.println(entry);
				// NEMEX_A.loadedGazetteers.get(this.gazetteerFilePath)
				// .getGazetteer().addNewEntry(entry.get(0));
				logger.info("Finished adding entry to dictionary");
				querySenseMap.clear();
			}
			fw.close();
			// wnlr.close();

			logger.info("Loading the gazetteer");
			NEMEX_A.loadNewGazetteer(this.gazetteerFilePath, this.delimiter,
					this.delimiterSwitchOff, this.nGramSize,
					this.ignoreDuplicateNgrams);

		}

		catch (Exception e) {
			logger.info("Error updating the Gazetteer file");
			e.printStackTrace();
		}

	}

	/*
	 * private String getQuery(String chunkerModelPath2) { // TODO
	 * Auto-generated method stub return null; }
	 */

	/**
	 * This method adds nemex.NemexType annotation on text queries.
	 * 
	 * It generates queries from text string. Approximate similar matches of
	 * queries are looked up using the Gazetteer created by the hypotheses.
	 * 
	 * The similar matches are added as values to nemex.NemexType annotation.
	 * 
	 * @param JCas
	 *            textView The text view, which holds the text to be analyzed.
	 * @param HashMap
	 *            <Integer, String> queryMap The map of unique query Id and
	 *            query (dictionary entry) String
	 * @param HashMap
	 *            <String, ArrayList<QueryOffset>> queryIndex Inverted index
	 *            from queries to the hypotheses and offsets which generate the
	 *            query.
	 * @return
	 */
	private void annotateSubstring(JCas view,
			HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<EntryInfo>> queryIndex) {

		String str = new String();
		List<String> values = new ArrayList<String>();
		Collection<Token> tokenAnnots = JCasUtil.select(view, Token.class);
		
		if(isBOW) {
			for(Iterator<Token> iter = tokenAnnots.iterator(); iter.hasNext();) {
				Token t = iter.next();
				str = t.getCoveredText().toLowerCase();
				int startOffset = t.getBegin();
				int endOffset = t.getEnd();
				
				try {
					values = NEMEX_A.checkSimilarity(str, gazetteerFilePath,
							this.similarityMeasureAlignmentLookup,
							this.similarityThresholdAlignmentLookup);

					if (values.size() > 0) {
						logger.info("Query text: " + str);
						logger.info("Similar entry: " + values);
						NemexType alignmentAnnot = addNemexAnnotation(view, values,
								startOffset, endOffset);

						addAlignmentLink(alignmentAnnot, view, startOffset,
								endOffset, queryMap, queryIndex);
					}
				} catch (GazetteerNotLoadedException e) {
					logger.info("Could not load the gazetteer");
					e.printStackTrace();
				}
			
			}
			
				
		}
		
		if(isBOW) {
			for(Iterator<Token> iter = tokenAnnots.iterator(); iter.hasNext();) {
				Token t = iter.next();
				str = t.getLemma().getValue().toLowerCase();
				int startOffset = t.getBegin();
				int endOffset = t.getEnd();
				
				try {
					values = NEMEX_A.checkSimilarity(str, gazetteerFilePath,
							this.similarityMeasureAlignmentLookup,
							this.similarityThresholdAlignmentLookup);

					if (values.size() > 0) {
						logger.info("Query text: " + str);
						logger.info("Similar entry: " + values);
						NemexType alignmentAnnot = addNemexAnnotation(view, values,
								startOffset, endOffset);

						addAlignmentLink(alignmentAnnot, view, startOffset,
								endOffset, queryMap, queryIndex);
					}
				} catch (GazetteerNotLoadedException e) {
					logger.info("Could not load the gazetteer");
					e.printStackTrace();
				}
			
			}
			
				
		}
		
		if(isBOChunks) {
		

		
		Token[] tokenAnnotsArray = tokenAnnots.toArray(new Token[tokenAnnots
				.size()]);

		int numOfTokens = tokenAnnotsArray.length;
		String[] tokenTextArray = new String[numOfTokens];
		String[] tagArray = new String[numOfTokens];
		int[] tokenStartOffsetArray = new int[numOfTokens];
		int[] tokenEndOffsetArray = new int[numOfTokens];

		for (int tNum = 0; tNum < tokenAnnotsArray.length; tNum++) {
			Token token = tokenAnnotsArray[tNum];

			tokenTextArray[tNum] = token.getCoveredText();
			tagArray[tNum] = token.getPos().getPosValue();
			tokenStartOffsetArray[tNum] = token.getBegin();
			tokenEndOffsetArray[tNum] = token.getEnd();

		}

		logger.info("Token text and offsets obtained");

		Span[] chunk = this.chunker.chunkAsSpans(tokenTextArray, tagArray);

		for (int i = 0; i < chunk.length; i++) {
			int start = chunk[i].getStart();
			int end = chunk[i].getEnd();
			str = "";
			for (int j = start; j < end; j++) {
				if (str != "")
					str += this.delimiter;
				str += tokenTextArray[j];
			}

			int startOffset = tokenStartOffsetArray[start];
			int endOffset = tokenEndOffsetArray[end - 1];

			Chunk chunkAnnot = new Chunk(view, startOffset, endOffset);
			chunkAnnot.setChunkValue(str);
			chunkAnnot.addToIndexes();

			str = str.toLowerCase();
		
			try {
				values = NEMEX_A.checkSimilarity(str, gazetteerFilePath,
						this.similarityMeasureAlignmentLookup,
						this.similarityThresholdAlignmentLookup);

				if (values.size() > 0) {
					logger.info("Query text: " + str);
					logger.info("Similar entry: " + values);
					NemexType alignmentAnnot = addNemexAnnotation(view, values,
							startOffset, endOffset);

					addAlignmentLink(alignmentAnnot, view, startOffset,
							endOffset, queryMap, queryIndex);
				}
			} catch (GazetteerNotLoadedException e) {
				logger.info("Could not load the gazetteer");
				e.printStackTrace();
			}
		}
		}
	}

	/**
	 * This method adds nemex.NemexType annotation on queries.
	 * 
	 * Annotations with on a given view, startOffset, endOffset and value are
	 * added to the index.
	 * 
	 * @param JCas
	 *            view The view which contains the text to be annotated
	 * @param List
	 *            <String> entry The values of the nemex.NemexType annotation
	 * @param int startOffset The startOffset of the nemex.NemexType annotation
	 * @param int endOffset The endOffset of the nemex.NemexType annotation
	 * @return NemexType The added nemex.NemexType annotation
	 */
	private NemexType addNemexAnnotation(JCas view, List<String> entry,
			int startOffset, int endOffset) {

		logger.info("Within addNemexAnnotation function, adding annotation on view: "
				+ view.getDocumentText()
				+ " ,and adding entries "
				+ entry
				+ " as values from start offset "
				+ startOffset
				+ " to end offset " + endOffset);

		try {
			NemexType curAnnot = new NemexType(view, startOffset, endOffset);
			StringArray valuesArray = new StringArray(view, entry.size());
			String[] entryArray = entry.toArray(new String[entry.size()]);
			valuesArray.copyFromArray(entryArray, 0, 0, entryArray.length);

			// logger.info("Setting values of annotation");
			curAnnot.setValues(valuesArray);

			// add annotation to index of annotations
			curAnnot.addToIndexes();
			logger.info("Added annotation to index");
			return curAnnot;
		} catch (Exception e) {
			logger.info("Could not generate NemexType");
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * This method adds alignment.Link annotation on nemex.NemexType.
	 * 
	 * Two targets are generated - one on text and hypothesis view each. The two
	 * targets are linked by alignment.Link from H to T.
	 * 
	 * @param NemexType
	 *            textAnnot The text annotation for text Target
	 * @param JCas
	 *            textView The JCas view which contains the text of a given pair
	 * @param int tStart The start offset of text annotation textAnnot
	 * @param int tEnd The end offset of text annotation textAnnot
	 * @param HashMap
	 *            <Integer, String> queryMap The map of unique query Id and
	 *            query (dictionary entry) String
	 * @param HashMap
	 *            <String, ArrayList<QueryOffset>> queryIndex Inverted index
	 *            from queries to the hypotheses and offsets which generate the
	 *            query.
	 * @return
	 */
	private void addAlignmentLink(NemexType textAnnot, JCas textView,
			int textStart, int textEnd, HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<EntryInfo>> queryIndex) {
		String[] values = textAnnot.getValues().toStringArray();
		for (int i = 0; i < values.length; i++) {

			String query = values[i];

			if (queryIndex.containsKey(query)) {
				ArrayList<EntryInfo> hypotheses = queryIndex.get(query);
				Iterator<EntryInfo> hypoIter = hypotheses.iterator();

				while (hypoIter.hasNext()) {
					EntryInfo hypothesis = hypoIter.next();
					JCas hypoView = hypothesis.getHypothesisView();
					int hypoStart = hypothesis.getStartOffset();
					int hypoEnd = hypothesis.getEndOffset();

					addLink(textView, textStart, textEnd, hypoView, hypoStart,
							hypoEnd);
				}
			} else
				logger.info("Query not present in queryIndex");

		}

	}

	/**
	 * This method adds alignment.Link annotation between text and hypothesis
	 * target
	 * 
	 * @param JCas
	 *            tView The JCas view which contains the text of a given pair
	 * @param int tStart The start offset of the text annotation to be used as
	 *        Target
	 * @param int tEnd The end offset of text annotation to be used as Target
	 * @param JCas
	 *            hView The JCas view which contains the hypothesis of a given
	 *            pair
	 * @param int hStart The start offset of hypothesis annotation to be used as
	 *        Target
	 * @param int hEnd The end offset of the hypothesis annotation to be used as
	 *        Target
	 * @return
	 */

	private void addLink(JCas tView, int tStart, int tEnd, JCas hView,
			int hStart, int hEnd) {

		// Prepare the Target instances
		Target textTarget = new Target(tView);
		Target hypoTarget = new Target(hView);

		for (NemexType ntype : JCasUtil.select(tView, NemexType.class)) {

			if ((ntype.getBegin() == tStart) && (ntype.getEnd() == tEnd)) {
				Target tg = new Target(tView);

				FSArray tAnnots = new FSArray(tView, 1);
				tAnnots.set(0, ntype);

				tg.setTargetAnnotations(tAnnots);
				tg.setBegin(tStart);
				tg.setEnd(tEnd);
				tg.addToIndexes();

				textTarget = tg;
			}
		}

		for (NemexType ntype : JCasUtil.select(hView, NemexType.class)) {
			if ((ntype.getBegin() == hStart) && (ntype.getEnd() == hEnd)) {
				Target tg = new Target(hView);
				FSArray hAnnots = new FSArray(hView, 1);
				hAnnots.set(0, ntype);
				tg.setTargetAnnotations(hAnnots);
				tg.setBegin(hStart);
				tg.setEnd(hEnd);
				tg.addToIndexes();
				hypoTarget = tg;
			}
		}

		if (direction == "HtoT") {
			// Mark an alignment.Link and add it to the hypothesis view
			Link link = new Link(hView);
			link.setTSideTarget(textTarget);
			link.setHSideTarget(hypoTarget);

			// Set the link direction
			link.setDirection(Direction.HtoT);

			// Set strength according to the nemex-a threshold
			link.setStrength(this.similarityThresholdAlignmentLookup);

			// Add the link information
			link.setAlignerID("NemexA");
			link.setAlignerVersion("1.0");
			link.setLinkInfo("nemex-results");

			// Mark begin and end according to the hypothesis target
			link.setBegin(hypoTarget.getBegin());
			link.setEnd(hypoTarget.getEnd());

			// Add to index
			link.addToIndexes();
		}

		else {
			// Mark an alignment.Link and add it to the hypothesis view
			Link link = new Link(hView);
			link.setTSideTarget(textTarget);
			link.setHSideTarget(hypoTarget);

			// Set the link direction
			link.setDirection(Direction.TtoH);

			// Set strength according to the nemex-a threshold
			link.setStrength(this.similarityThresholdAlignmentLookup);

			// Add the link information
			link.setAlignerID("NemexA");
			link.setAlignerVersion("1.0");
			link.setLinkInfo("nemex-results");

			// Mark begin and end according to the hypothesis target
			link.setBegin(hypoTarget.getBegin());
			link.setEnd(hypoTarget.getEnd());

			// Add to index
			link.addToIndexes();

		}
	}

	@Override
	public String getComponentName() {
		return this.getClass().getName();
	}

	@Override
	public String getInstanceName() {
		return this.gazetteerFilePath;
	}

	private final static Logger logger = Logger.getLogger(NemexAligner.class);

	private Boolean isBOW;
	private Boolean isBOL;
	private Boolean isBOChunks;
	private Boolean isWN;

	private int numOfExtDicts;
	private String[] externalDictPath;
	private double[] similarityThresholdExtLookup;
	private String[] similarityMeasureExtLookup;

	private String gazetteerFilePath;
	private double similarityThresholdAlignmentLookup;
	private String similarityMeasureAlignmentLookup;

	private String delimiter;
	private Boolean delimiterSwitchOff;
	private int nGramSize;
	private Boolean ignoreDuplicateNgrams;

	private String direction;

	private WordnetLexicalResource wnlr;
	private ChunkerME chunker;

}
