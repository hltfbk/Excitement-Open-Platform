package ac.biu.nlp.nlp.instruments.inflector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import ac.biu.nlp.nlp.instruments.inflector.Inflection.InflectionType;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggedToken;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;

/**
 * This class accepts a verb/noun lemma and returns all its inflections, according to the regular English inflection conventions, 
 * and to two given irregular verbs and nouns files.   
 * 
 * @author Amnon Lotan
 *
 * @since 24/02/2011
 */
public class EnglishLemmaInflector 
{
	private static final String ED_SUFFIX = "ed";
	private static final String D_SUFFIX = "d";
	private static final String E_SUFFIX = "e";
	private static final String DEFAULT_VERB_AND_NOUN_SUFFIX = "s";
	private static final String IES_SUFFIX = "ies";
	private static final String FRICATIVE_SUFFIX = "es";
	private static final String GERUND_SUFFIX = "ing";
	private static final char SPACE = ' ';
	/**
	 * rejects "wed", but not "breed", cos we have "bred"
	 */
	private static final String PRETERITE_PATTERN = ".*..ed$";
	/**
	 * 	to reject "sing" "ring" and "ping", but cling, string, swing are still false positives here, because of lying and dying.
	 *  So those three must be explicitly address in the file
	 */
	private static final String GERUND_PATTERN = ".*.." + GERUND_SUFFIX + "$";
	/**
	 * accepts "seen" "been" and longer ones
	 */
	private static final String IRREGULAR_PARTICIPLE_N_PATTERN = ".*...n$";
	/**
	 * matches anything ending with "s" like sees, lies, wolves...
	 */
	private static final String PRESENT_3SG_PATTERN = ".*..s$";
	private static final String FRICATIVES_PATTERN = ".*..(s|x|z|sh|ch)$";
	private static final String Y_SUFFIX_PATTERN = ".*..[^aeiou]y$";
	private static final String O_SUFFIX_PATTERN = ".*..[^aeiou]o$";
	private static final String E_GERUND_SUFFIX_PATTERN = ".*..[^e]e$";
	private static final String VOWEL_PREFIX_PATTERN = "^[aeiouAEIOU].*";

	//				 			{ one_lemma[], gerunds[], participles[], preterites[], presents[] }
	private static final String[][] BE = new String[][]	
	{	new String[]{"be"}, new String[]{"being"}, new String[]{"been"}, new String[]{"was", "were"}, new String[]{"am", "is", "are"}};
	private static final String[][] HAVE = new String[][]
    {	new String[]{"have"}, new String[]{"having"}, new String[]{"had"}, new String[]{"had"}, new String[]{"have", "has"}};
	private static final String[][] GO = new String[][]
    {	new String[]{"go"}, new String[]{"going"}, new String[]{"gone"}, new String[]{"went"}, new String[]{"go", "goes"}};                                
	/**
	 * inflections of the most irregular english verbs.
	 */
	private static final String[][][] IRREGULAR_VERBS = new String[][][]	{BE, HAVE, GO};
	
	/**
	 * Construct a new {@link EnglishLemmaInflector}, that knows the inflections of a handful of hardcoded verbs, and all those given in the verbs 
	 * and nouns files. 
	 * <p>
	 * Format of lines of irregulars files: {@code inflected_word <space> lemma}
	 * 
	 * @param irregularVerbsFile optional, e.g. "D:/Apps/Data/RESOURCES/WordNet/2.1/dict.snow.30k/verb.exc" or iParams.getFile("irregular-verbs")
	 * @param irregularNounsFile optional, e.g. "D:/Apps/Data/RESOURCES/WordNet/2.1/dict.snow.30k/noun.exc" or iParams.getFile("irregular-nouns")
	 * @throws InflectorException
	 */
	public EnglishLemmaInflector(File irregularVerbsFile, File irregularNounsFile) throws InflectorException
	{
		// fill in the verb inflection tables
		verbs = new Hashtable<String, Hashtable<InflectionType, Vector<Inflection>>>();
		addTheMostIrregularVerbInflections(IRREGULAR_VERBS, verbs);			// fill the most irregular verbs
		populateTableFromIrregularsFile(verbs, irregularVerbsFile, true);

		// populate the nouns inflection tables
		nouns = new Hashtable<String, Vector<Inflection>>();
		populateTableFromIrregularsFile(nouns, irregularNounsFile, false);
	}
	
