/**
 * 
 */
package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.uimafit.util.JCasUtil;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
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

	public NemexAligner(String gazetteerFilePath, String delimiter,
			Boolean delimiterSwitchOff, int nGramSize,
			Boolean ignoreDuplicateNgrams, String similarityMeasure,
			double similarityThreshold, String chunkerModelPath) {

		this.gazetteerFilePath = gazetteerFilePath;
		this.delimiter = delimiter;
		this.delimiterSwitchOff = delimiterSwitchOff;
		this.nGramSize = nGramSize;
		this.ignoreDuplicateNgrams = ignoreDuplicateNgrams;

		this.similarityMeasure = similarityMeasure;
		this.similarityThreshold = similarityThreshold;
		this.chunkerModelPath = chunkerModelPath;
		// NEMEX_A.loadNewGazetteer(this.gazetteerFilePath, this.delimiter,
		// this.delimiterSwitchOff, this.nGramSize,
		// this.ignoreDuplicateNgrams);
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
		// intro log
		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null) {
			logger.info("Null JCas object");
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");
		}

		HashMap<Integer, String> queryMap = new HashMap<Integer, String>();

		HashMap<String, ArrayList<QueryInfo>> queryIndex = new HashMap<String, ArrayList<QueryInfo>>();

		try {
			JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			createDictionary(hypoView, queryMap, queryIndex);

		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the hypothesis view", e);
		}

		try {
			JCas textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			logger.info("TEXT: " + textView.getDocumentText());
			annotateSubstring(textView, queryMap, queryIndex);

		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the text view", e);
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
	public void createDictionary(JCas hypoView,
			HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<QueryInfo>> queryIndex)
			throws PairAnnotatorComponentException {
		logger.info("HYPO: " + hypoView.getDocumentText());

		String query = new String();

		int index = 0;
		double totalNoOfQueries = 0;
		logger.info("Creating queries from hypothesis");

		Collection<Token> hTokens = JCasUtil.select(hypoView, Token.class);
		Token[] tokens = hTokens.toArray(new Token[hTokens.size()]);

		logger.info(tokens.length + " Tokens obtained from hypothesis");
		int tLength = tokens.length;
		String[] hTokenArray = new String[tLength];
		String[] hTagArray = new String[tLength];
		int[] hTokenStartOffsetArray = new int[tLength];
		int[] hTokenEndOffsetArray = new int[tLength];

		for (int tNum = 0; tNum < tokens.length; tNum++) {
			Token token = tokens[tNum];

			hTokenArray[tNum] = token.getCoveredText();
			hTagArray[tNum] = token.getPos().getPosValue();
			hTokenStartOffsetArray[tNum] = token.getBegin();
			hTokenEndOffsetArray[tNum] = token.getEnd();

		}

		logger.info("Token text and offsets obtained");

		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = new FileInputStream(
					chunkerModelPath);
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		ChunkerME chunker = new opennlp.tools.chunker.ChunkerME(model);
		Span[] chunk = chunker.chunkAsSpans(hTokenArray, hTagArray);

		for (int i = 0; i < chunk.length; i++) {
			int start = chunk[i].getStart();
			int end = chunk[i].getEnd();
			String tag = chunk[i].getType();
			query = "";
			for (int j = start; j < end; j++) {
				if (query != "")
					query += "#";
				query += hTokenArray[j];

			}
			
			
			int startOffset = hTokenStartOffsetArray[start];
			int endOffset = hTokenEndOffsetArray[end - 1];
			
			Chunk annot = new Chunk(hypoView, startOffset, endOffset);
			annot.setChunkValue(query);
			annot.addToIndexes();
			
			query = query.toLowerCase();
			logger.info("Query:" + query);
			ArrayList<QueryInfo> offsets = new ArrayList<QueryInfo>();

			QueryInfo curOffset = new QueryInfo(hypoView, startOffset,
					endOffset, tag);

			if (queryMap.containsValue(query)) {
				offsets = queryIndex.get(query);
			} else {
				index++;
				queryMap.put(index, query);
			}

			totalNoOfQueries++;
			offsets.add(curOffset);

			queryIndex.put(query, offsets);

		}

		logger.info("Finished creating queries");

		logger.info("Adding queries to dictionary");
		Iterator<Entry<Integer, String>> iter = queryMap.entrySet().iterator();

		PrintWriter fw;
		try {
			fw = new PrintWriter(new FileWriter(this.gazetteerFilePath));
			fw.println("0 utf-8 EN " + (int) totalNoOfQueries + " "
					+ queryMap.size());
			fw.close();

			fw = new PrintWriter(new FileWriter(this.gazetteerFilePath, true));
			
			HashMap<String, Integer> querySenseMap = new HashMap<String, Integer>();

			
			while (iter.hasNext()) {

				Map.Entry<Integer, String> queryEntry = (Map.Entry<Integer, String>) iter
						.next();
				int idx = (int) queryEntry.getKey();
				String queryText = (String) queryEntry.getValue();

				ArrayList<QueryInfo> value = (ArrayList<QueryInfo>) queryIndex
						.get(queryText);

				logger.info("Creating dictionary entry from hypothesis query");

				List<String> values = new ArrayList<String>();
				values.add(queryText);

				Iterator<QueryInfo> queryIter = value.iterator();
				
				while (queryIter.hasNext()) {

					QueryInfo hQuery = (QueryInfo) queryIter.next();
					int start = hQuery.getStartOffset();
					int end = hQuery.getEndOffset();
					String tag = hQuery.getPosTag();
					if (querySenseMap.containsKey(tag)) {
						querySenseMap.put(tag, querySenseMap.get(tag) + 1);
					} else
						querySenseMap.put(tag, 1);
					logger.info("Adding NemexType annotation on hypothesis query");
					addNemexAnnotation(hypoView, values, start, end);
					logger.info("Finished adding NemexType annotation on hypothesis query");
				}

				String entry = new String();
				entry = new String(idx + " "
						+ Math.log(value.size() / totalNoOfQueries) + " "
						+ queryText);

				Iterator<Entry<String, Integer>> senseIter = querySenseMap
						.entrySet().iterator();
				while (senseIter.hasNext()) {
					Map.Entry<String, Integer> sense = (Map.Entry<String, Integer>) senseIter
							.next();
					entry = entry + " " + sense.getKey() + ":"
							+ sense.getValue() + ":"
							+ Math.log(sense.getValue() / totalNoOfQueries);
				}
				logger.info("Adding entry to dictionary," + entry);

				fw.println(entry);
				// NEMEX_A.loadedGazetteers.get(this.gazetteerFilePath)
				// .getGazetteer().addNewEntry(entry.get(0));
				logger.info("Finished adding entry to dictionary");
				querySenseMap.clear();
			}
			fw.close();
			NEMEX_A.loadNewGazetteer(this.gazetteerFilePath, this.delimiter,
					this.delimiterSwitchOff, this.nGramSize,
					this.ignoreDuplicateNgrams);
		} catch (IOException e) {
			logger.info("Error updating the Gazetteer file");
			e.printStackTrace();
		}

	}

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
	private void annotateSubstring(JCas textView,
			HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<QueryInfo>> queryIndex) {

		String str = new String();
		List<String> values = new ArrayList<String>();

		Collection<Token> tTokens = JCasUtil.select(textView, Token.class);
		Token[] tokens = tTokens.toArray(new Token[tTokens.size()]);

		logger.info(tokens.length + " Tokens obtained");
		int tLength = tokens.length;
		String[] tTokenArray = new String[tLength];
		String[] tTagArray = new String[tLength];
		int[] tTokenStartOffsetArray = new int[tLength];
		int[] tTokenEndOffsetArray = new int[tLength];

		for (int tNum = 0; tNum < tokens.length; tNum++) {
			Token token = tokens[tNum];

			tTokenArray[tNum] = token.getCoveredText();
			tTagArray[tNum] = token.getPos().getPosValue();
			tTokenStartOffsetArray[tNum] = token.getBegin();
			tTokenEndOffsetArray[tNum] = token.getEnd();

		}

		logger.info("Token text and offsets obtained");

		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = new FileInputStream(
					chunkerModelPath);
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		ChunkerME chunker = new opennlp.tools.chunker.ChunkerME(model);
		Span[] chunk = chunker.chunkAsSpans(tTokenArray, tTagArray);

		for (int i = 0; i < chunk.length; i++) {
			int start = chunk[i].getStart();
			int end = chunk[i].getEnd();
			str = "";
			for (int j = start; j < end; j++) {
				if (str != "")
					str += "#";
				str += tTokenArray[j];
			}
			
			
			
			
			int startOffset = tTokenStartOffsetArray[start];
			int endOffset = tTokenEndOffsetArray[end - 1];
			
			Chunk annot = new Chunk(textView, startOffset, endOffset);
			annot.setChunkValue(str);
			annot.addToIndexes();
			
			str = str.toLowerCase();

			
			try {
				values = NEMEX_A.checkSimilarity(str, gazetteerFilePath,
						similarityMeasure, similarityThreshold);
				
				if (values.size() > 0) {
					logger.info("Text query: " + str);
					logger.info("Similar entry: " + values);
					NemexType textAnnot = addNemexAnnotation(textView, values,
							startOffset, endOffset);

					addAlignmentLink(textAnnot, textView, startOffset, endOffset, queryMap,
							queryIndex);
				}
			} catch (GazetteerNotLoadedException e) {
				logger.info("Could not load the gazetteer");
				e.printStackTrace();
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
			NemexType annot = new NemexType(view, startOffset, endOffset);
			StringArray valuesArray = new StringArray(view, entry.size());
			String[] entryArray = entry.toArray(new String[entry.size()]);
			valuesArray.copyFromArray(entryArray, 0, 0, entryArray.length);

			//logger.info("Setting values of annotation");
			annot.setValues(valuesArray);

			// add annotation to index of annotations
			annot.addToIndexes();
			return annot;
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
			HashMap<String, ArrayList<QueryInfo>> queryIndex) {
		String[] values = textAnnot.getValues().toStringArray();
		for (int i = 0; i < values.length; i++) {

			String query = values[i];

			ArrayList<QueryInfo> hypotheses = queryIndex.get(query);
			Iterator<QueryInfo> hypoIter = hypotheses.iterator();

			while (hypoIter.hasNext()) {
				QueryInfo hypothesis = hypoIter.next();
				JCas hypoView = hypothesis.getHypothesisView();
				int hypoStart = hypothesis.getStartOffset();
				int hypoEnd = hypothesis.getEndOffset();

				addLink(textView, textStart, textEnd, hypoView, hypoStart,
						hypoEnd);
			}

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
				tg.setBegin(ntype.getBegin());
				tg.setEnd(ntype.getEnd());
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
				tg.setBegin(ntype.getBegin());
				tg.setEnd(ntype.getEnd());
				tg.addToIndexes();
				hypoTarget = tg;
			}
		}

		// Mark an alignment.Link and add it to the hypothesis view
		Link link = new Link(hView);
		link.setTSideTarget(textTarget);
		link.setHSideTarget(hypoTarget);

		// Set the link direction
		link.setDirection(Direction.HtoT);

		// Set strength according to the nemex-a threshold
		link.setStrength(this.similarityThreshold);

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

	@Override
	public String getComponentName() {
		return this.getClass().getName();
	}

	@Override
	public String getInstanceName() {
		return this.gazetteerFilePath;
	}

	private final static Logger logger = Logger.getLogger(NemexAligner.class);
	private String gazetteerFilePath;
	private String delimiter;
	private Boolean delimiterSwitchOff;
	private int nGramSize;
	private Boolean ignoreDuplicateNgrams;

	private double similarityThreshold;
	private String similarityMeasure;
	private String chunkerModelPath;

}
