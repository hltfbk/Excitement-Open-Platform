/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi;
import java.util.HashSet;
import java.util.Set;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;

/**
 * This class calculates cousin synset relations, synsets that share a common hypernym. It implements
 * {@link JwiDictionary#getLooseCousinTerms(String, eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech, int)}, 
 * {@link JwiDictionary#getLooseCousinTerms(String, eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech, int, int)},
 * {@link JwiDictionary#getStrictCousinTerms(String, eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech, int)} , and
 * {@link JwiDictionary#getStrictCousinTerms(String, eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech, int, int)}
 * <p>
 * see the documentation of those methods to learn about this class.
 * @author Amnon Lotan
 *
 * @since Dec 3, 2011
 */
public class JwiCousinFinder {

	///////////////////////////////////////////////////////////////// PACKAGE VISIBLE	/////////////////////////////////////////////////////////////////
	
	/**
	 * Ctor
	 * @param jwiDictionary
	 */
	JwiCousinFinder(JwiDictionary jwiDictionary) {
		super();
		this.jwiDictionary = jwiDictionary;
	}
	

	/**
	 * @param iSynsets
	 * @param degree
	 * @return
	 * @throws WordNetException 
	 */
	Set<String> getStrictCousinsForRealSynset( ISynset[] iSynsets, int degree) throws WordNetException {
		Set<String> cousinLemmas = new HashSet<String>();
		
		if (iSynsets != null && iSynsets.length > 0)
		{
			POS pos = iSynsets[0].getPOS();
			if (pos.equals(POS.NOUN) || pos.equals(POS.VERB))	// only nouns and verbs have hypernyms
			{
				Set<ISynsetID> synsetIDs = new HashSet<ISynsetID>();
				for (ISynset synset : iSynsets)
					synsetIDs.add(synset.getID());
						
				Set<ISynsetID> remoteHypernymIDs = findRelatedSynsetsAtExactDistance(synsetIDs, Pointer.HYPERNYM, degree);
				Set<ISynsetID> remoteCousinIDs = findRelatedSynsetsAtExactDistance(remoteHypernymIDs, Pointer.HYPONYM, degree);
				remoteCousinIDs.removeAll(synsetIDs);			// the initial synsets are not their own cousins
				
				// screen out the cousins that are reachable by climbing and sliding less than 'degree' steps, and remain with those cousins reachable only by climbing
				// and sliding 'degree' steps.
				remoteCousinIDs.removeAll(getLooseCousinsForRealSynset(iSynsets, degree-1));	 
				
				// collect the lemmas
				cousinLemmas = getWordsOfSynsetIDs(remoteCousinIDs);
				
				// screen out the original synset's lemmas
				Set<String> originalSynsetLemmas = getWordsOfSynsets(iSynsets);
				cousinLemmas.removeAll(originalSynsetLemmas);		
			}
		}
		return cousinLemmas;
	}
	

	/**
	 * Get all the cousin synsets of the given iSynsets, according to the loose definition, by which a cousin of a synset to the n-th degree is any synset that 
	 * shares a hypernym, reachable by a hypernym path of length n (and possibly by other shorter and/or longer paths as well). Also, a cousin of a set of synsets
	 * is a cousin of AT LEAST one of the synsets in the set.
	 *  
	 * @param iSynsets
	 * @param degree
	 * @return
	 */
	Set<String>  getLooseCousinsForRealSynset(ISynset[] iSynsets, int degree) {
		Set<String> cousinLemmas = new HashSet<String>();
		
		if (iSynsets != null && iSynsets.length > 0)
		{
			POS pos = iSynsets[0].getPOS();
			if (pos.equals(POS.NOUN) || pos.equals(POS.VERB))	// only nouns and verbs have hypernyms
			{
				Set<ISynsetID> remoteCousinSynsetIDs = computeRemoteCousinSynsetIDs(iSynsets, degree);
				// collect the lemmas
				cousinLemmas = getWordsOfSynsetIDs(remoteCousinSynsetIDs);
				
				// screen out the original synset's lemmas
				Set<String> originalSynsetLemmas = getWordsOfSynsets(iSynsets);
				cousinLemmas.removeAll(originalSynsetLemmas);		
			}
		}
		return cousinLemmas;
	}

	/**
	 * get the strict cousin synsets at the given degree
	 * @param iSynset
	 * @param degree
	 * @return
	 */
	Set<Synset> getStrictCousinSynsets(ISynset iSynset, int degree) {
		Set<ISynsetID> remoteCousinSynsetIDs = computeRemoteCousinSynsetIDs(new ISynset[]{iSynset}, degree);
		ISynset[] cousinISynsets = getISynsets(remoteCousinSynsetIDs);
		Set<Synset> cousinSynsets = new HashSet<Synset>();
		for (ISynset iSynset2 : cousinISynsets)
			cousinSynsets.add(this.jwiDictionary.getSynset(iSynset2));
			
		return cousinSynsets;
	}
	