	/**
	 * Returns a list of inflected forms of the verb/noun lemmatized phrase and its POS. The list
	 * does not include the lemma itself nor the infinite verb form (to <lemma>).
	 * 
	 * @param token
	 * @return
	 * @throws InflectorException 
	 */
	public List<Inflection> inflect( PosTaggedToken token ) throws InflectorException
	{
		Vector<Inflection> inflections =  new Vector<Inflection>();
		
		String lemmaPhrase = token.getToken();
		CanonicalPosTag pos = token.getPartOfSpeech().getCanonicalPosTag();
		
		if(pos == CanonicalPosTag.NOUN)
			inflections = inflectNoun(lemmaPhrase, nouns);
		else if (pos == CanonicalPosTag.VERB && lemmaPhrase.length() >= 2)
			inflections = inflectVerb(lemmaPhrase.toLowerCase(), verbs);
		// else return empty list
		
		return inflections;
	}
	
	/**
	 * Returns a list of inflected forms of the {@link PosTaggedToken}, with all sorts of a/an/to be/to have determiners and auxiliaries,
	 * used for web-queries. The list includes the lemma itself.
	 * 
	 * @param token
	 * @param givenInflections optional, a list of the token's inflections you want to be used. If null, then {@link #inflect(PosTaggedToken)}
	 * will be used to make up the list. If you don't want any inflections to be used, give an empty list.
	 * @return
	 * @throws InflectorException 
	 */
	public List<String> queryInflect(PosTaggedToken token, List<Inflection> givenInflections) throws InflectorException
	{
		if(givenInflections == null)
			givenInflections = inflect(token);
		
		Vector<String> retQueryInflections = new Vector<String>();
		
		String phrase = token.getToken();
		switch (token.getPartOfSpeech().getCanonicalPosTag())
		{
			case NOUN:
				String lastWord = phrase.substring( phrase.lastIndexOf(SPACE) + 1);
				// add "the" "a"/"an"
				if(Character.isLowerCase(lastWord.charAt(0)))	// ignore uppercases
				{
					retQueryInflections.add("the " + phrase);
					if(!nounIsPlural(lastWord))
						if(phrase.matches(VOWEL_PREFIX_PATTERN))
							retQueryInflections.add("an " + phrase);
						else
							retQueryInflections.add("a " + phrase);
				}
				
				// add given inflections
				for (Inflection inflection : givenInflections)
				{
					retQueryInflections.add(inflection.word);
					if(inflection.type == InflectionType.NOUN_PLURAL)
						retQueryInflections.add("the " + inflection.word);
				}
				break;
				
			case VERB:
				// add "will" "would" "to"
				retQueryInflections.add("will " + phrase);
				retQueryInflections.add("would " + phrase);
				retQueryInflections.add("to " + phrase);

				// add BE and HAVE auxiliaries
				for(Inflection inflection : givenInflections)
				{
					if(inflection.type == InflectionType.GERUND)
					{
						retQueryInflections.add("are " + inflection.word);
						retQueryInflections.add("is " + inflection.word);
						retQueryInflections.add("was " + inflection.word);
						retQueryInflections.add("were " + inflection.word);
					}
					else if(inflection.type == InflectionType.PAST_PARTICIPLE)
					{
						retQueryInflections.add("have " + inflection.word);
						retQueryInflections.add("has " + inflection.word);
						retQueryInflections.add("had " + inflection.word);
					}
					else
						retQueryInflections.add(inflection.word);
				}
				break;
				
			case ADJECTIVE:
				if(Character.isLowerCase(phrase.charAt(0)))
				{
					// add "the" "a"/"an"
					retQueryInflections.add("the " + phrase);
					if(phrase.matches(VOWEL_PREFIX_PATTERN))
						retQueryInflections.add("an " + phrase);
					else
						retQueryInflections.add("a " + phrase);
				}
				
				// add given inflections
				for(Inflection inflection : givenInflections)
					retQueryInflections.add(inflection.word);
				break;
				
				default:
					for(Inflection inflection : givenInflections)
						retQueryInflections.add(inflection.word);
		}
		
		return retQueryInflections;
	}
	
