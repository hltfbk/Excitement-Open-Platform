/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import de.tudarmstadt.ukp.wiktionary.api.Quotation;
import de.tudarmstadt.ukp.wiktionary.api.WikiString;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryEntry;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryException;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionarySense;


/**
 * A wrapper for a JWKTL-version of a wiktionary {@link WordEntry}. It holds all the information for a {@code <lemma, pos>}
 * pair.  
 * <p>
 * Notes:
 * <li> getCategories() returns only the last category in the list you see at the bottom of the matching web page.
 * 
 * @author Amnon Lotan
 * @since 21/06/2011
 * 
 */
public class JwktlEntry implements WiktionaryEntry {
	
	private static final long serialVersionUID = -106924462648425232L;
	private WordEntry realEntry;
	/**
		the senses map. it's important for getAllSenses() that the senses be in order. Hence the TreeMap 
	 */
	private final SortedMap<Integer, JwktlSense> senseMap = new TreeMap<Integer, JwktlSense>();
	private ImmutableList<String> examples;
	private ImmutableList<String> quotations;

	/**
	 * Ctor
	 * @param realEntry
	 * @param glossParser
	 * @throws JwktlException
	 */
	JwktlEntry( WordEntry realEntry, WktGlossParser glossParser) throws JwktlException {
		if (realEntry == null)
			throw new JwktlException("Got null entry");
		this.realEntry = realEntry;
		// fill up the senses map. it's important for getAllSenses() that the senses be inserted in order
		for (int i = 0; i < realEntry.getNumberOfSenses(); i++)
			senseMap.put(new Integer(i), new JwktlSense(this.realEntry, i, glossParser));
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WktEntry#getSense(int)
	 */
	public WiktionarySense getSense(int senseNum) throws WiktionaryException
	{
		if (senseNum < 1)
			throw new WiktionaryException("The sense number must be positive. I got a "+senseNum);
		if (senseNum > senseMap.size())
			throw new WiktionaryException(realEntry.getWord() + " has only " + senseMap.size()+" senses, and you entered #"+senseNum);
		return senseMap.get(new Integer(senseNum));
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktEntry#getAllSenses()
	 */
	public Set<WiktionarySense> getAllSenses() throws WiktionaryException {
		return new HashSet<WiktionarySense>(senseMap.values());
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktEntry#getAllSortedSenses()
	 */
	public List<WiktionarySense> getAllSortedSenses() throws WiktionaryException {
		return new Vector<WiktionarySense>(senseMap.values());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WktEntry#getWord()
	 */
	public String getWord()
	{
		return this.realEntry.getWord();
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WktEntry#getDetailedInformation()
	 */
	public String getDetailedInformation()
	{
		return this.realEntry.getDetailedInformation();
	}
	
	public ImmutableList<String> getCategories()
	{
		return new ImmutableListWrapper<String>(this.realEntry.getCategories());
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktEntry#getPartOfSpeech()
	 */
	public WiktionaryPartOfSpeech getWiktionaryPartOfSpeech() throws WiktionaryException {
		return JwktlUtils.toWiktionaryPartOfSpeech(this.realEntry.getPartOfSpeech());
	}
	
	public ImmutableList<String> getRelatedWords(WiktionaryRelation relation) throws JwktlException {
		return new ImmutableListWrapper<String>( JwktlUtils.cleanRelatedWords(
			 JwktlUtils.wktRelationToJwktlRelaton(relation) != null ? 
					 realEntry.getAllRelatedWords(JwktlUtils.wktRelationToJwktlRelaton(relation)) : new Vector<String>()));
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return realEntry.getInformation();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#getExamples()
	 */
	public ImmutableList<String> getExamples() {
		if (this.examples == null)
		{
			ArrayList<String> tmpExamples = new ArrayList<String>();
			for (WikiString wikiString : realEntry.getAllExamples())
				tmpExamples.add(wikiString.getTextIncludingWikiMarkup());
			examples = new ImmutableListWrapper<String>(tmpExamples);
		}
		return examples;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#getQuotations(ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WiktionaryRelation)
	 */
	public ImmutableList<String> getQuotations() {
		if (this.quotations == null)
		{
			ArrayList<String> tmpQuotations = new ArrayList<String>();
			for (Quotation jwktlQuotation : realEntry.getAllQuotations())
				tmpQuotations.add(jwktlQuotation.toString());
			quotations = new ImmutableListWrapper<String>(tmpQuotations);
		}
		return quotations;	
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#getWords()
	 */
	public ImmutableList<String> getWords() throws JwktlException {
		return getRelatedWords(WiktionaryRelation.SYNONYM);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#getGloss()
	 */
	public String getGloss() throws JwktlException {
		return ""; 	// entries don't have glosses but in their senses
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((realEntry == null) ? 0 : realEntry.hashCode());
		result = prime * result
				+ ((senseMap == null) ? 0 : senseMap.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JwktlEntry other = (JwktlEntry) obj;
		if (realEntry == null) {
			if (other.realEntry != null)
				return false;
		} else if (!realEntry.equals(other.realEntry))
			return false;
		if (senseMap == null) {
			if (other.senseMap != null)
				return false;
		} else if (!senseMap.equals(other.senseMap))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#isEntry()
	 */
	public boolean isEntry() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktEntry#getNumberOfSenses()
	 */
	public int getNumberOfSenses() {
		return realEntry.getNumberOfSenses();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary_v2.wiktionary.WktSense#getSenseNo()
	 */
	@Override
	public int getSenseNo() {
		return 0;		// the sense number of the entry itself is 0
	}
}
