package eu.excitementproject.eop.core.component.alignment.phraselink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class represents Meteor Phrase Table. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class MeteorPhraseTable {
	
	public MeteorPhraseTable(String resourcePath) throws IOException
	{
		logger = Logger.getLogger(this.getClass().toString()); 

		// TODO load up the resource. (in memory?) 
		logger.debug("Loading Meteor Paraphrase table from resource path: " + resourcePath); 

		
	}

	/**
	 * Query the table; return possible paraphrases for the given phrase with score.  
	 * 
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
	
	private final Logger logger; 

}