	/**
	 * @param table
	 * @param irregularsFile	format of lines: inflected_word <space> lemma 
	 * @param isVerbs 
	 * @throws InflectorException 
	 */
	private void populateTableFromIrregularsFile(Object table, File irregularsFile, boolean isVerbs) throws InflectorException
	{
		// read the irregularsFile, line by line
		if(irregularsFile != null)
		{

			try
			{
				BufferedReader verbsReader = new BufferedReader(new FileReader(irregularsFile));
				try
				{
					String line;
					while((line = verbsReader.readLine()) != null)
					{
						line = line.trim().toLowerCase();
						int firstSpace = line.indexOf(SPACE);
						if( firstSpace >= 0)
						{              
							String lemma = line.substring(firstSpace + 1);
							String inflected = line.substring(0, firstSpace);

							if (isVerbs)
							{
								// Is this lemma a most irregular verb from IRREGULAR_VERBS?
								if (!isMostIrregularLemma(lemma))
									// classify this inflected word
									classifyAndAddInflectedVerb(lemma, inflected, table);
							}
							else	// is nouns
								addInflectedNoun(lemma, inflected, table);
						}
					}
				}
				finally
				{
					verbsReader.close();
				}
			}
			catch (IOException e)
			{
				throw new InflectorException("Error reading from " + irregularsFile, e);
			}
		}
	}

	/**
	 * @param lemma
	 * @param inflected
	 * @param table
	 */
	private void addInflectedNoun(String lemma, String inflected, Object nounTableObject)
	{
		@SuppressWarnings("unchecked")
		Hashtable<String, Vector<Inflection>> nounTable = (Hashtable<String, Vector<Inflection>>) nounTableObject;
		
		Vector<Inflection> inflections = nounTable.get(lemma);
		if(inflections == null){
			inflections = new Vector<Inflection>();
			nounTable.put(lemma, inflections);
		}
		inflections.addElement(new Inflection(InflectionType.NOUN_PLURAL, inflected));
	}

