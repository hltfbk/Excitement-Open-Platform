package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;

/**
 * Implementation of {@link Dictionary}. The underlying implementation is <a href='http://projects.csail.mit.edu/jwi/'>JWI (the MIT Java Wordnet Interface)</a>.
 * Requires <code>jars\jwi_2.2.2\edu.mit.jwi_2.2.2_jdk.jar</code>.
 * <p>
 * In addition, this class exports four methods for finding the <b>sisters/cousins of a synset</b>, according to the wordnet hypernym graph.
 * 
 * <p>
 * <b>NOTICE</b> this code does not support interpreting <code>null</code> POS args as wildcards. WordNetLexicalResource can implement that
 * feature, when configured to wrap this class.

 * @author Amnon Lotan
 *
 */
public class JwiDictionary implements Dictionary
{
	protected final IDictionary jwiRealDictionary;
	protected final JwiCousinFinder cousinFinder;
	
	/**
	 * Ctor with the wordnet directory e.g. <code>Data\RESOURCES\WordNet\3.0\dict.wn.orig\</code>
	 * @param wordnetDir
	 * @throws WordNetInitializationException
	 */
	public JwiDictionary(File wordnetDir) throws WordNetInitializationException
	{
		if (wordnetDir == null)
			throw new WordNetInitializationException("got null wordnet dir");
		if (!wordnetDir.exists())
			throw new WordNetInitializationException(wordnetDir + " doesn't exist");
		if (wordnetDir.isFile())
			throw new WordNetInitializationException(wordnetDir + " is a file, not a directory");
			
		jwiRealDictionary = new  edu.mit.jwi.Dictionary(wordnetDir);
		try {
			if (!jwiRealDictionary.open())
				throw new WordNetInitializationException("Error on edu.mit.jwi.Dictionary.open()" );
		} catch (IOException e) {
			throw new WordNetInitializationException("Error on edu.mit.jwi.Dictionary.open()" );
		}
		
		cousinFinder = new JwiCousinFinder(this);
	}
	
