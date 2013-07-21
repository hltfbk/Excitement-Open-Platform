package eu.excitementproject.eop.common.representation.parse.representation.basic;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;


/**
 * Used to give a mapping between the standard set of Penn Tree bank Part-Of-Speech types,
 * and the {@link BurstPartOfSpeechType}.
 * <P>
 * This class provides the public static variable: <code> map </code>.
 * Don't create instances of this class, since it does not make sense. Just use
 * the static variable <code> map </code> to get the required information.
 * <P>
 * Actually, my recommendation is not to use {@link BurstPartOfSpeechType} at all,
 * so this mapping is not really necessary.
 * 
 * @author Asher Stern
 *
 */
@LanguageDependent("English")
public class PennTreebankPartOfSpeechTags
{
	public static final ImmutableMap<String, BurstPartOfSpeechType> map;
	
	private static final Map<String, BurstPartOfSpeechType> privateMap;
	 
	static
	{
		privateMap = new LinkedHashMap<String, BurstPartOfSpeechType>();
		privateMap.put("CC", null);
		privateMap.put("CD", BurstPartOfSpeechType.CARDINAL);
		privateMap.put("DT", BurstPartOfSpeechType.DETERMINER);
		privateMap.put("EX", null);
		privateMap.put("FW",null);
		privateMap.put("IN", BurstPartOfSpeechType.PREPOSITION);
		privateMap.put("JJ", BurstPartOfSpeechType.ADJECTIVE);
		privateMap.put("JJR", BurstPartOfSpeechType.ADJECTIVE);
		privateMap.put("JJS", BurstPartOfSpeechType.ADJECTIVE);
		privateMap.put("LS",null);
		privateMap.put("MD", null);
		privateMap.put("NN", BurstPartOfSpeechType.NOUN);
		privateMap.put("NNS", BurstPartOfSpeechType.NOUN);
		privateMap.put("NNP", BurstPartOfSpeechType.NOUN);
		privateMap.put("NNPS", BurstPartOfSpeechType.NOUN);
		privateMap.put("PDT", BurstPartOfSpeechType.DETERMINER);
		privateMap.put("POS", null);
		privateMap.put("PRP", BurstPartOfSpeechType.PRONOUN);
		privateMap.put("PRP$", BurstPartOfSpeechType.PRONOUN);
		privateMap.put("RB", BurstPartOfSpeechType.ADVERB);
		privateMap.put("RBR", BurstPartOfSpeechType.ADVERB);
		privateMap.put("RBS", BurstPartOfSpeechType.ADVERB);
		privateMap.put("RP", BurstPartOfSpeechType.PARTICLE);
		privateMap.put("SYM", null);
		privateMap.put("TO", BurstPartOfSpeechType.PREPOSITION);
		privateMap.put("UH", null);
		privateMap.put("VB", BurstPartOfSpeechType.VERB);
		privateMap.put("VBD", BurstPartOfSpeechType.VERB);
		privateMap.put("VBG", BurstPartOfSpeechType.VERB);
		privateMap.put("VBN", BurstPartOfSpeechType.VERB);
		privateMap.put("VBP", BurstPartOfSpeechType.VERB);		
		privateMap.put("VBZ", BurstPartOfSpeechType.VERB);
		privateMap.put("WDT", BurstPartOfSpeechType.DETERMINER);
		privateMap.put("WP", BurstPartOfSpeechType.PRONOUN);
		privateMap.put("WP$", BurstPartOfSpeechType.PRONOUN);
		privateMap.put("WRB", BurstPartOfSpeechType.ADVERB);
		
		
		map = new ImmutableMapWrapper<String, BurstPartOfSpeechType>(privateMap);
	}

}