	/**
	 * classify this inflected word
	 * 
	 * @param lemma
	 * @param inflected
	 * @param verbs2
	 */
	private void classifyAndAddInflectedVerb( String lemma, String inflected, Object verbsTableObject)
	{
		 @SuppressWarnings("unchecked")
		Hashtable<String, Hashtable<InflectionType, Vector<Inflection>>> verbsTable = 
			(Hashtable<String, Hashtable<InflectionType, Vector<Inflection>>>) verbsTableObject;
		
		Hashtable<InflectionType, Vector<Inflection>> inflectionHash = verbsTable.get(lemma);
		if(inflectionHash == null)
		{          
			inflectionHash = new Hashtable<InflectionType, Vector<Inflection>>();
			verbsTable.put(lemma, inflectionHash);
		}          
		
		Vector<Inflection> inflections;
		
		// add a gerund from the file, if such exists
		if(inflected.matches(GERUND_PATTERN))
			addToInflectionHash(inflectionHash, InflectionType.GERUND, inflected);
		// add a preterite
		else if(inflected.matches(PRETERITE_PATTERN))
		{
			addToInflectionHash(inflectionHash, InflectionType.PRETERITE, inflected);

			// a preterite is also a participle (?)
			inflections = inflectionHash.get(InflectionType.PAST_PARTICIPLE);
			if(inflections == null)
			{
				inflections = new Vector<Inflection>();
				inflectionHash.put(InflectionType.PAST_PARTICIPLE, inflections);
				inflections.addElement(new Inflection(InflectionType.PAST_PARTICIPLE, inflected));
			}
			// in case there already was a participle inflection for this verb, don't add this one
		}
		// add an irregular past participle ending with "n"
		else if(inflected.matches(IRREGULAR_PARTICIPLE_N_PATTERN))
		{
			inflections = inflectionHash.get(InflectionType.PAST_PARTICIPLE);
			if(inflections == null)
			{
				inflections = new Vector<Inflection>();
				inflectionHash.put(InflectionType.PAST_PARTICIPLE, inflections);
			}
			// in case there already was a participle inflection for this verb, delete it
			else
				inflections.clear();
			inflections.addElement(new Inflection(InflectionType.PAST_PARTICIPLE, inflected));
		}
		// match verbs ending with "s" as present 3rd person singular
		else if (inflected.matches(PRESENT_3SG_PATTERN))
			addToInflectionHash(inflectionHash, InflectionType.PRESENT_3SG, inflected);
		else
		{
			// add another kind of irregular preterite
			addToInflectionHash(inflectionHash, InflectionType.PRETERITE, inflected);

			// and assume that this is also an irregular participle								
			addToInflectionHash(inflectionHash, InflectionType.PAST_PARTICIPLE, inflected);
		}		
	}

	/**
	 * @param inflectionHash
	 * @param gerund
	 * @param inflected
	 * @param lemma 
	 */
	private void addToInflectionHash( Hashtable<InflectionType, Vector<Inflection>> inflectionHash, InflectionType inflectionType, 
			String inflected)
	{
		Vector<Inflection> inflections = inflectionHash.get(inflectionType);
		if(inflections == null)
		{
			inflections = new Vector<Inflection>();
			inflectionHash.put(inflectionType, inflections);
		}
		inflections.addElement(new Inflection(inflectionType, inflected));
	}

	/**
	 * Is this lemma a most irregular verb from {@code IRREGULAR_VERBS}?
	 * 
	 * @param lemma
	 * @return
	 */
	private static boolean isMostIrregularLemma(String lemma)
	{
		for (String[][] irregularVerbRec : IRREGULAR_VERBS)
			if(lemma.equalsIgnoreCase(irregularVerbRec[0][0]))
				return true;
		return false;
	}

	/**
	 * add the most irregular verbs' inflections to the verbs table
	 * 
	 * @param mostIrregularVerbs 
	 * @param verbs 
	 */
	private void addTheMostIrregularVerbInflections(
			String[][][] mostIrregularVerbs, Hashtable<String, Hashtable<InflectionType, Vector<Inflection>>> verbs)
	{
		for (String[][] verbInflections : mostIrregularVerbs)
		{
			String lemma = verbInflections[0][0];
			
			Hashtable<InflectionType, Vector<Inflection>> inflectionsHash = new Hashtable<InflectionType, Vector<Inflection>>();
	
			Vector<Inflection> gerundInflections = new Vector<Inflection>();
			for (String gerund : verbInflections[1])
				gerundInflections.addElement(new Inflection(InflectionType.GERUND, gerund));
			inflectionsHash.put(InflectionType.GERUND, gerundInflections);
			
			Vector<Inflection> participleInflections = new Vector<Inflection>();
			for (String participle : verbInflections[2])
				participleInflections.addElement(new Inflection(InflectionType.PAST_PARTICIPLE, participle));
			inflectionsHash.put(InflectionType.PAST_PARTICIPLE, participleInflections);
	
			Vector<Inflection> preteriteInflections = new Vector<Inflection>();
			for (String preterite : verbInflections[3])
				preteriteInflections.addElement(new Inflection(InflectionType.PRETERITE, preterite));
			inflectionsHash.put(InflectionType.PRETERITE, preteriteInflections);
	
			Vector<Inflection> presentInflections = new Vector<Inflection>();
			for (String present_3sg : verbInflections[4])
				presentInflections.addElement(new Inflection(InflectionType.PRESENT_3SG, present_3sg));
			inflectionsHash.put(InflectionType.PRESENT_3SG, presentInflections);
	
			verbs.put(lemma, inflectionsHash);
		}
	}

