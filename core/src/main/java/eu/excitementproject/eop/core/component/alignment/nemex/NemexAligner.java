/**
 * 
 */
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

/**
 * @author Madhumita
 * 
 */
public class NemexAligner implements AlignmentComponent {

	public NemexAligner(String gazetteerFilePath, String delimiter,
			Boolean delimiterSwitchOff, int nGramSize,
			Boolean ignoreDuplicateNgrams, String similarityMeasure,
			double similarityThreshold) {

		this.gazetteerFilePath = gazetteerFilePath;
		this.delimiter = delimiter;
		this.delimiterSwitchOff = delimiterSwitchOff;
		this.nGramSize = nGramSize;
		this.ignoreDuplicateNgrams = ignoreDuplicateNgrams;

		this.similarityMeasure = similarityMeasure;
		this.similarityThreshold = similarityThreshold;
		// NEMEX_A.loadNewGazetteer(this.gazetteerFilePath, this.delimiter,
		// this.delimiterSwitchOff, this.nGramSize,
		// this.ignoreDuplicateNgrams);
	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		// intro log
		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null) {
			logger.info("Null JCas object");
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");
		}

		// unique query id vs query string
		HashMap<Integer, String> queryMap = new HashMap<Integer, String>();

		// query string vs hypothesis views and offsets
		HashMap<String, ArrayList<QueryOffset>> queryIndex = new HashMap<String, ArrayList<QueryOffset>>();

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

	public void createDictionary(JCas hypoView,
			HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<QueryOffset>> queryIndex)
			throws PairAnnotatorComponentException {
		logger.info("HYPO: " + hypoView.getDocumentText());

		String hypothesis = hypoView.getDocumentText().toLowerCase();
		hypothesis = hypothesis.replaceAll(" ", "#");
		String query = new String();

		int index = 0;
		int totalNoOfQueries = 0;
		logger.info("Creating queries from hypothesis");
		for (int i = 0; i < hypothesis.length(); i++) {
			for (int j = i + 1; j <= hypothesis.length(); j++) {

				query = hypothesis.substring(i, j);

				ArrayList<QueryOffset> offsets = new ArrayList<QueryOffset>();

				QueryOffset curOffset = new QueryOffset(hypoView, i, j);

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
		}

		logger.info("Finished creating queries");

		logger.info("Adding queries to dictionary");
		Iterator<Entry<Integer, String>> iter = queryMap.entrySet().iterator();

		PrintWriter fw;
		try {
			fw = new PrintWriter(new FileWriter(this.gazetteerFilePath, true));
			fw.println("0 utf-8 EN " + totalNoOfQueries + " " + queryMap.size());
			while (iter.hasNext()) {

				Map.Entry<Integer, String> queryEntry = (Map.Entry<Integer, String>) iter
						.next();
				int idx = (int) queryEntry.getKey();
				String queryText = (String) queryEntry.getValue();

				ArrayList<QueryOffset> value = (ArrayList<QueryOffset>) queryIndex
						.get(queryText);

				logger.info("Creating dictionary entry from hypothesis query");

				List<String> entry = new ArrayList<String>();
				entry.add(new String(idx + " " + value.size() + " " + queryText
						+ " " + "NG:" + "1:" + value.size()));

				logger.info("Adding entry to dictionary," + entry.get(0));

				fw.println(entry.get(0));
				// NEMEX_A.loadedGazetteers.get(this.gazetteerFilePath)
				// .getGazetteer().addNewEntry(entry.get(0));
				logger.info("Finished adding entry to dictionary");

				Iterator<QueryOffset> queryIter = value.iterator();
				while (queryIter.hasNext()) {
					QueryOffset hQuery = (QueryOffset) queryIter.next();
					int start = hQuery.getStartOffset();
					int end = hQuery.getEndOffset();
					logger.info("Adding NemexType annotation on hypothesis query");
					addNemexAnnotation(hypoView, entry, start, end);
					logger.info("Finished adding NemexType annotation on hypothesis query");
				}

			}
			fw.close();
			NEMEX_A.loadNewGazetteer(this.gazetteerFilePath, this.delimiter,
					this.delimiterSwitchOff, this.nGramSize,
					this.ignoreDuplicateNgrams);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void annotateSubstring(JCas textView,
			HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<QueryOffset>> queryIndex) {

		String content = textView.getDocumentText().toLowerCase();
		content = content.replaceAll(" ", "#");
		String str = new String();
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < content.length(); i++)
			for (int j = i + 1; j <= content.length(); j++) {
				str = content.substring(i, j);

				try {
					values = NEMEX_A.checkSimilarity(str, gazetteerFilePath,
							similarityMeasure, similarityThreshold);
					if (values.size() > 0) {
						NemexType textAnnot = addNemexAnnotation(textView,
								values, i, j);
						addAlignmentLink(textAnnot, textView, i, j, queryMap,
								queryIndex);
					}
				} catch (GazetteerNotLoadedException e) {
					e.printStackTrace();
				}
			}

	}

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

			logger.info("Setting values of annotation");
			annot.setValues(valuesArray);
			annot.addToIndexes();
			return annot;
		} catch (Exception e) {
			logger.info("Could not generate NemexType");
			e.printStackTrace();
		}

		return null;

	}

