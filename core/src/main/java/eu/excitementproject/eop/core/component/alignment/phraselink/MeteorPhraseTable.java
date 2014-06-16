package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map; 
import java.util.HashMap; 

import org.apache.log4j.Logger;

/**
 * This class represents Meteor Phrase Table. 
 * 
 * Memo: 
 *    TODO consider (later) this aspect. -- force symmetric? for the moment, no. 
 *   - one interesting aspect is that Meteor Phrase Table is *not* symmetric. 
 *   - (e.g. "12 candidate countries -> 12 candidates does exist", but not the other way around, etc)
 * 
 * @author Tae-Gil Noh
 *
 */
public class MeteorPhraseTable {

	public MeteorPhraseTable(String resourcePath) throws IOException
	{
		// initialize private final variables 
		logger = Logger.getLogger(this.getClass().toString()); 
		entryPairsAsMap = new HashMap<String,Map<String,Float>>(); 

		// start loading the table text from resource path
		logger.info("Loading Meteor Paraphrase table from resource path: " + resourcePath); 

		InputStream is = getClass().getResourceAsStream(resourcePath);
		BufferedReader tableReader = new BufferedReader(new InputStreamReader(is)); 

		int ec = 0; 
		String line1 = null; 
		while((line1 = tableReader.readLine()) != null)
		{
			Float prob = Float.parseFloat(line1); 
			String lhs = tableReader.readLine(); 
			String rhs = tableReader.readLine(); 
			
			// no lhs yet in the map ?
			if (!entryPairsAsMap.containsKey(lhs))
			{
				// then, make the entry in the first-level map, 
				Map<String,Float> map = new HashMap<String,Float>(); 
				entryPairsAsMap.put(lhs, map); 
			}

			// add the (lhs, rhs, prob) 
			// lhs as the key for outer map, rhs as the key for inner map, 
			// and probability value is in the value of the inner map. 
			entryPairsAsMap.get(lhs).put(rhs, prob); 
			
			ec++;
		}
		logger.info("loading complelte, " + ec + " entries).") ; 

	}

	/**
	 * Query the table; return possible paraphrases for the given phrase with score.  
	 * 
	 * Give phrase is treated as "Left". (LHS ->) and the returning values are 
	 * (RHS, probability)
	 * 
	 * @param phrase
	 * @return
	 */
	public List<ScoredString> lookupParaphrasesFor(String phrase)
	{
		ArrayList<ScoredString> phrList = new ArrayList<ScoredString>(); 

		// check if the phrase exist as LHS in the paraphrase table 
		if (!entryPairsAsMap.containsKey(phrase))
		{
			// if not, no need to look into it. 
			// returning an empty list.
			return phrList;   
		}
		
		// Okay. there is paraphrase rules applicable to this give LHS. 
		// return them all, as a list of (String, Score) tuples. 
		// [ (rhs1, probability for rhs1), (rhs2, probability for rhs2), ...] 
		Map<String,Float> mapForLhs = entryPairsAsMap.get(phrase); 
		Iterator<String> itr = mapForLhs.keySet().iterator();
		while(itr.hasNext())
		{
			String rhs = itr.next(); 
			Float prob = mapForLhs.get(rhs); 
			ScoredString rhsAndItsProb = new ScoredString(rhs, prob); 
			phrList.add(rhsAndItsProb); 
		}

		return phrList; 
	}

	/**
	 * A simple class that represents a tuple of String (that holds a phrase)
	 * and its score. 
	 * 
	 * ("A phrase", double value) 
	 * 
	 * @author Tae-Gil Noh
	 *
	 */
	public class ScoredString
	{
		public ScoredString(String phrase, double score)
		{
			this.string = phrase; 
			this.score = score; 
		}

		public double getScore()
		{
			return score; 
		}

		public String getString()
		{
			return string; 
		}

		private final double  score; 
		private final String string; 
	}

	// internal data structures and variables 

	// entryPairsAsMap holds all entries. 
	// single entry is: (lhs string, rhs string, probability float) 
	// 
	// lhs as the key for outer map, 
	// rhs as the key for inner map, 
	// and probability value is in the value of the inner map. 
	private final Map<String,Map<String,Float>> entryPairsAsMap; 
	private final Logger logger; 
	
}