	/**
	 * add the lemmaInflections, prefixed by firstWords and suffixed by lastWords, to retInflections 
	 * 
	 * @param lemmaInflections
	 * @param firstWords
	 * @param lastWords
	 * @param retInflections
	 */
	private void addPhraseInflections(Vector<Inflection> lemmaInflections, String firstWords, String lastWords, Vector<Inflection> retInflections)
	{
		if(firstWords.length() + lastWords.length() > 0)
			for(Inflection inflection : lemmaInflections)
				retInflections.add(new Inflection(inflection.type, firstWords + inflection.word + lastWords));
		else
			retInflections.addAll(lemmaInflections);
	}

	/**
	 * @param lemmaPhrase
	 * @param nounsTable 
	 * @return the inflected noun phrase 
	 */
	private Vector<Inflection> inflectNoun(String lemmaPhrase, Hashtable<String, Vector<Inflection>> nounsTable)
	{
		lemmaPhrase = lemmaPhrase.trim();		
		// the lemmaPhrase is split to its first words and its last word cos only the last word counts in noun inflection 
		int lastSpace = lemmaPhrase.lastIndexOf(SPACE);
		// whether lastSpace is -1 or bigger, lastSpace+1 is still the beginning of the last word
		String lastWord = lemmaPhrase.substring(lastSpace + 1);	 

		Vector<Inflection> retInflections = new Vector<Inflection>();
		if ( nounShouldBeInflected(lastWord) ) 
		{
			String firstWords = lemmaPhrase.substring(0, lastSpace + 1);
			
			Vector<Inflection> savedInflections = nounsTable.get(lastWord);
			if(savedInflections == null)
			{
				// pick the default inflection for the suffix of this noun phrase
				if (lastWord.matches(FRICATIVES_PATTERN) || lastWord.matches(O_SUFFIX_PATTERN))
					lastWord = lastWord + FRICATIVE_SUFFIX;
				else if (lastWord.matches(Y_SUFFIX_PATTERN))
					lastWord = lastWord.substring(0, lastWord.length() - 1) + IES_SUFFIX;
				else
					lastWord = lastWord + DEFAULT_VERB_AND_NOUN_SUFFIX;
	
				retInflections.addElement(new Inflection(InflectionType.NOUN_PLURAL, firstWords + lastWord));
			}
			else
				addPhraseInflections(savedInflections, firstWords, "", retInflections);
		}
			
		return retInflections;
	}
	
	/**
	 * @param verbsTable 
	 * @param firstWord
	 * @return
	 * @throws InflectorException 
	 */
	private Vector<Inflection> inflectVerb(String lemma, Hashtable<String, Hashtable<InflectionType, Vector<Inflection>>> verbsTable) 
		throws InflectorException
	{
		int firstSpace = lemma.indexOf(SPACE);
		String lastWords = firstSpace >= 0 ? lemma.substring(firstSpace) 	: "";
		String firstWord = firstSpace >= 0 ? lemma.substring(0, firstSpace) : lemma; 

		Hashtable<InflectionType, Vector<Inflection>> inflectionsOfThisVerb = verbsTable.get(firstWord);
		if(inflectionsOfThisVerb == null)
			inflectionsOfThisVerb = new Hashtable<InflectionType, Vector<Inflection>>();

		Vector<Inflection> retInflectionsOfThisVerb = new Vector<Inflection>();

		// add gerund inflections
		retInflectionsOfThisVerb.addAll(inflectVerbByInflectionType(inflectionsOfThisVerb, InflectionType.GERUND,			 	firstWord, lastWords));
		retInflectionsOfThisVerb.addAll(inflectVerbByInflectionType(inflectionsOfThisVerb, InflectionType.PAST_PARTICIPLE, 	firstWord, lastWords));
		retInflectionsOfThisVerb.addAll(inflectVerbByInflectionType(inflectionsOfThisVerb, InflectionType.PRETERITE,			firstWord, lastWords));
		retInflectionsOfThisVerb.addAll(inflectVerbByInflectionType(inflectionsOfThisVerb, InflectionType.PRESENT_3SG, 		firstWord, lastWords));
		
		return retInflectionsOfThisVerb;
	}
	
