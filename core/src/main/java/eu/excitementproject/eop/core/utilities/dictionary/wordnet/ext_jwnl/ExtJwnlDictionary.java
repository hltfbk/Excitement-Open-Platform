/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;


/**
 * Implementation of {@link WordnetDictionary}. The underlying implementation is <a href='http://sourceforge.net/projects/extjwnl/'>ExtJWNL - Extended 
 * Java WordNet Library</a>.<br>
 * This class inherits some of the method implementations from {@link PartialExtJwnlDictionary}.
 * Its partial for clarity, so we can put half of the method implementations in the other class.<br>
 * 
 * <b>NOTICE</b> this code does not support <code>null</code> POS args as wildcards. WordNetLexicalResource implements that
 * feature, wrapping this class.
 * <p>
 * <b>NOTE</b> ExtJwnl <i>lowercases</i> the words it is queried about.
 * 
 * @author Amnon Lotan
 * @since 20/06/2011
 * 
 */
public class ExtJwnlDictionary implements eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary {

	public static final int NUMBER_OF_NON_LETTER_OR_DIGIT_TO_FILTER_WORD = 5;
	
	private final Dictionary extJwnlRealDictionary;
	
	/**
	 * Ctor
	 * @throws WordNetInitializationException 
	 */
	public ExtJwnlDictionary(String wnDir) throws WordNetInitializationException
	{
		this(new File(wnDir));
		
	}
	
	/**
	 * Ctor
	 * @param wnDictionaryDir
	 * @throws WordNetInitializationException 
	 */
	public ExtJwnlDictionary(File wnDictionaryDir) throws WordNetInitializationException {
		if (wnDictionaryDir == null)
			throw new WordNetInitializationException("null WN directory");
		if (!wnDictionaryDir.exists())
			throw new WordNetInitializationException("given WN directory " + wnDictionaryDir + " doesn't exist");
		
		File propsFile = null;
		try {
			propsFile = ExtJwnlDictionaryInitializer.makePropsFile(wnDictionaryDir);
			this.extJwnlRealDictionary = Dictionary.getInstance(new FileInputStream(propsFile));		
		} catch (FileNotFoundException e) {
			throw new WordNetInitializationException("props file " + propsFile + " not found", e);
		} catch (net.sf.extjwnl.JWNLException e) {
			throw new WordNetInitializationException("error instantiating a Dictionary with " + propsFile , e);
		}
	}

	Dictionary getJwnlRealDictionary()
	{
		return extJwnlRealDictionary;
	}
	
////////////////////////////////////////////////// PUBLIC ///////////////////////////////////////////
	
	public void close()
	{
		this.extJwnlRealDictionary.close();
	}

	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSensesOf(java.lang.String)
	 */
	public Map<WordNetPartOfSpeech, Set<Synset>> getSynsetOf(String lemma) throws WordNetException
	{
		Map<WordNetPartOfSpeech, Set<Synset>> ret = new LinkedHashMap<WordNetPartOfSpeech, Set<Synset>>();
		if (doNotProcessThisWord(lemma)) ;
		else
		{
			try
			{
				IndexWordSet indexWordSet = extJwnlRealDictionary.lookupAllIndexWords(lemma);
				if (indexWordSet!=null)
				{
					for (WordNetPartOfSpeech partOfSpeech : WordNetPartOfSpeech.values())
					{
						POS pos = ExtJwnlUtils.getJwnlPartOfSpeec(partOfSpeech);
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
			return new LinkedHashSet<Synset>();
		else
		{
			try
			{
				IndexWord indexWord = extJwnlRealDictionary.lookupIndexWord(ExtJwnlUtils.getJwnlPartOfSpeec(partOfSpeech), lemma);
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
		Map<WordNetPartOfSpeech, List<Synset>> ret = new LinkedHashMap<WordNetPartOfSpeech, List<Synset>>();
		if (doNotProcessThisWord(lemma)) ;
		else
		{
			try
			{
				IndexWordSet indexWordSet = extJwnlRealDictionary.lookupAllIndexWords(lemma);
				if (indexWordSet!=null)
				{
					for (WordNetPartOfSpeech partOfSpeech : WordNetPartOfSpeech.values())
					{
						POS pos = ExtJwnlUtils.getJwnlPartOfSpeec(partOfSpeech);
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
		try
		{
			IndexWord indexWord = getIndexWord(lemma, partOfSpeech);
			return indexWord != null ? indexWordToList(indexWord) : new ArrayList<Synset>();
		}
		catch(JWNLException e)
		{
			throw new WordNetException("looking for word <"+lemma+"> with part of speech: "+partOfSpeech.toString()+" failed. See nested exception",e);
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
			Iterator iter = extJwnlRealDictionary.getIndexWordIterator(ExtJwnlUtils.getJwnlPartOfSpeec(pos));
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
		if (!(synset instanceof ExtJwnlSynset))
			throw new WordNetException("Class cast error. You gave ExtJwnlDictionary a Synset of an incompaltible class. Use ExtJwnlSynset instead: " + synset); 
		return new ExtJwnlSensedWord((ExtJwnlSynset) synset, lemma);
	}
	
	////////////////////////////////////////////////// PROTECTED ///////////////////////////////////////////
	
	protected Set<Synset> indexWordToSet(IndexWord indexWord) throws JWNLException
	{
		Set<Synset> ret = new LinkedHashSet<Synset>();
		if (indexWord!=null)
		{
			for (net.sf.extjwnl.data.Synset jwnlRealSynset : indexWord.getSenses())
			{
				ret.add(new ExtJwnlSynset(this, jwnlRealSynset));
			}
		}
		return ret;
	}
	
	protected List<Synset> indexWordToList(IndexWord indexWord) throws JWNLException
	{
		List<Synset> ret = null;
		if (indexWord!=null)
		{
			if (indexWord.getSenses()!=null)
			{
				ret = new ArrayList<Synset>(indexWord.getSenses().size());
				for (net.sf.extjwnl.data.Synset jwnlRealSynset : indexWord.getSenses())
				{
					ret.add(new ExtJwnlSynset(this, jwnlRealSynset));
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
		return doNotProcessThisWord(lemma) ? null : extJwnlRealDictionary.lookupIndexWord(ExtJwnlUtils.getJwnlPartOfSpeec(partOfSpeech), lemma);
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
//	XXX	temporarily commenting this out
//		boolean ret = true;
//		if (word!=null)
//		{
//			int numberOfNonLetterAndDigit = 0;
//			for (int index=0;index<word.length();index++)
//			{
//				if (!Character.isLetterOrDigit(word.charAt(index)))
//					++numberOfNonLetterAndDigit;
//			}
//			if (numberOfNonLetterAndDigit>=NUMBER_OF_NON_LETTER_OR_DIGIT_TO_FILTER_WORD)
//				ret = true;
//			else
//				ret = false;
//		}
//		else
//		{
//			ret = true;
//		}
//			
//		return ret;
	}
}
