package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.apache.uima.fit.util.JCasUtil;

import de.dfki.lt.nemex.a.NEMEX_A;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitement.type.alignment.Target;
import eu.excitement.type.nemex.NemexType;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * 
 * This class provides a base class with utility functions  for all alignments 
 * generated using NemexA, which is a tool developed at DFKI for matching multiword 
 * entries.
 *
 * @author Madhumita
 * @since September 2015
 * 
 */

public class NemexAligner implements AlignmentComponent {

	public NemexAligner(CommonConfig config, String sectionName)
			throws ConfigurationException {

		NameValueTable comp = config.getSection(sectionName);

		this.numOfExtDicts = Integer.parseInt(comp.getString("numOfExtDicts"));

		if (0 == numOfExtDicts) {

			logger.info("No external dictionaries to load.");

		} else {

			this.extDicts = comp.getString("extDicts").split(",");

			String[] delimiterExtLookup = comp.getString("delimExtLookup")
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
			loadExternalDictionaries(numOfExtDicts, extDicts,
					delimiterExtLookup, delimiterSwitchOffExtLookup,
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

		this.direction = comp.getString("direction");

	}

	/**
	 * Loads external dictionaries.
	 * 
	 * @param numOfExtDicts
	 *            The number of external dictionaries to load.
	 * @param externalDict
	 *            The paths to external dictionaries.
	 * @param delimiterExtLookup
	 *            Delimiters used as word separators in each external
	 *            dictionaries.
	 * @param delimiterSwitchOffExtLookup
	 *            Whether to switch of delimiters when looking up in given
	 *            external dictionary or not.
	 * @param nGramSizeExtLookup
	 *            size of nGrams to use for looking up in external dictionaries.
	 * @param ignoreDuplicateNGramsExtLookup
	 *            Whether to ignore duplicate nGrams or not when looking up in
	 *            external dictionaries.
	 */

	protected void loadExternalDictionaries(int numOfExtDicts,
			String[] externalDict, String[] delimiterExtLookup,
			boolean[] delimiterSwitchOffExtLookup, int[] nGramSizeExtLookup,
			boolean[] ignoreDuplicateNGramsExtLookup) {

		try {
			for (int i = 0; i < numOfExtDicts; i++) {
				NEMEX_A.loadNewGazetteer(externalDict[i],
						delimiterExtLookup[i], delimiterSwitchOffExtLookup[i],
						nGramSizeExtLookup[i],
						ignoreDuplicateNGramsExtLookup[i]);
			}

			logger.info("Loading external Nemex Dictionaries done");
		} catch (Exception e) {
			logger.error("Error in loading the external Nemex Dictionaries");
		}

	}

	/**
	 * Returns text or hypothesis view from the given T/H pair.
	 * 
	 * @param aJCas
	 *            The JCas object corresponding to T/H pair currently processed.
	 * @param type
	 *            Whether text object is required or hypothesis.
	 * @return Text or Hypothesis views for aJCas, as required.
	 * @throws AlignmentComponentException
	 */
	protected JCas readCas(JCas aJCas, String type)
			throws AlignmentComponentException {
		try {
			JCas view = null;
			if (type.equalsIgnoreCase("text"))
				view = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			else if (type.equalsIgnoreCase("hypothesis"))
				view = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			else
				return view;

			logger.info(type.toUpperCase() + " " + view.getDocumentText());
			return view;
		} catch (CASException e) {
			throw new AlignmentComponentException("Failed to access the "
					+ type + " view", e);
		}
	}

	
	/**
	 * Creates Nemex gazetteer from all entries in entryMap.
	 * 
	 * @param gazetteerAlignLookup
	 *            Gazetteer file which needs to be created.
	 * @param entryMap
	 *            Map containing all entries to add to gazetteer.
	 * @param entryInvIndex
	 *            Inverted index of dictionary entries and hypothesis views and
	 *            offsets generating the entry.
	 * @param totalNumOfGazetteerEntries
	 *            total number of entries
	 * @throws IOException
	 */
	protected void addEntryToDict(String gazetteerAlignLookup,
			HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryInvIndex,
			double totalNumOfGazetteerEntries) throws IOException {

		logger.info("Adding entries to dictionary");

		PrintWriter fw;

		/*
		 * First open the gazetteer file in write mode to overwrite existing
		 * file. Then open it in append mode to append all entries.
		 */
		fw = new PrintWriter(new FileWriter(gazetteerAlignLookup));
		fw.println("0 utf-8 EN " + (int) totalNumOfGazetteerEntries + " "
				+ entryMap.size());
		fw.close();

		fw = new PrintWriter(new FileWriter(gazetteerAlignLookup, true));

		// Iterate over all dictionary entries to be written to file
		for (Iterator<Entry<Integer, String>> iter = entryMap.entrySet()
				.iterator(); iter.hasNext();) {

			HashMap<String, Integer> entrySenseMap = new HashMap<String, Integer>();

			Map.Entry<Integer, String> curEntry = (Map.Entry<Integer, String>) iter
					.next();

			int idx = curEntry.getKey();
			String entryText = curEntry.getValue();

			ArrayList<EntryInfo> entryHypos = (ArrayList<EntryInfo>) entryInvIndex
					.get(entryText);

			/*
			 * Iterate over all the hypotheses that have generated the given
			 * entry. Account all the used senses of the entry in the those
			 * hypothesis substrings.
			 */
			for (Iterator<EntryInfo> entryIter = entryHypos.iterator(); entryIter
					.hasNext();) {

				EntryInfo hQuery = (EntryInfo) entryIter.next();
				String tag = hQuery.getPosTag();
				if (entrySenseMap.containsKey(tag)) {
					entrySenseMap.put(tag, entrySenseMap.get(tag) + 1);
				} else
					entrySenseMap.put(tag, 1);
			}

			// Generate entry in the format required by Gazetteer file.
			logger.info("Creating dictionary entry string from query");

			String entry = new String(idx + " "
					+ Math.log(entryHypos.size() / totalNumOfGazetteerEntries)
					+ " " + entryText);

			/*
			 * Iterate over all senses of usage for the entry
			 */
			for (Iterator<Entry<String, Integer>> senseIter = entrySenseMap
					.entrySet().iterator(); senseIter.hasNext();) {
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
			logger.info("Finished adding entry to dictionary");

			// NEMEX_A.loadedGazetteers.get(this.gazetteerFilePath)
			// .getGazetteer().addNewEntry(entry.get(0));

		}
		fw.close();
	}

	/**
	 * Adds NemexType annotation at given offset range with given values of
	 * annotation.
	 * 
	 * @param view
	 *            View to add annotation at.
	 * @param entry
	 *            values for annotation.
	 * @param startOffset
	 *            start offset of annotation on given view.
	 * @param endOffset
	 *            end offset of annotation on given view.
	 * @return created annotation.
	 */
	protected NemexType addNemexAnnotation(JCas view, List<String> entry,
			int startOffset, int endOffset) {

		logger.info("Within addNemexAnnotation function, adding annotation on view: "
				+ view.getDocumentText()
				+ " , and adding entries "
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
	 * Finds all views which have generated matching entries to given query.
	 * Adds alignment between given offsets of query view and entry view.
	 * 
	 * @param queryAnnot
	 *            NemexType annotation on query string.
	 * @param queryView
	 *            View for query string.
	 * @param queryStart
	 *            Start offset of query string.
	 * @param queryEnd
	 *            End offset of query string.
	 * @param entryMap
	 *            Map of all entry IDs and entry string.
	 * @param entryIndex
	 *            Index of entry string and views and offsets generating it.
	 * @param direction
	 *            direction of alignment.
	 * @param simThreshold
	 *            similarity threshold for nemex lookup, used as threshold for
	 *            alignment.
	 */
	protected void addAlignmentLink(NemexType queryAnnot, JCas queryView,
			int queryStart, int queryEnd, HashMap<Integer, String> entryMap,
			HashMap<String, ArrayList<EntryInfo>> entryIndex, String direction,
			double simThreshold) {

		// Entries similar to query at queryView from queryStart to queryEnd
		String[] values = queryAnnot.getValues().toStringArray();

		// Iterate over all similar entries
		for (int i = 0; i < values.length; i++) {

			String curEntry = values[i];

			if (entryIndex.containsKey(curEntry)) {

				/*
				 * Iterate over all views and offsets which have generated given
				 * curEntry
				 */
				for (Iterator<EntryInfo> entryViewIter = entryIndex.get(
						curEntry).iterator(); entryViewIter.hasNext();) {

					EntryInfo entry = entryViewIter.next();

					JCas entryView = entry.getView();
					int entryStart = entry.getStartOffset();
					int entryEnd = entry.getEndOffset();

					// Add alignment link between current query and entry
					addLink(queryView, queryStart, queryEnd, entryView,
							entryStart, entryEnd, direction, simThreshold);
				}
			} else
				logger.info("Entry not present in entryIndex");

		}

	}

	/**
	 * Creates alignment link annotation between entry and query annotations.
	 * 
	 * @param queryView
	 *            view for query string.
	 * @param queryStart
	 *            start offset of query string.
	 * @param queryEnd
	 *            end offset of query string.
	 * @param entryView
	 *            view for entry string.
	 * @param entryStart
	 *            start offset for entry string.
	 * @param entryEnd
	 *            end offset for entry string.
	 * @param direction
	 *            direction of link
	 * @param simThreshold
	 *            similarity threshold for nemex lookup, used as link threshold.
	 */
	private void addLink(JCas queryView, int queryStart, int queryEnd,
			JCas entryView, int entryStart, int entryEnd, String direction,
			double simThreshold) {

		logger.info("Adding alignment link");

		Link link;

		// Prepare the Target instances

		Target queryTarget = new Target(queryView);
		Target entryTarget = new Target(entryView);

		for (NemexType ntype : JCasUtil.select(queryView, NemexType.class)) {

			if ((ntype.getBegin() == queryStart)
					&& (ntype.getEnd() == queryEnd)) {

				logger.info("NemexType annotation on queryView:"
						+ ntype.getCoveredText());

				Target tg = new Target(queryView);

				FSArray queryAnnots = new FSArray(queryView, 1);
				queryAnnots.set(0, ntype);

				tg.setTargetAnnotations(queryAnnots);
				tg.setBegin(queryStart);
				tg.setEnd(queryEnd);
				tg.addToIndexes();

				queryTarget = tg;
			}

		}

		for (NemexType ntype : JCasUtil.select(entryView, NemexType.class)) {
			if ((ntype.getBegin() == entryStart)
					&& (ntype.getEnd() == entryEnd)) {

				logger.info("NemexType annotation on entryView:"
						+ ntype.getCoveredText());

				Target tg = new Target(entryView);
				FSArray entryAnnots = new FSArray(entryView, 1);
				entryAnnots.set(0, ntype);
				tg.setTargetAnnotations(entryAnnots);
				tg.setBegin(entryStart);
				tg.setEnd(entryEnd);
				tg.addToIndexes();
				entryTarget = tg;
			}
		}

		if (direction.equalsIgnoreCase("HtoT")) {

			// Mark an alignment.Link and add it to the hypothesis view
			link = new Link(entryView);
			// Set link targets
			link.setTSideTarget(queryTarget);
			link.setHSideTarget(entryTarget);
			// Set the link direction
			link.setDirection(Direction.HtoT);

			// Mark begin and end according to the hypothesis target
			link.setBegin(entryTarget.getBegin());
			link.setEnd(entryTarget.getEnd());

		} else {

			// Mark an alignment.Link and add it to the hypothesis view
			link = new Link(queryView);
			// Set link targets
			link.setTSideTarget(entryTarget);
			link.setHSideTarget(queryTarget);
			// Set the link direction
			link.setDirection(Direction.TtoH);

			// Mark begin and end according to the hypothesis target
			link.setBegin(queryTarget.getBegin());
			link.setEnd(queryTarget.getEnd());

		}

		// Set strength as that of BOChunks if activated, BOW otherwise
		link.setStrength(simThreshold);

		// Add the link information
		link.setAlignerID("NemexA");
		link.setAlignerVersion("1.0");
		link.setLinkInfo("nemex-results");

		// Add to index
		link.addToIndexes();

		logger.info("Added alignment link between entryView NemexType and queryView NemexType annotations");

	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getComponentName() {
		return "NemexAligner";
	}

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void close() throws PairAnnotatorComponentException
	{
		// nothing to close on this aligner
	}


	private final static Logger logger = Logger.getLogger(NemexAligner.class);

	protected int numOfExtDicts;
	protected String[] extDicts;
	protected String[] simMeasureExtLookup;
	protected double[] simThresholdExtLookup;

	protected String gazetteerAlignLookup;
	protected String simMeasureAlignLookup;
	protected double simThresholdAlignLookup;
	protected String delimiterAlignLookup;
	protected boolean delimiterSwitchOffAlignLookup;
	protected int nGramSizeAlignLookup;
	protected boolean ignoreDuplicateNGramsAlignLookup;

	protected String direction;

}