	/**
	 * Inflect the given verbal phrase by the table we have, or, if not hit in the table, by the default formula
	 * 
	 * @param inflectionsOfThisVerb
	 * @param inflectionType
	 * @param firstWord
	 * @param lastWords
	 * @return
	 * @throws InflectorException 
	 */
	private Vector<Inflection> inflectVerbByInflectionType(
			Hashtable<InflectionType, Vector<Inflection>> inflectionsOfThisVerb, InflectionType inflectionType, String firstWord, String lastWords) 
			throws InflectorException
	{
		Vector<Inflection> retInflectionsOfThisVerb = new Vector<Inflection>();

		// check the table firsts
		Vector<Inflection> typeInflections = inflectionsOfThisVerb.get(inflectionType);
		if(typeInflections != null)
			addPhraseInflections(typeInflections, "", lastWords, retInflectionsOfThisVerb);
		else
		{
			// add the default inflection
			String inflection;
			switch (inflectionType)
			{
				case GERUND:
					// careful with verbs ending with 'e'
					inflection = ( (firstWord.matches(E_GERUND_SUFFIX_PATTERN)) ? firstWord.substring(0, firstWord.length() - 1) : firstWord ) +
						GERUND_SUFFIX + lastWords;
					break;
				case PAST_PARTICIPLE:
				case PRETERITE:
					inflection = firstWord + ( firstWord.endsWith(E_SUFFIX) ? D_SUFFIX : ED_SUFFIX ) + lastWords;
					break;
				case PRESENT_3SG:
					inflection = 
					(
						firstWord.matches(FRICATIVES_PATTERN)	? firstWord 										+ FRICATIVE_SUFFIX	:
						firstWord.matches(Y_SUFFIX_PATTERN)		? firstWord.substring(0, firstWord.length() - 1) 	+ IES_SUFFIX 		:
						firstWord.matches(O_SUFFIX_PATTERN)		? firstWord 										+ FRICATIVE_SUFFIX	:
																  firstWord 										+ DEFAULT_VERB_AND_NOUN_SUFFIX
					) + lastWords;
					break;
				default:
					throw new InflectorException("bug alert! got " + inflectionType);
			}

			retInflectionsOfThisVerb.addElement(new Inflection(inflectionType, inflection));
		}
		
		return retInflectionsOfThisVerb;
	}

	/**
	 * @param noun
	 * @return
	 */
	private boolean nounIsPlural(String noun)
	{
		return (noun.length() > 1 &&
				noun.charAt(noun.length() - 1) == 's' &&
				noun.charAt(noun.length() - 2) != 's' );
	}
	
	/**
	 * accept nouns that begin with a lowercase, have at least two letters, and which do not end with a single 's' (i.e. 'ss' is ok)
	 * 
	 * @param noun
	 * @return
	 */
	private boolean nounShouldBeInflected(String noun)
	{
		return (Character.isLowerCase(noun.charAt(0)) &&
				noun.length() > 1 && 
				(noun.charAt(noun.length() - 1) != 's' || noun.charAt(noun.length() - 2) == 's'));
	}
	
	/**
	 * A table of nouns and their inflections 
	 */
	private Hashtable<String, Vector<Inflection>> nouns = null;
	/**
	 * A table of verbs and their inflections...
	 */
	private Hashtable<String, Hashtable<InflectionType, Vector<Inflection>>> verbs = null;
}