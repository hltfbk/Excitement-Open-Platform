/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultCooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultRelation;
import eu.excitementproject.eop.distsim.items.StringBasedTextUnit;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;


/**
  * Extraction of co-occurrences, composed of word pairs in a given window of a given raw text sentence. Stop words are exclude according to a given stop words set 
  * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class RawTextBasedWordCooccurrenceExtraction extends WordCooccurrenceExtraction<String> {

	protected static final int DEFAULT_WINDOW_SIZE = 3;
	protected static final String NEIGHBOUR_REL = "NEIGHBOUR";
	
	private Logger logger = Logger.getLogger("RawTextBasedWordCooccurrenceExtraction.class");
	
	public RawTextBasedWordCooccurrenceExtraction() {
		this.stopWords =  new HashSet<String>();
		this.windowSize = DEFAULT_WINDOW_SIZE;
	}

	public RawTextBasedWordCooccurrenceExtraction(Set<String> stopWords, int windowSize) {
		this.stopWords =  stopWords;
		this.windowSize = windowSize;
	}

	public RawTextBasedWordCooccurrenceExtraction(ConfigurationParams confParams) throws ConfigurationException, IOException {
		try {
			this.windowSize = confParams.getInt(Configuration.WINDOW_SIZE);
		} catch (ConfigurationException e) {
			this.windowSize = DEFAULT_WINDOW_SIZE;
		}
		this.stopWords =  new HashSet<String>();
		try {
			initStopWords(new File(confParams.get(Configuration.STOP_WORDS_FILE)));			
		} catch (ConfigurationException e) {
			logger.info("no stop-words file is defined");			
		}		
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.cooccurrence.CooccurrenceExtraction#extractCooccurrences(java.lang.Object)
	 */
	@Override
	public Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence<String>>> extractCooccurrences(String sentence) throws CooccurrenceExtractionException {
		List<StringBasedTextUnit> textUnints = new LinkedList<StringBasedTextUnit>();
		List<DefaultCooccurrence<String>> coOccurrences = new LinkedList<DefaultCooccurrence<String>>();
		try {
			String[] words = sentence.split("\\s+");
			for (int i =0; i<words.length; i++) {
				if (!relevantWord(words[i])) {
					StringBasedTextUnit word1 = new StringBasedTextUnit(words[i]);
					for (int j=i+1; j<(i+windowSize) && j < words.length ; j++) {
						if (relevantWord(words[j])) {							
							StringBasedTextUnit word2 = new StringBasedTextUnit(words[j]);
							textUnints.add(word1);
							textUnints.add(word2);
							coOccurrences.add(new DefaultCooccurrence<String>(word1, word2,new DefaultRelation<String>(NEIGHBOUR_REL)));
						}
					}
						
				}
			}
		} catch (Exception e) {			
			throw new CooccurrenceExtractionException(e);
		}
		return new Pair<List<StringBasedTextUnit>,List<DefaultCooccurrence<String>>>(textUnints,coOccurrences);
	}
	

	protected void initStopWords(File file) throws IOException {
		logger.info("reading stop words file: " + file.getAbsolutePath());
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line = reader.readLine())!=null)
			stopWords.add(line.trim());
		logger.info(stopWords.size() +" stop words were read");
		reader.close();		
	}
	
	/**
	 * Check whether the given word should be part of a co-occurrence 
	 * 
	 * @param word
	 * @return true if the given word should be part of a co-occurrence 
	 */
	protected boolean relevantWord(String word) {
		return !stopWords.contains(word) && !isPunctuated(word);
	}

	/**
	 * Check whether the given word contains at least one punctuation character
	 * 
	 * @param word 
	 * @return true if the given word contains at least one punctuation character
	 */
	protected static boolean isPunctuated(String word) {
		return Pattern.matches(".*\\p{Punct}.*", word);
	}
	
	Set<String> stopWords;
	int windowSize;

}

