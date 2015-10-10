package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.dfki.lt.nemex.a.NEMEX_A;
import de.dfki.lt.nemex.a.data.GazetteerNotLoadedException;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitement.type.nemex.NemexType;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * Aligns person names occurring in T and H pairs.
 * 
 * @author Madhumita
 * @since September 2015
 * 
 */
public class NemexPersonNameAligner extends NemexAligner {

	/**
	 * Load required Nemex gazetterrs, the configuration parameters and the
	 * model to classify person names.
	 * 
	 * @param config
	 *            the configuration file
	 * @throws ConfigurationException
	 */
	public NemexPersonNameAligner(CommonConfig config)
			throws ConfigurationException {
		super(config, "NemexPersonNameScoring");

		// Load the NE model for person name
		loadPersonNameModel(config);
	}

	/**
	 * Load the trained opennlp model file to identify person names
	 * 
	 * @param config
	 *            the configuration file
	 * @throws ConfigurationException
	 */
	private void loadPersonNameModel(CommonConfig config)
			throws ConfigurationException {

		NameValueTable comp = config.getSection("NemexPersonNameScoring");
		String modelName = comp.getString("personNameModelPath");

		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(modelName);
		} catch (FileNotFoundException e1) {
			logger.warn("Please specify the correct model path for person name recognition");
		}

