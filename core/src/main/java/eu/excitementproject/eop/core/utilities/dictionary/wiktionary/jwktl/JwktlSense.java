/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tudarmstadt.ukp.wiktionary.api.Quotation;
import de.tudarmstadt.ukp.wiktionary.api.WikiString;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryException;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionarySense;

/**
 * Represents one of the senses of a wiktionary JWKTL {@link WordEntry}, a specific sense of a lemma+POS pair.
 * <p>
 * information available per sense: getAssignedExamples(), getAssignedQuotations(int sense) , getAssignedRelatedWords(RelationType type, int sense), 
 * getAssignedTranslations(int sense) , getAssignedTranslations(Language lang, int sense) ,getGloss(int sense) 
 * <p>
 * Notes:
 * <li>The synonyms sometimes carry Wikisaurus links, but it seems there's no handy tool for querying it
 * <li>The listed categories are hypernyms, though maybe inaccurate, because they're common to all senses and entries of a word
 * <li>{@link #getRelatedWords(WiktionaryRelation)} pulls both words specific to this sense and this entry
 * @author Amnon Lotan
 * @since 21/06/2011
 * 
 */
public class JwktlSense implements WiktionarySense {
	
	private static final long serialVersionUID = -3584045401776370716L;

	private final WordEntry realEntry;
	private final int senseNum;
	private ImmutableList<String> examples;
	private ImmutableList<String> quotations;
	private final WktGlossParser glossParser;
	private ImmutableList<String> synoynms;	

	/**
	 * Ctor
	 * @throws JwktlException 
	 */
	JwktlSense(WordEntry realEntry, int senseNum, WktGlossParser glossParser) throws JwktlException {
		if (realEntry == null)
			throw new JwktlException("Got null realEntry");
		if (senseNum < 0 || senseNum >= realEntry.getNumberOfSenses())
			throw new JwktlException(realEntry.getWord() + " has only " + realEntry.getNumberOfSenses()+" senses, and you entered #"+senseNum);
		if (glossParser == null)
			throw new JwktlException("Got null WktGlossParser object");
		this.realEntry = realEntry;
		this.senseNum = senseNum;
		this.glossParser = glossParser;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WktSense#getExamples()
	 */
	public ImmutableList<String> getExamples()
	{
		if (this.examples == null)
		{
			ArrayList<String> tmpExamples = new ArrayList<String>();
			for (WikiString wikiString : realEntry.getAssignedExamples(senseNum))
				tmpExamples.add(wikiString.getTextIncludingWikiMarkup());
			examples = new ImmutableListWrapper<String>(tmpExamples);
		}
		return examples;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WktSense#getRelatedWordsList(ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WiktionaryRelation)
	 */
	public ImmutableList<String> getRelatedWords(WiktionaryRelation relation) throws JwktlException
	{
		List<String> wordsList = new LinkedList<String>();
		if (JwktlUtils.wktRelationToJwktlRelaton(relation)!= null)
			wordsList.addAll(realEntry.getAssignedRelatedWords(JwktlUtils.wktRelationToJwktlRelaton(relation), senseNum));
		
		// add more words by specialized methods
		
		// Asher: December 20, 2012 - Replace the switch by if
		if (WiktionaryRelation.GLOSS_TERMS.equals(relation))
		{
			List<String> glossTerms;
			try {
				glossTerms = glossParser.parseGloss(realEntry.getWord(), getWiktionaryPartOfSpeech().toPartOfSpeech(), realEntry.getGloss(senseNum).getPlainText());
			} catch (WiktionaryException e) {
				throw new JwktlException("", e);
			}
			wordsList.addAll(glossTerms);
		}
		
//		switch (relation)
//		{
//		case GLOSS_TERMS:		
//			List<String> glossTerms;
//			try {
//				glossTerms = glossParser.parseGloss(realEntry.getWord(), getWiktionaryPartOfSpeech().toPartOfSpeech(), realEntry.getGloss(senseNum).getPlainText());
//			} catch (WiktionaryException e) {
//				throw new JwktlException("", e);
//			}
//			wordsList.addAll(glossTerms);
//			break;
//		}
		
		return new ImmutableListWrapper<String>( JwktlUtils.cleanRelatedWords(wordsList));
	}
	
	public ImmutableList<String> getQuotations()
	{
		if (this.quotations == null)
		{
			ArrayList<String> quotations_tmp = new ArrayList<String>();
			for (Quotation jwktlQuotation : realEntry.getAssignedQuotations(senseNum))
				quotations_tmp.add(jwktlQuotation.toString());
			quotations = new ImmutableListWrapper<String>(quotations_tmp);
		}
		return quotations;
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.Sense#getPartOfSpeech()
	 */
	public WiktionaryPartOfSpeech getWiktionaryPartOfSpeech() throws JwktlException {
		return JwktlUtils.toWiktionaryPartOfSpeech(realEntry.getPartOfSpeech());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.Sense#getWords()
	 */
	public ImmutableList<String> getWords() throws JwktlException {
		if (synoynms == null)
		{
			synoynms = getRelatedWords(WiktionaryRelation.SYNONYM);
		}
		return synoynms;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#getGloss()
	 */
	public String getGloss() throws JwktlException {
		return realEntry.getGloss(senseNum).getPlainText();
	}	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.WktEntry#getWord()
	 */
	public String getWord()
	{
		return this.realEntry.getWord();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + realEntry.getInformation() + " sense #" + senseNum + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((examples == null) ? 0 : examples.hashCode());
		result = prime * result
				+ ((quotations == null) ? 0 : quotations.hashCode());
		result = prime * result
				+ ((realEntry == null) ? 0 : realEntry.hashCode());
		result = prime * result + senseNum;
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
		JwktlSense other = (JwktlSense) obj;
		if (examples == null) {
			if (other.examples != null)
				return false;
		} else if (!examples.equals(other.examples))
			return false;
		if (quotations == null) {
			if (other.quotations != null)
				return false;
		} else if (!quotations.equals(other.quotations))
			return false;
		if (realEntry == null) {
			if (other.realEntry != null)
				return false;
		} else if (!realEntry.equals(other.realEntry))
			return false;
		if (senseNum != other.senseNum)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktSense#isEntry()
	 */
	public boolean isEntry() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary_v2.wiktionary.WktSense#getSenseNo()
	 */
	@Override
	public int getSenseNo() {
		return senseNum;
	}
}