	//////////////////////////////////////////////////////////////////////////// PRIVATE	////////////////////////////////////////////////////////////////
	
	/**
	 * @param iSynsets
	 * @param degree
	 * @return
	 */
	private Set<ISynsetID> computeRemoteCousinSynsetIDs(ISynset[] iSynsets,	int degree) {
		
		Set<ISynsetID> synsetIDs = new HashSet<ISynsetID>();
		for (ISynset synset : iSynsets)
			synsetIDs.add(synset.getID());
				
				Set<ISynsetID> remoteHypernymIDs = findRelatedSynsetsAtLooseDistance(synsetIDs, Pointer.HYPERNYM, degree);
				Set<ISynsetID> remoteCousinSynsetIDs = findRelatedSynsetsAtLooseDistance(remoteHypernymIDs, Pointer.HYPONYM, degree);
				remoteCousinSynsetIDs.removeAll(synsetIDs);			// the initial synsets are not their own cousins
				
				return remoteCousinSynsetIDs;
	}

	/**
	 * @param iSynsetIDs
	 * @return
	 */
	private Set<String> getWordsOfSynsetIDs(Set<ISynsetID> iSynsetIDs) {
		ISynset[] synsets = getISynsets(iSynsetIDs);
		return getWordsOfSynsets(synsets);

	}

	/**
	 * @param iSynsetIDs
	 * @return
	 */
	private ISynset[] getISynsets(Set<ISynsetID> iSynsetIDs) {
		ISynset[] synsets = new ISynset[iSynsetIDs.size()];
		int i = 0;
		for (ISynsetID synsetID : iSynsetIDs)
			synsets[i++] = jwiDictionary.jwiRealDictionary.getSynset(synsetID);
		return synsets;
	}


	/**
	 * @param iSynsets
	 * @return
	 */
	private Set<String> getWordsOfSynsets(ISynset[] iSynsets) {
		Set<String> words = new HashSet<String>();
		for (ISynset synset : iSynsets)
			for (IWord iWord : synset.getWords())
				words.add(iWord.getLemma());
		return words;
	}

	/**
	 * get all the synsets that are at the end of a 'relation' type path of length 'degree' from any one of the given synsetIDs. We don't care that these paths
	 * be minimal
	 * @param remoteHypernymIDs
	 * @param relation must be a transitive relation like hypernym or hyponym
	 * @param degree
	 * @return
	 */
	private Set<ISynsetID> findRelatedSynsetsAtLooseDistance(	Set<ISynsetID> remoteHypernymIDs, Pointer relation, int degree) {
		Set<ISynsetID> neighborIDs = remoteHypernymIDs; 
		for(int depth = 0; depth < degree; depth++)
		{
			Set<ISynsetID> secondaryNeighborIDs = new HashSet<ISynsetID>();
			for (ISynsetID neighborID : neighborIDs)
			{
				ISynset neighbor = jwiDictionary.jwiRealDictionary.getSynset(neighborID);
				for (ISynsetID secondaryNeighborID : neighbor.getRelatedSynsets(relation))
					secondaryNeighborIDs.add(secondaryNeighborID);
			}
			neighborIDs = secondaryNeighborIDs;
		}
		return neighborIDs;
	}

	/**
	 * @param relation must be a transitive relation like hypernym or hyponym
	 * @param degree
	 * @param initialSynsetIDs 
	 * @return
	 */
	private Set<ISynsetID> findRelatedSynsetsAtExactDistance(	Set<ISynsetID> initialSynsetIDs, Pointer relation, int degree) {
		Set<ISynsetID> neighborIDs = new HashSet<ISynsetID>(initialSynsetIDs); 
		Set<ISynsetID> visitedIDs = new HashSet<ISynsetID>(initialSynsetIDs);;
		for(int depth = 0; depth < degree; depth++)
		{
			Set<ISynsetID> secondaryNeighborIDs = new HashSet<ISynsetID>();
			for (ISynsetID neighborID : neighborIDs)
			{
				ISynset neighbor = jwiDictionary.jwiRealDictionary.getSynset(neighborID);
				for (ISynsetID secondaryNeighborID : neighbor.getRelatedSynsets(relation))
					// if we haven't visited this hypernym yet, add it 
					if (!visitedIDs.contains(secondaryNeighborID))
					{
						secondaryNeighborIDs.add(secondaryNeighborID);
						visitedIDs.add(secondaryNeighborID);
					}
			}
			neighborIDs = secondaryNeighborIDs;
		}
		return neighborIDs;
	}

	private final JwiDictionary jwiDictionary;
}
