/**
 * 
 */
package eu.excitementproject.eop.core.component.alignment.nemex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitement.type.nemex.*;
import de.dfki.lt.nemex.a.*;
import de.dfki.lt.nemex.a.data.Gazetteer;
import de.dfki.lt.nemex.a.data.GazetteerNotLoadedException;
import de.dfki.lt.nemex.a.similarity.SimilarityMeasure;

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
		NEMEX_A.loadNewGazetteer(this.gazetteerFilePath, this.delimiter,
				this.delimiterSwitchOff, this.nGramSize,
				this.ignoreDuplicateNgrams);
	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		// intro log
		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (aJCas == null)
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");

		createDictionary(aJCas);

		JCas textView;

		try {
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);

		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)",
					e);
		}

		logger.info("TEXT: " + textView.getDocumentText().substring(0, 25)
				+ " ...");

		String text = textView.getDocumentText().toLowerCase();

		annotateSubstring(text);

	}

	public void createDictionary(JCas aJCas)
			throws PairAnnotatorComponentException {
		logger.info("Create dictionary entries from all the queries in H:  ");

		if (aJCas == null)
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");

		JCas hypoView;

		try {
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the HypothesisView", e);

		}

		logger.info("HYPO: " + hypoView.getDocumentText().substring(0, 25)
				+ " ...");

		String hypothesis = hypoView.getDocumentText().toLowerCase();
		String query = new String();

		HashMap<String, ArrayList<QueryOffset>> queryIndex = new HashMap<String, ArrayList<QueryOffset>>(); //query String and offsets for the query Strings
		HashMap<Integer, String> queryMap = new HashMap<Integer, String>(); //query id and query String
		
		int index = 0;
		for (int i = 0; i < hypothesis.length(); i++)
			for (int j = 1; j <= hypothesis.length()+1; j++) {
				
				query = hypothesis.substring(i, j);
				ArrayList<QueryOffset> offsets = new ArrayList<QueryOffset>();
				
				QueryOffset curOffset = new QueryOffset(hypoView, i,j);
				
				if (queryMap.containsValue(query)) {
					offsets= queryIndex.get(query);
				}
				else {
					index++;
					queryMap.put(index, query);
				}
					
				
				offsets.add(curOffset);

				queryIndex.put(query, offsets);

			}

		int i = 0;
		String firstLine = new String("0 utf-8 EN 4 4");

		NEMEX_A.loadedGazetteers.get(this.gazetteerFilePath).getGazetteer()
				.addNewEntry(firstLine);

		Iterator iter = queryMap.entrySet().iterator();
		while (iter.hasNext()) {
			i++;
			Map.Entry queryEntry = (Map.Entry) iter.next();
			String key = (String) queryEntry.getKey();
			ArrayList<QueryOffset> value = (ArrayList<QueryOffset>) queryEntry.getValue();
			
			String entry = new String(i + " " + value.size() + " "
					+ key + " " + "NG:" + "1:"
					+ value.size());
			NEMEX_A.loadedGazetteers.get(this.gazetteerFilePath).getGazetteer()
					.addNewEntry(entry);
		}

	}

	private void annotateSubstring(String content) {

		String str = new String();
		List<String> values = null;
		for (int i = 0; i < content.length(); i++)
			for (int j = 1; j <= content.length(); j++) {
				str = content.substring(i, j);
				try {
					values = NEMEX_A.checkSimilarity(str, gazetteerFilePath,
							similarityMeasure, similarityThreshold);
				} catch (GazetteerNotLoadedException e) {
					e.printStackTrace();
				}

				addAnnotation(values, i, j);

			}

	}

	private void addAnnotation(List<String> values, int startOffset,
			int endOffset) {
		/*NemexType annot = new NemexType();
		annot.setBegin(startOffset);
		annot.setEnd(endOffset);
		annot.setValues(values);*/

	}

	@Override
	public String getComponentName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
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
