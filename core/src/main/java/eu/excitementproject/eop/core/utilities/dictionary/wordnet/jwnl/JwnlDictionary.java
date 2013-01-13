package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;

/**
 * Implementation of {@link Dictionary}. The underlying implementation is <a href='http://sourceforge.net/projects/jwordnet/'>JWNL - Java WordNet Library</a>.
 * <p>
 * <b>NOTICE</b> this code does not support <code>null</code> POS args as wildcards. WordNetLexicalResource implements that
 * feature, wrapping this class.
 * <p>
 * <b>WARNING</b> This dictionary will crash in most multiprocess settings!
 * 
 * @author Asher Stern
 *
 */
public class JwnlDictionary implements Dictionary
{
	public static final int NUMBER_OF_NON_LETTER_OR_DIGIT_TO_FILTER_WORD = 5;
	
	private static net.didion.jwnl.dictionary.Dictionary jwnlRealDictionary;
	private static Object initializationSynchronizer = new Object();
		// static Object is preferred to using <class name>.class as
		// synchronization object. The reason is flexibility in class name
		// changing.
	
	JwnlDictionary() throws WordNetInitializationException
	{
		
		// Double checking of static member, to avoid race conditions, while
		// not harming performance.

		if (null==jwnlRealDictionary)
		{
			synchronized(initializationSynchronizer)
			{
				if (null==jwnlRealDictionary)
				{
					try
					{
						jwnlRealDictionary = net.didion.jwnl.dictionary.Dictionary.getInstance();
					}
					catch(Exception e)
					{
						throw new WordNetInitializationException("Construction of: "+this.getClass().getName()+" failed. See nested exception.",e);
					}
				}
			}
		}
	}
	
	net.didion.jwnl.dictionary.Dictionary getJwnlRealDictionary()
	{
		return jwnlRealDictionary;
	}

	//////////////////////////////////////////////////PUBLIC ///////////////////////////////////////////
	