	private void addAlignmentLink(NemexType textAnnot, JCas textView,
			int textStart, int textEnd, HashMap<Integer, String> queryMap,
			HashMap<String, ArrayList<QueryOffset>> queryIndex) {
		String[] values = textAnnot.getValues().toStringArray();
		for (int i = 0; i < values.length; i++) {

			String query = values[i];

			ArrayList<QueryOffset> hypotheses = queryIndex.get(query);
			Iterator<QueryOffset> hypoIter = hypotheses.iterator();

			while (hypoIter.hasNext()) {
				QueryOffset hypothesis = hypoIter.next();
				JCas hypoView = hypothesis.getHypothesisView();
				int hypoStart = hypothesis.getStartOffset();
				int hypoEnd = hypothesis.getEndOffset();

				addLink(textView, textStart, textEnd, hypoView, hypoStart,
						hypoEnd);
			}

		}

	}

	private void addLink(JCas tView, int tStart, int tEnd, JCas hView,
			int hStart, int hEnd) {
		// Prepare the Target instances
		Target textTarget = new Target(tView);
		Target hypoTarget = new Target(hView);

		for (NemexType ntype : JCasUtil.select(tView, NemexType.class)) {
			// Actual work code to put Annotations
			// in a Target instance.

			// 1) prepare a Target instance.
			if ((ntype.getBegin() >= tStart) && (ntype.getEnd() <= tEnd)) {
				Target tg = new Target(tView);
				// 2) prepare a FSArray instance, put the target annotations in
				// it.
				FSArray tAnnots = new FSArray(tView, 1); // this is a size 1
															// FSarray;
				tAnnots.set(0, ntype);
				// 3) Okay, now the FSArray is prepared. Put it on field
				// "targetAnnotations"
				tg.setTargetAnnotations(tAnnots);
				// 4) Set begin - end value of the Target annotation (just like
				// any annotation)
				// note that, setting of begin and end of Target is a
				// convention.
				// - begin as the earliest "begin" (among Target-ed annotations)
				// - end as the latest "end" (among Target-ed annotations)
				tg.setBegin(ntype.getBegin());
				tg.setEnd(ntype.getEnd());
				// 5) add it to the index (just like any annotation)
				tg.addToIndexes();

				// The target instance is now ready.
				textTarget = tg;
			}
		}

		for (NemexType ntype : JCasUtil.select(hView, NemexType.class)) {
			if ((ntype.getBegin() >= tStart) && (ntype.getEnd() <= tEnd)) {
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

}