	public void close()
	{
		jwiRealDictionary.close();
	}

	
	public void finalize() {
		jwiRealDictionary.close();
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSynsetOf(java.lang.String)
	 */
	public Map<WordNetPartOfSpeech, Set<Synset>> getSynsetOf(String lemma) throws WordNetException
	{
		Map<WordNetPartOfSpeech, Set<Synset>> ret = new HashMap<WordNetPartOfSpeech, Set<Synset>>();
		for (WordNetPartOfSpeech pos : WordNetPartOfSpeech.values())
			ret.put(pos, getSynsetsOf(lemma, pos));
		return ret;
	}

	/**
	 * Returns the number of synsets of given lemma and POS.
	 * @param lemma
	 * @param partOfSpeech
	 * @return
	 * @throws WordNetException
	 * @author Ofer Bronstein
	 * @since June 2014
	 */
	public int getNumberOfSynsets(String lemma, WordNetPartOfSpeech partOfSpeech)  throws WordNetException
	{
		IIndexWord idxWord = jwiRealDictionary.getIndexWord (lemma, JwiUtils.getJwiPartOfSpeec(partOfSpeech));
		if (idxWord==null) {
			return 0;
		}
		return idxWord.getWordIDs().size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSynsetsOf(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetPartOfSpeech)
	 */
	public Set<Synset> getSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech)  throws WordNetException
	{
		Set<Synset> synsets = new HashSet<Synset>();
		for (ISynset jwiRealSynset : getRealSynsets(lemma, partOfSpeech))
			synsets.add(getSynset( jwiRealSynset));
		
		return synsets;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSortedSynsetOf(java.lang.String)
	 */
	public Map<WordNetPartOfSpeech, List<Synset>> getSortedSynsetOf(String lemma) throws WordNetException
	{
		Map<WordNetPartOfSpeech, List<Synset>> ret = new HashMap<WordNetPartOfSpeech, List<Synset>>();
		for (WordNetPartOfSpeech pos : WordNetPartOfSpeech.values())
			ret.put(pos, getSortedSynsetsOf(lemma, pos));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSortedSynsetsOf(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetPartOfSpeech)
	 */
	public List<Synset> getSortedSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech) throws WordNetException
	{
		List<Synset> synsets = new ArrayList<Synset>();
		IIndexWord idxWord = jwiRealDictionary.getIndexWord (lemma, JwiUtils.getJwiPartOfSpeec(partOfSpeech));
		if (idxWord != null)
		{
			List<IWordID> wordIDs = idxWord.getWordIDs();
			for (IWordID wordID :  wordIDs)
			{
				IWord word = jwiRealDictionary.getWord(wordID);
				if (word == null)
					throw new WordNetException("Internal error: got this wordID " + wordID + " from the JWI dictionary, but the dictionary didn't find a word for it");
				ISynset jwiRealSynset = word.getSynset ();
				synsets.add(getSynset(jwiRealSynset));
			}
		}
		
		return synsets;
	}
	
	/**
	 * Get all the cousin synsets of the synsets matching the given <lemma, pos>, according to the <b>strict</b> definition, by which an n-th cousin of a synset A  
	 * is any synset B, such that the shortest hypernym-and-then-hyponym path from A to B passes through a common hypernym H, and the shortest hypernym-graph path 
	 * from A to H, and from B to H, is n steps long.<br>
	 * Also, an nth cousin of a set of synsets
	 * is an nth cousin of at least one of the synsets in the set, and is not an l-th cousin of any set member, where <code>0 < l < n</code>. In other words,
	 * it is a synset, to which the shortest hypernym-and-then-hyponym path from the set consists of n hypernym edges and then n hyponym edges.<br>
	 * <p>
	 * If the input matches no synsets, or if no such cousins exist, an empty set is returned.
	 * <p>
	 * <b>Note on ambiguity!</b> Even with this strict definition, you may see the same lemmas returned for different <code>degree</code>s, because 
	 * the screening is done on synsets, but the lemmas are returned. E.g.
	 *  the verbs <i>dog</i> and <i>haunt</i>: One sense of <i>haunt </i>is a 1-cousin of <i>dog</i>, and another sense of <i>haunt </i>is a 2-cousin of <i>dog</i>.  
	 * 
	 * @param lemma
	 * @param pos
	 * @param degree must be positive
	 * @return
	 * @throws WordNetException 
	 */
	public Set<String> getStrictCousinTerms(String lemma, WordNetPartOfSpeech pos, int degree) throws WordNetException {
		if (degree < 1)
			throw new WordNetException("depth must be positive. I got " + degree);

		Set<String> cousins = cousinFinder.getStrictCousinsForRealSynset(getRealSynsets(lemma, pos), degree);
		return cousins;
	}

	/**
	 * Get all the cousin synsets of the synset matching the given <lemma, pos, #sense>, according to the <b>strict</b> definition, by which an n-th cousin of a synset A  
	 * is any synset B, such that the shortest hypernym-and-then-hyponym path from A to B passes through a common hypernym H, and the shortest hypernym-graph path 
	 * from A to H, and from B to H, is n steps long.<br>
	 * <p>
	 * If the input matches no synsets, or if no such cousins exist, an empty set is returned.
	 * 
	 * @param lemma
	 * @param pos
	 * @param sense
	 * @param degree must be positive
	 * @return
	 * @throws WordNetException 
	 */
	public Set<String> getStrictCousinTerms(String lemma, WordNetPartOfSpeech pos, int sense, int degree) throws WordNetException {
		if (degree < 1)
			throw new WordNetException("depth must be positive. I got " + degree);

		ISynset[] synsetSet = new ISynset[1];
		synsetSet[0] = getRealSynset(lemma,pos ,sense);
		return cousinFinder.getStrictCousinsForRealSynset(synsetSet, degree);
	}
	
	/**
	 * Get all the cousin synsets of the synsets matching the given <lemma, pos>, according to the loose definition, by which an n-th cousin of a synset  
	 * is any synset that 
	 * shares a hypernym, reachable from each by a hypernym path of length n (and possibly by other shorter/longer paths as well). Also, a cousin of a set of synsets
	 * is a cousin of AT LEAST one of the synsets in the set.<br>
	 * <p>
	 * If the input matches no synsets, or if no such cousins exist, an empty set is returned.
	 * 
	 * @param lemma
	 * @param pos
	 * @param degree must be positive
	 * @return
	 * @throws WordNetException 
	 */
	public Set<String> getLooseCousinTerms(String lemma, WordNetPartOfSpeech pos, int degree) throws WordNetException {
		if (degree < 1)
			throw new WordNetException("depth must be positive. I got " + degree);

		Set<String> cousins = cousinFinder.getLooseCousinsForRealSynset(getRealSynsets(lemma, pos), degree);
		return cousins;
	}

	/**
	 * Get all the cousin synsets of the synset matching the given <lemma, pos, #sense>, according to the loose definition, by which an n-th cousin of a synset  
	 * is any synset that 
	 * shares a hypernym, reachable from each by a hypernym path of length n (and possibly by other shorter/longer paths as well). Also, a cousin of a set of synsets
	 * is a cousin of AT LEAST one of the synsets in the set.<br>
	 * <p>
	 * If the input matches no synsets, or if no such cousins exist, an empty set is returned.
	 * 
	 * @param lemma
	 * @param pos
	 * @param sense
	 * @param degree
	 * @return
	 * @throws WordNetException 
	 */
	public Set<String> getLooseCousinTerms(String lemma, WordNetPartOfSpeech pos, int sense, int degree) throws WordNetException {
		if (degree < 1)
			throw new WordNetException("depth must be positive. I got " + degree);

		ISynset[] synsetSet = new ISynset[1];
		synsetSet[0] = getRealSynset(lemma,pos ,sense);
		return cousinFinder.getLooseCousinsForRealSynset(synsetSet, degree);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary#getSensedWord(java.lang.String, ac.biu.nlp.nlp.instruments.dictionary.wordnet.Synset)
	 */
	@Override
	public SensedWord getSensedWord(String lemma, Synset synset) throws WordNetException {
		if (!(synset instanceof JwiSynset))
			throw new WordNetException("Class cast error. You gave ExtJwnlDictionary a Synset of an incompaltible class. Use ExtJwnlSynset instead: " + synset); 
		return new JwiSensedWord((JwiSynset) synset, lemma);
	}

	///////////////////////////////////////////////////////////////////////// package	////////////////////////////////////////////////////////
	
	Set<Synset> getSetOfSynsets(List<ISynsetID> synsetIds)
	{
		if (null==synsetIds)
			return new HashSet<Synset>();
		
		Set<Synset> ret = new HashSet<Synset>();
		for(ISynsetID sid : synsetIds)
			ret.add(getSynset(jwiRealDictionary.getSynset(sid)));
		return ret;
	}
	
	IDictionary getJwiRealDictionary()
	{
		return jwiRealDictionary;
	}

	/**
	 * @param relatedWords
	 * @return
	 */
	Set<Synset> getSetOfSynsetsOfWords(List<IWordID> wordIDs) {
		Set<Synset> synsets = new HashSet<Synset>(wordIDs.size());
		// every IWordID has one synset
		for ( IWordID wordID : wordIDs)
			synsets.add(getSynset(wordID));
		return synsets ;
	}
	
	/**
	 * @param relatedWordID
	 * @return
	 */
	JwiSynset getSynset(IWordID wordID) {
		return (JwiSynset) getSynset(jwiRealDictionary.getWord(wordID).getSynset());
	}
	
	/**
	 * @param iSynset
	 * @return
	 */
	Synset getSynset(ISynset iSynset) {
		return new JwiSynset(this, iSynset);
	}
	
	///////////////////////////////////////////////////////////////////////// protected	////////////////////////////////////////////////////////

	/**
	 * @param lemma
	 * @param pos
	 * @param sense
	 * @return
	 * @throws WordNetException 
	 */
	protected ISynset getRealSynset(String lemma,	WordNetPartOfSpeech pos, int sense) throws WordNetException {
		if (sense <= 0)
			throw new WordNetException("sense numbers begin at 1, I got "+ sense);
		ISynset[] synsets = getRealSynsets(lemma, pos);
		if (synsets == null)
			return null;
		if (sense > synsets.length)
			throw new WordNetException("You requested a sense that doesn't exist. <"+lemma+", "+pos+"> only has "+synsets.length+" " +
					"synsets, but you asked for #"+sense);
		return synsets[sense-1];
	}

	/**
	 * @param lemma
	 * @param pos
	 * @return
	 * @throws WordNetException 
	 */
	protected ISynset[] getRealSynsets(String lemma, WordNetPartOfSpeech pos) throws WordNetException  {
		IIndexWord idxWord = jwiRealDictionary.getIndexWord (lemma, JwiUtils.getJwiPartOfSpeec(pos));
		ISynset[] realSynsets ;
		if (idxWord != null)
		{
			List<IWordID> wordIDs = idxWord.getWordIDs();
			realSynsets = new ISynset[wordIDs.size()];
			int i = 0;
			for (IWordID wordID :  wordIDs)
			{
				IWord iWord = jwiRealDictionary.getWord(wordID);
				if (iWord == null)
					throw new WordNetException("Internal error: got this wordID " + wordID + " from the JWI dictionary, but the dictionary didn't find a word for it");
				ISynset jwiRealSynset = iWord.getSynset ();
				realSynsets[i++] = jwiRealSynset;
			}
		}
		else	// there is no synset matching <lemma, pos>
			realSynsets = new ISynset[0];
		return realSynsets;
	}
}