	public void close()
	{
		// Do not close the real dictionary. It is static!
	}

	
	/**
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary#getSynsetOf(java.lang.String)
	 */
	public Map<WordNetPartOfSpeech, Set<Synset>> getSynsetOf(String lemma) throws WordNetException
	{
		Map<WordNetPartOfSpeech, Set<Synset>> ret = new HashMap<WordNetPartOfSpeech, Set<Synset>>();
		if (doNotProcessThisWord(lemma)) ;
		else
		{
			try
			{
				net.didion.jwnl.data.IndexWordSet indexWordSet = jwnlRealDictionary.lookupAllIndexWords(lemma);
				if (indexWordSet!=null)
				{
					for (WordNetPartOfSpeech partOfSpeech : WordNetPartOfSpeech.values())
					{
						net.didion.jwnl.data.POS pos = JwnlUtils.getJwnlPartOfSpeec(partOfSpeech);
						if (indexWordSet.getIndexWord(pos)!=null)
							ret.put(partOfSpeech, indexWordToSet(indexWordSet.getIndexWord(pos)));
					}
				}
				
			}
			catch(JWNLException e)
			{
				throw new WordNetException("looking for lemma <"+lemma+"> failed. See nested exception",e);
			}
		}
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSynsetsOf(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetPartOfSpeech)
	 */
	public Set<Synset> getSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech)  throws WordNetException
	{
		if (doNotProcessThisWord(lemma))
			return new HashSet<Synset>();
		else
		{
			try
			{
				net.didion.jwnl.data.IndexWord indexWord = jwnlRealDictionary.lookupIndexWord(JwnlUtils.getJwnlPartOfSpeec(partOfSpeech), lemma);
				return indexWordToSet(indexWord);
			}
			catch(JWNLException e)
			{
				throw new WordNetException("looking for word <"+lemma+"> with part of speech: "+partOfSpeech.toString()+" failed. See nested exception",e);
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSortedSynsetOf(java.lang.String)
	 */
	public Map<WordNetPartOfSpeech, List<Synset>> getSortedSynsetOf(String lemma) throws WordNetException
	{
		Map<WordNetPartOfSpeech, List<Synset>> ret = new HashMap<WordNetPartOfSpeech, List<Synset>>();
		if (doNotProcessThisWord(lemma)) ;
		else
		{
			try
			{
				net.didion.jwnl.data.IndexWordSet indexWordSet = jwnlRealDictionary.lookupAllIndexWords(lemma);
				if (indexWordSet!=null)
				{
					for (WordNetPartOfSpeech partOfSpeech : WordNetPartOfSpeech.values())
					{
						net.didion.jwnl.data.POS pos = JwnlUtils.getJwnlPartOfSpeec(partOfSpeech);
						if (indexWordSet.getIndexWord(pos)!=null)
							ret.put(partOfSpeech, indexWordToList(indexWordSet.getIndexWord(pos)));
					}
				}

			}
			catch(JWNLException e)
			{
				throw new WordNetException("looking for lemma <"+lemma+"> failed. See nested exception",e);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSortedSynsetsOf(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetPartOfSpeech)
	 */
	public List<Synset> getSortedSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech) throws WordNetException
	{
		if (doNotProcessThisWord(lemma)) return new ArrayList<Synset>();
		else
		{
			try
			{
				net.didion.jwnl.data.IndexWord indexWord = jwnlRealDictionary.lookupIndexWord(JwnlUtils.getJwnlPartOfSpeec(partOfSpeech), lemma);
				return indexWordToList(indexWord);
			}
			catch(JWNLException e)
			{
				throw new WordNetException("looking for word <"+lemma+"> with part of speech: "+partOfSpeech.toString()+" failed. See nested exception",e);
			}
		}
	}
	
	/**
	 * Return all {@link Synset}s of a particular POS
	 * @param pos
	 * @return
	 * @throws WordNetException
	 */
	public List<Synset> getAllWords(WordNetPartOfSpeech pos) throws WordNetException
	{
		List<Synset> synsets = new ArrayList<Synset>();
		try {
			@SuppressWarnings("rawtypes")
			Iterator iter = jwnlRealDictionary.getIndexWordIterator(JwnlUtils.getJwnlPartOfSpeec(pos));
			while (iter.hasNext())
			{
				IndexWord indexWord = (IndexWord) iter.next();
				synsets.addAll(indexWordToList(indexWord));
			}
			
		} catch (JWNLException e) {
			throw new WordNetException("looking for all <"+pos+">s failed. See nested exception",e);
		}
		
		return synsets;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSensedWord(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.Synset)
	 */
	@Override
	public SensedWord getSensedWord(String lemma, Synset synset) throws WordNetException {
		if (!(synset instanceof JwnlSynset))
			throw new WordNetException("Class cast error. You gave JwnlDictionary a Synset of an incompaltible class. Use JwnlSynset instead: " + synset); 
		return new JwnlSensedWord((JwnlSynset) synset, lemma);
	}
	
//////////////////////////////////////////////////PROTECTED ///////////////////////////////////////////

	protected Set<Synset> indexWordToSet(net.didion.jwnl.data.IndexWord indexWord) throws JWNLException
	{
		Set<Synset> ret = new HashSet<Synset>();
		if (indexWord!=null)
		{
			for (net.didion.jwnl.data.Synset jwnlRealSynset : indexWord.getSenses())
			{
				ret.add(new JwnlSynset(this, jwnlRealSynset));
			}
		}
		return ret;
	}
	
	protected List<Synset> indexWordToList(net.didion.jwnl.data.IndexWord indexWord) throws JWNLException
	{
		List<Synset> ret = null;
		if (indexWord!=null)
		{
			if (indexWord.getSenses()!=null)
			{
				ret = new ArrayList<Synset>(indexWord.getSenses().length);
				for (net.didion.jwnl.data.Synset jwnlRealSynset : indexWord.getSenses())
				{
					ret.add(new JwnlSynset(this, jwnlRealSynset));
				}
			}
		}
		if (null==ret)
			ret = new ArrayList<Synset>();
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.jwnl.PartialJwnlDictionary#getIndexWord(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetPartOfSpeech)
	 */
	protected IndexWord getIndexWord(String lemma, WordNetPartOfSpeech partOfSpeech) throws JWNLException {
		return doNotProcessThisWord(lemma) ? null : jwnlRealDictionary.lookupIndexWord(JwnlUtils.getJwnlPartOfSpeec(partOfSpeech), lemma);
	}
	
	/**
	 * A work-around to JWNL problem. If a word contains many spaces, or many
	 * non-letter-digit characters, the JWNL search for that word takes long-long-
	 * long time (it can be hours or days).
	 * This function returns <tt>true</tt> if it seems that searching the
	 * word will take to long time.
	 * 
	 * @param word a word to look up in WordNet.
	 * @return <tt> true </tt> if looking up the given word may take too long time (several
	 * hours or several days).
	 */
	protected boolean doNotProcessThisWord(String word)
	{
		return false;
//		XXX	temporarily commenting this out
		/*boolean ret = true;
		if (word!=null)
		{
			int numberOfNonLetterAndDigit = 0;
			for (int index=0;index<word.length();index++)
			{
				if (!Character.isLetterOrDigit(word.charAt(index)))
					++numberOfNonLetterAndDigit;
			}
			if (numberOfNonLetterAndDigit>=NUMBER_OF_NON_LETTER_OR_DIGIT_TO_FILTER_WORD)
				ret = true;
			else
				ret = false;
		}
		else
		{
			ret = true;
		}
			
		return ret;*/
	}
}