		try {
			this.model = new TokenNameFinderModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {

		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null) {
			logger.info("Null JCas object");
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");
		}

		JCas tView = readCas(aJCas, "text");
		JCas hView = readCas(aJCas, "hypothesis");

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
			createDictionary(tView, queryMap, queryInvIndex);
			annotateSubstring(hView, queryMap, queryInvIndex);
		} else {
			createDictionary(hView, queryMap, queryInvIndex);
			annotateSubstring(tView, queryMap, queryInvIndex);
		}

	}

	/**
	 * Add alignment link between matching names
	 * 
	 * @param queryView
	 *            view to generate query names from
	 * @param entryMap
	 *            map of id and names
	 * @param entryInvIndex
	 *            map of names and info like view it is generated from, offsets.
	 */
	private void annotateSubstring(JCas queryView,
			HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex) {

		List<String> values = new ArrayList<String>();

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

		// Recognize names in text
		Span[] nameSpan = identifyName(queryView);

		Collection<Token> tokens = JCasUtil.select(queryView, Token.class); // tokens
		
		Token[] tokenArr = new Token[tokens.size()];
		
		int i = 0;
		for(Token t:tokens)
			tokenArr[i++] = t;

		// Iterate over each identified name
		for (Span curNameSpan : nameSpan) {
			int nameStartToken = curNameSpan.getStart(); // starting token num
															// of name : included
			int nameEndToken = curNameSpan.getEnd(); // ending token num of name: excluded

			int nameStart = tokenArr[nameStartToken].getBegin(); // start offset
																// of name
			int nameEnd = tokenArr[nameEndToken-1].getEnd(); // end offset of name

			String queryName = queryView.getDocumentText().substring(nameStart,
					nameEnd); // name string
			logger.info("Found name, "+queryName);
			
			queryName = queryName.replaceAll(" ", this.delimiterAlignLookup); // replace
																	// space in
																	// multiword
																	// entry
																	// with
																	// delimiter

			try {

				// Find all similar entries to current word
				values = NEMEX_A.checkSimilarity(queryName,
						this.gazetteerAlignLookup, this.simMeasureAlignLookup,
						this.simThresholdAlignLookup);

				/*
				 * Add NemexType annotation if matching entries found. Add
				 * alignment link between query and retrieved entries.
				 */
				if (values.size() > 0) {
					logger.info("Query text: " + queryName);
					logger.info("Similar entry: " + values);
					NemexType queryAnnot = addNemexAnnotation(queryView,
							values, nameStart, nameEnd);

					addAlignmentLink(queryAnnot, queryView, nameStart, nameEnd,
							entryMap, entryInvIndex, this.direction,
							this.simThresholdAlignLookup);
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
	 * Create Nemex dictionaries from names in given view
	 * 
	 * @param view
	 *            view to generate dictionary from
	 * @param entryMap
	 *            map of id and names
	 * @param entryInvIndex
	 *            map of names and info like view it is generated from, offsets.
	 * @throws PairAnnotatorComponentException
	 */
	private void createDictionary(JCas view, HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex)
			throws PairAnnotatorComponentException {

		// Recognize names in text
		Span[] nameSpan = identifyName(view);

		Collection<Token> tokens = JCasUtil.select(view, Token.class); // tokens
				
		Token[] tokenArr = new Token[tokens.size()];
		
		int i = 0;
		for(Token t:tokens)
			tokenArr[i++] = t;

		double numOfGazetteerEntries = 0; // total no. of names
		int index = 0; // id of name in Entry map

		// Iterate over each identified name
		for (Span curNameSpan : nameSpan) {
			int nameStartToken = curNameSpan.getStart(); // starting token num
															// of name: inclusive
			int nameEndToken = curNameSpan.getEnd(); // ending token num of name:exclusive

			int nameStart = tokenArr[nameStartToken].getBegin(); // start offset
																// of name
			int nameEnd = tokenArr[nameEndToken-1].getEnd(); // end offset of name

			String name = view.getDocumentText().substring(nameStart, nameEnd); // name
																				// string
			logger.info("Found name,"+name);
			
			name = name.replaceAll(" ", this.delimiterAlignLookup); // replace space in
																// multiword
																// entry with
																// delimiter

			logger.info("Adding NemexType annotation on entry");
			List<String> nemexAnnotVals = Arrays.asList(name);
			addNemexAnnotation(view, nemexAnnotVals, nameStart, nameEnd);
			logger.info("Finished adding NemexType annotation on entry");

			ArrayList<EntryInfo> offsets = new ArrayList<EntryInfo>();

			// Add all the names to entryMap and entryInvIndex
			EntryInfo curOffset = new EntryInfo(view, nameStart, nameEnd,
					"PER_AR", false);

			if (entryMap.containsValue(name)) {
				offsets = entryInvIndex.get(name);
			} else {
				index++;
				entryMap.put(index, name);
			}

			numOfGazetteerEntries++;
			offsets.add(curOffset);

			entryInvIndex.put(name, offsets);

			// get all matching names from external dictionaries
			List<String> values = new ArrayList<String>();
			for (int n = 0; n < this.numOfExtDicts; n++) {
				try {
					values.addAll(NEMEX_A.checkSimilarity(name,
							this.extDicts[n], this.simMeasureExtLookup[n],
							this.simThresholdExtLookup[n]));
				} catch (GazetteerNotLoadedException e) {
					e.printStackTrace();
				}
			}

			// entry info indicating that it is an extended entry
			EntryInfo newOffset = new EntryInfo(view, nameStart, nameEnd,
					"PER_AR", true);

			// add all matching names to the entry map and index
			for (String curName : values) {
				if (entryMap.containsValue(curName)) {
					offsets = entryInvIndex.get(curName);
				} else {
					index++;
					entryMap.put(index, curName);
				}

				numOfGazetteerEntries++;
				offsets.add(newOffset);

				entryInvIndex.put(curName, offsets);
			}
		}

		try {
			// write all entries to a new dictionary
			addEntryToDict(this.gazetteerAlignLookup, entryMap, entryInvIndex,
					numOfGazetteerEntries);
		} catch (IOException e) {
			throw new PairAnnotatorComponentException(e.getMessage());
		}

	}

	/**
	 * Identify all occuring names
	 * 
	 * @param view
	 *            view to identify names from
	 * @return span of names wrt token indices
	 */
	private Span[] identifyName(JCas view) {
		NameFinderME nameFinder = new NameFinderME(model);

		// get all tokens in given view
		Collection<Token> tokens = JCasUtil.select(view, Token.class);

		String[] tokenStr = new String[tokens.size()];

		int i = 0;
		for (Iterator<Token> iter = tokens.iterator(); iter.hasNext();) {
			tokenStr[i++] = iter.next().getCoveredText();
		}

		Span nameSpans[] = nameFinder.find(tokenStr);

		return nameSpans;
	}

	@Override
	public String getComponentName() {
		return "NemexPersonNameAligner";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	private final static Logger logger = Logger
			.getLogger(NemexPersonNameAligner.class.getName());

	private TokenNameFinderModel model;
}
