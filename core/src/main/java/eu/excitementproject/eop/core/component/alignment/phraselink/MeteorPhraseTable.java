package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

		int lc = 0; 
		int ec = 0; 
		String line = null; 
		while((line = tableReader.readLine()) != null)
		{
			// TODO fill up the data structure
			line.length(); 
			lc++;
		}
		ec = lc / 3; 
		logger.info("loading complelte, " + lc + " lines (" + ec + " entries).") ; 

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

		// TODO Do matching and fill phrList 



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
	private final Map<String,Map<String,Float>> entryPairsAsMap; 
	private final Logger logger; 
	
	

}
