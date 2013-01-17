package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jmwn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.itc.mwn.IndexWord;
import org.itc.mwn.IndexWordSet;
import org.itc.mwn.MysqlDictionary;
import org.itc.mwn.POS;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;


/**
 * 
 * Implementation of {@link Dictionary} for Multi WordNet. It interfaces with the JMWN library.  
 * 
 * 
 * @author nastase
 *
 */

@SuppressWarnings("unused")
public class JmwnDictionary implements Dictionary{
	
	private static MysqlDictionary jmwnRealDictionary;
	private String language = "italian";
	
	JmwnDictionary() throws WordNetInitializationException {
	  if (null==jmwnRealDictionary){
		  try {
			  jmwnRealDictionary = new MysqlDictionary();
		  }
		  catch(Exception e) {
				throw new WordNetInitializationException("Construction of: "+this.getClass().getName()+" failed. See nested exception.",e);
		  }
	  }
	}
	
	
	JmwnDictionary(String language) throws WordNetInitializationException {
		  if (null==jmwnRealDictionary){
			  try {
				  jmwnRealDictionary = new MysqlDictionary();
			  }
			  catch(Exception e) {
					throw new WordNetInitializationException("Construction of: "+this.getClass().getName()+" failed. See nested exception.",e);
			  }
		  }
		this.language = language;
	}
	
	
	JmwnDictionary(File configFile) throws WordNetInitializationException {
	  if (null==jmwnRealDictionary){
		  try {
			  jmwnRealDictionary = new MysqlDictionary(configFile);
		  }
		  catch(Exception e) {
				throw new WordNetInitializationException("Construction of: "+this.getClass().getName()+" failed. See nested exception.",e);
		  }
	  }
	}
		
		
	JmwnDictionary(File configFile, String language) throws WordNetInitializationException {
		  if (null==jmwnRealDictionary){
			  try {
				  jmwnRealDictionary = new MysqlDictionary(configFile);
			  }
			  catch(Exception e) {
					throw new WordNetInitializationException("Construction of: "+this.getClass().getName()+" failed. See nested exception.",e);
			  }
		  }
		this.language = language;
	}	
	
	
	MysqlDictionary getJmwnRealDictionary() {
		return jmwnRealDictionary;
	}

	public void close() {
		// no closing
	}

	public SensedWord getSensedWord(String lemma, Synset synset) throws WordNetException {
		if (!(synset instanceof JmwnSynset)) 
			throw new WordNetException("Class cast error. You gave JmwnDictionary a Synset of an incompatible class." + synset.getClass() + " Use JmwnSynset : " + synset);
		return new JmwnSensedWord((JmwnSynset) synset, lemma);
	}

	public Map<WordNetPartOfSpeech, List<Synset>> getSortedSynsetOf(String lemma) throws WordNetException {
		Map<WordNetPartOfSpeech, List<Synset>> ret = new HashMap<WordNetPartOfSpeech, List<Synset>>();
		if (doNotProcessThisWord(lemma)) ;
		else
		{
			try
			{
				org.itc.mwn.IndexWordSet indexWordSet = jmwnRealDictionary.lookupAllIndexWords(lemma,language);
				if (indexWordSet!=null)
				{
					for (WordNetPartOfSpeech partOfSpeech : WordNetPartOfSpeech.values())
					{
						org.itc.mwn.POS pos = JmwnUtils.getJmwnPartOfSpeech(partOfSpeech);
						if (indexWordSet.getIndexWord(pos)!=null)
							ret.put(partOfSpeech, indexWordToList(indexWordSet.getIndexWord(pos)));
					}
				}

			}
			catch(Exception e)
			{
				throw new WordNetException("looking for lemma <"+lemma+"> failed. See nested exception",e);
			}
		}
		return ret;
	}

	public List<Synset> getSortedSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech) throws WordNetException {
//		if (doNotProcessThisWord(lemma)) return new ArrayList<Synset>();
//		else
		{
			try
			{
//				System.out.println("Checking out lemma :" + lemma + "/" + language + " -- in JMWN");
				org.itc.mwn.IndexWord indexWord = jmwnRealDictionary.lookupIndexWord(JmwnUtils.getJmwnPartOfSpeech(partOfSpeech), lemma, language);
				return indexWordToList(indexWord);
			}
			catch(Exception e)
			{
				throw new WordNetException("looking for word <"+lemma+"> with part of speech: "+partOfSpeech.toString()+" failed. See nested exception",e);
			}
		}
	}

	public Map<WordNetPartOfSpeech, Set<Synset>> getSynsetOf(String lemma) throws WordNetException {

		Map<WordNetPartOfSpeech, Set<Synset>> ret = new HashMap<WordNetPartOfSpeech, Set<Synset>>();
		if (doNotProcessThisWord(lemma)) ;
		else
		{
			try
			{
			  IndexWordSet indexWordSet = jmwnRealDictionary.lookupAllIndexWords(lemma, language);
			  if (indexWordSet != null) {
				  for (org.itc.mwn.POS pos : POS.CATS) {
					  if (indexWordSet.getIndexWord(pos) != null)
						  ret.put(JmwnUtils.getWordNetPartOfSpeech(pos), indexWordToSet(indexWordSet.getIndexWord(pos)));
				  }
			  }
			} catch (Exception e) {
				throw new WordNetException("looking for lemma <" + lemma + "> failed. See nested exception", e);
			}
		}
		
		return ret;
	}

	
	public Set<Synset> getSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech) throws WordNetException {
		if (doNotProcessThisWord(lemma))
			return new HashSet<Synset>();
		else {
			try {
				org.itc.mwn.IndexWord indexWord = jmwnRealDictionary.lookupIndexWord(JmwnUtils.getJmwnPartOfSpeech(partOfSpeech), lemma, language);
				return indexWordToSet(indexWord);
			} catch(Exception e) {
				throw new WordNetException("looking for word <"+lemma+"> with part of speech: "+partOfSpeech.toString()+" failed. See nested exception",e);
			}
		}
	}
	
	
	
	protected Set<Synset> indexWordToSet(org.itc.mwn.IndexWord indexWord) throws WordNetException
	{
		Set<Synset> ret = new HashSet<Synset>();
		if (indexWord!=null)
		{
//			for (org.itc.mwn.Synset jmwnRealSynset : indexWord.getSenses())
			org.itc.mwn.Synset[] senses = indexWord.getSenses();
			if (senses != null) {
				for( int i = 0; i < senses.length; i++){
					ret.add(new JmwnSynset(this, senses[i]));
				}
			} 
		}
		return ret;
	}
	

	protected List<Synset> indexWordToList(org.itc.mwn.IndexWord indexWord) throws WordNetException
	{
//		System.out.println("\tprocessing index word: " + indexWord.toString());
		List<Synset> ret = null;
		if (indexWord!=null)
		{
			if (indexWord.getSenses()!=null)
			{
//				System.out.println("\t\thas " + indexWord.getSenses().length + " senses ");
				ret = new ArrayList<Synset>(indexWord.getSenses().length);
				for (org.itc.mwn.Synset jmwnRealSynset : indexWord.getSenses())
				{
					ret.add(new JmwnSynset(this, jmwnRealSynset));
				}
			}
		}
		if (null==ret)
			ret = new ArrayList<Synset>();
		
		return ret;
	}
	
	// added to parallel the JwnlDictionary implementation.
	protected boolean doNotProcessThisWord(String word)
	{
		if (word.matches("GAP!")) 
			return true;
		return false;
		// filter out words containing numbers or strange characters
	}

}
