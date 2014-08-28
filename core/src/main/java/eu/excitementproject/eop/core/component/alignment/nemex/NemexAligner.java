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
		
		HashMap<Integer, String> queryMap = new HashMap<Integer, String>(); //query id and query String
		HashMap<String, ArrayList<QueryOffset>> queryIndex = new HashMap<String, ArrayList<QueryOffset>>(); //query String and offsets for the query Strings
		
		createDictionary(aJCas, queryMap, queryIndex);

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

		annotateSubstring(textView, queryMap, queryIndex);

	}

	public void createDictionary(JCas aJCas, HashMap<Integer, String> queryMap, HashMap<String, ArrayList<QueryOffset>> queryIndex)
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
			NemexType hypoAnnot = addNemexAnnotation(hypoView, entry, i, j);
		}

	}

	private void annotateSubstring(JCas textView, , HashMap<Integer, String> queryMap, HashMap<String, ArrayList<QueryOffset>> queryIndex) {
		
		String content = textView.getDocumentText().toLowerCase();
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

				NemexType textAnnot = addNemexAnnotation(textView, values, i, j);
				addAlignmentLink(textAnnot, textView, i, j, queryMap, queryIndex);

			}

	}

	private NemexType addNemexAnnotation(JCas view, List<String> values, int startOffset,
			int endOffset) {
		
		NemexType annot = new NemexType(view, startOffset, endOffset);
		//annot.setBegin(startOffset);
		//annot.setEnd(endOffset);
		annot.setValues(values);
		return annot;

	}
	
	private void addAlignmentLink(NemexType textAnnot, JCas textView, int textStart, int textEnd, HashMap<Integer, String> queryMap, HashMap<String, ArrayList<QueryOffset>> queryIndex) {
		List<String> values = textAnnot.getValues();
		Iterator<String> textAnnotIter = values.iterator();
		while(textAnnotIter.hasNext()) {
			String value = textAnnotIter.next();
			int queryId = Integer.parseInt(value.split("\\s+").get(0));
			String query = (String) queryMap.get(queryId);
			
			ArrayList<QueryOffset> hypotheses = queryIndex.get(query);
			Iterator<QueryOffset> hypoIter = hypotheses.iter();
			
			while(hypoIter.hasNext()) {
				QueryOffset hypothesis = hypoIter.next();
				JCas hypoView = hypothesis.getHypothesisView();
				int hypoStart = hypothesis.getStartOffset();
				int hypoEnd = hypothesis.getEndOffset();
				
				addLink(textView, textStart, textEnd, hypoView, hypoStart, hypoEnd);
			}
			
			
		}
		
	}
	
	private void addLink(JCas tView, int tStart, int tEnd, JCas hView, int hStart, int hEnd) {
		// Prepare the Target instances
		Target textTarget = new Target(tView);
		Target hypoTarget = new Target(hView);
				
		// Prepare an FSArray instance and put the target annotations in it   
		//FSArray textAnnots = new FSArray(tView, tEnd - tStart + 1);
		//FSArray hypoAnnots = new FSArray(hView, hEnd - hStart + 1);
		
		for(NemexType ntype: JCasUtil.select(tView, NemexType.class))
		{
			// Actual work code to put Annotations 
			// in a Target instance. 
			
			// 1) prepare a Target instance. 
			if ( (ntype.getBegin() >= tStart) && (ntype.getEnd() <= tEnd)) {
			Target tg = new Target(tView);
			// 2) prepare a FSArray instance, put the target annotations in it.   
			// (Note that FSArray is not really a Java Array -- but FSArray) 
			FSArray tAnnots = new FSArray(tView, 1); // this is a size 1 FSarray; 
			tAnnots.set(0,ntype);
			// 3) Okay, now the FSArray is prepared. Put it on field "targetAnnotations" 
			tg.setTargetAnnotations(tAnnots);
			// 4) Set begin - end value of the Target annotation (just like any annotation)
			// note that, setting of begin and end of Target is a convention. 
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
		
		for(NemexType ntype: JCasUtil.select(hypoViewOfJCas1, Token.class) )
		{
			if ( (ntype.getBegin() >= tStart) && (ntype.getEnd() <= tEnd)) {
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

		textTarget.setTargetAnnotations(textAnnots);
		hypoTarget.setTargetAnnotations(hypoAnnots);
		
		// Mark an alignment.Link and add it to the hypothesis view
		Link link = new Link(hypoView); 
		link.setTSideTarget(textTarget); 
		link.setHSideTarget(hypoTarget); 

		// Set the link direction
		link.setDirection(Direction.hToT); 
				
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
