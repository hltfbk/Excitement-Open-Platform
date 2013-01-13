/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import de.tudarmstadt.ukp.wiktionary.api.Language;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryDictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryEntry;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryException;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionarySense;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;


/**
 * A {@link WiktionaryDictionary} implementation using Basically an elaborate wrapper for {@link Wiktionary} using JWKTL 
 * (http://elara.tk.informatik.tu-darmstadt.de/Publications/2008/lrec08_camera_ready.pdf).  
 * <P>
 * Prerequs:<br>
 * <li><b>The <code>jwktl</code> jar, which requires Java6, and will not run on Java 5!</b><br>
 * <li>Jar of Oracle Berkeley DB Java Edition 4.0.92 or higher from http://www.oracle.com/technology/software/products/berkeley-db/je/index.html
 * <li>If you might use {@link WiktionarySense#getRelatedWords(WiktionaryRelation)} to get hypernyms, then you must also have {@link EasyFirstParser} running!
 *  
 * 
 * @author Amnon Lotan
 * @since Jun 22, 2011
 * @see {@link  http://elara.tk.informatik.tu-darmstadt.de/Publications/2008/lrec08_camera_ready.pdf}
 * @version jwktl-0.14.1
 * 
 */
public class JwktlDictionary implements WiktionaryDictionary 
{
	private final Wiktionary wiktionary;
	private final WktGlossParser glossParser;

	/**
	 * Ctor
	 * create new Wiktionary object using the parsed data dump directory. See class comment.
	 * @param wiktionaryDir  e.g. "\\\\nlp-srv/data/RESOURCES/Wiktionary/parse"
	 * @param posTaggerModelFile e.g. "b:/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger" 
	 * @throws JwktlException 
	 */
	public JwktlDictionary(String wiktionaryDir, String posTaggerModelFile) throws JwktlException {
		if (wiktionaryDir == null)
			throw new JwktlException("Got null wiktionaryDir");
		if (posTaggerModelFile == null)
			throw new JwktlException("Got null posTaggerModelFile");
		
		Wiktionary _wiktionary;
		try {	_wiktionary = new Wiktionary(wiktionaryDir);	}
		catch (NoClassDefFoundError e) {	throw new JwktlException("Could not constructrt a new Wiktionary obj with the given directory path: "+ wiktionaryDir
				+ ". You are probably missing je-4.1.10.jar in the classpath.", e);	}
		
		wiktionary = _wiktionary;
		wiktionary.setAllowedWordLanguage(Language.ENGLISH);
		wiktionary.setAllowedEntryLanguage(Language.ENGLISH);
		wiktionary.setIsCaseSensitive(true);
		
		glossParser = new WktGlossParser(posTaggerModelFile);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// close Wiktionary object
		wiktionary.close();	
		super.finalize();
	}
	
	public WiktionaryEntry getEntry(String lemma, WiktionaryPartOfSpeech partOfSpeech) throws WiktionaryException {
		List<WordEntry> entries = wiktionary.getWordEntries(lemma, JwktlUtils.toJwktlPartOfSpeech(partOfSpeech));
		if (entries.size() > 1)
			throw new JwktlException("Internal error! <"+lemma+", "+ partOfSpeech+"> has more than one entry! maybe the interface is inadaquate");
		return entries.isEmpty() ? null : new JwktlEntry(entries.get(0), glossParser);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wiktionary.WktDictionary#getEntriesOf(java.lang.String)
	 */
	public Map<WiktionaryPartOfSpeech, WiktionaryEntry> getEntriesOf(String lemma) throws WiktionaryException {
		Map<WiktionaryPartOfSpeech, WiktionaryEntry> entryMap = new HashMap<WiktionaryPartOfSpeech, WiktionaryEntry>();
		for (WordEntry wordEntry : wiktionary.getWordEntries(lemma))
		{
			WiktionaryPartOfSpeech wiktionaryPartOfSpeech = JwktlUtils.toWiktionaryPartOfSpeech(wordEntry.getPartOfSpeech());
			if (entryMap.containsKey(wiktionaryPartOfSpeech))
				throw new JwktlException("Internal error! <"+lemma+", "+ wiktionaryPartOfSpeech+"> has more than one entry! maybe the interface is inadaquate");
			entryMap.put(wiktionaryPartOfSpeech, new JwktlEntry(wordEntry, glossParser));
		}
		return entryMap;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.Dictionary#getSynsetsOf(java.lang.String, java.lang.Enum)
	 */
	public Set<WiktionarySense> getSensesOf(String lemma, WiktionaryPartOfSpeech partOfSpeech) throws WiktionaryException 
	{
		WiktionaryEntry entry = getEntry(lemma, partOfSpeech); 
		return entry != null ? entry.getAllSenses() : new HashSet<WiktionarySense>();
	}
	
	public Map<WiktionaryPartOfSpeech, Set<WiktionarySense>> getSensesOf(String lemma) throws WiktionaryException {
		Map<WiktionaryPartOfSpeech, Set<WiktionarySense>> sensesMap = new HashMap<WiktionaryPartOfSpeech, Set<WiktionarySense>>();
		for (Entry<WiktionaryPartOfSpeech, WiktionaryEntry> wordEntry : getEntriesOf(lemma).entrySet())
			sensesMap.put(wordEntry.getKey(), wordEntry.getValue().getAllSenses());
		return sensesMap;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.Dictionary#getSortedSynsetsOf(java.lang.String, java.lang.Enum)
	 */
	public List<WiktionarySense> getSortedSensesOf(String lemma, WiktionaryPartOfSpeech partOfSpeech) throws WiktionaryException {
		WiktionaryEntry entry = getEntry(lemma, partOfSpeech); 
		return entry != null ? entry.getAllSortedSenses() : new Vector<WiktionarySense>();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.Dictionary#getSortedSynsetOf(java.lang.String)
	 */
	public Map<WiktionaryPartOfSpeech, List<WiktionarySense>> getSortedSensesOf(String lemma)	throws WiktionaryException {
		Map<WiktionaryPartOfSpeech, List<WiktionarySense>> sensesMap = new HashMap<WiktionaryPartOfSpeech, List<WiktionarySense>>();
		for (Entry<WiktionaryPartOfSpeech, WiktionaryEntry> wordEntry : getEntriesOf(lemma).entrySet())
			sensesMap.put(wordEntry.getKey(), wordEntry.getValue().getAllSortedSenses());
		return sensesMap;
	}
}
