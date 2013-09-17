package eu.excitementproject.eop.transformations.utilities;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.StringUtil;

/**
 * Currently this class is fine for easy-first as well as minipar.
 * 
 * 
 * @author Asher Stern
 *
 */
@LanguageDependent("english")
@ParserSpecific({"easyfirst","minipar"})
public class InfoObservations
{
	// TODO HARD CODED STRINGS
	
	// public constants
	public static final String MINIPAR_LEX_MOD_RELATION = "lex-mod";
	public static final Set<String> MINIPAR_NON_WORDS_LEMMAS;
	public static final Set<String> NON_CONTENT_VERBS;
	public static final Set<String> NON_CONTENT_VERBS_VERBS_ONLY;
	public static final Set<SimplerCanonicalPosTag> CONTENT_CANONICAL_PART_OF_SPEECH;
	public static final Set<String> PRONOUNS;
	public static final Set<String> NON_CONTENT_WORDS;
	static
	{
		MINIPAR_NON_WORDS_LEMMAS = new LinkedHashSet<String>();
		MINIPAR_NON_WORDS_LEMMAS.add("fin");
		MINIPAR_NON_WORDS_LEMMAS.add("inf");
		MINIPAR_NON_WORDS_LEMMAS.add("vpsc");
		
		NON_CONTENT_VERBS = new LinkedHashSet<String>();
		NON_CONTENT_VERBS.add("be");
		NON_CONTENT_VERBS.add("have");
		
		NON_CONTENT_VERBS_VERBS_ONLY = new LinkedHashSet<String>();
		NON_CONTENT_VERBS_VERBS_ONLY.add("will");
		NON_CONTENT_VERBS_VERBS_ONLY.add("shall");
		
		CONTENT_CANONICAL_PART_OF_SPEECH = new LinkedHashSet<SimplerCanonicalPosTag>();
		CONTENT_CANONICAL_PART_OF_SPEECH.add(SimplerCanonicalPosTag.NOUN);
		CONTENT_CANONICAL_PART_OF_SPEECH.add(SimplerCanonicalPosTag.ADJECTIVE);
		CONTENT_CANONICAL_PART_OF_SPEECH.add(SimplerCanonicalPosTag.ADVERB);
		CONTENT_CANONICAL_PART_OF_SPEECH.add(SimplerCanonicalPosTag.PRONOUN);
		CONTENT_CANONICAL_PART_OF_SPEECH.add(SimplerCanonicalPosTag.VERB);
		
		
		
		
		PRONOUNS = new LinkedHashSet<String>();
		PRONOUNS.add("I");
		PRONOUNS.add("You");
		PRONOUNS.add("you");
		PRONOUNS.add("he");
		PRONOUNS.add("He");
		PRONOUNS.add("she");
		PRONOUNS.add("She");
		PRONOUNS.add("it");
		PRONOUNS.add("It");
		PRONOUNS.add("We");
		PRONOUNS.add("we");
		PRONOUNS.add("They");
		PRONOUNS.add("they");
		
		NON_CONTENT_WORDS = new LinkedHashSet<String>();
		NON_CONTENT_WORDS.addAll(PRONOUNS);
		NON_CONTENT_WORDS.addAll(NON_CONTENT_VERBS);
	}

	public static boolean infoHasLemma(Info info)
	{
		boolean ret = false;
		String lemma = InfoGetFields.getLemma(info, "");
		lemma = lemma.trim();
		
		if ( ( StringUtil.hasNeitherLetterNorDigit(lemma) ) || (MINIPAR_NON_WORDS_LEMMAS.contains(lemma)) )
			ret = false;
		else
			ret = true;
		
		return ret;
	}
	
	public static boolean infoIsNamedEntity(Info info)
	{
		boolean ret = false;
		if (info!=null){if(info.getNodeInfo()!=null){if(info.getNodeInfo().getNamedEntityAnnotation()!=null)
		{
			ret = true;
		}}}
		return ret;
	}
	
	public static boolean infoIsNumber(Info info)
	{
		String lemma = InfoGetFields.getLemma(info);
		return StringUtil.isNumber(lemma);
	}
	
	public static boolean infoIsContentVerb(Info info)
	{
		boolean ret = false;
		if (infoHasLemma(info))
		{
			PartOfSpeech posObject = InfoGetFields.getPartOfSpeechObject(info);
			if (simplerPos(posObject.getCanonicalPosTag())==SimplerCanonicalPosTag.VERB)
			{
				String lemma = InfoGetFields.getLemma(info);
				if (!StringUtil.setContainsIgnoreCase(NON_CONTENT_VERBS, lemma))
				{
					if (!StringUtil.setContainsIgnoreCase(NON_CONTENT_VERBS_VERBS_ONLY, lemma))
					{
						ret = true;
					}
				}
			}
		}
		return ret;
	}
	
	public static boolean infoIsContentWord(Info info)
	{
		boolean ret = false;
		if (infoHasLemma(info))
		{
			String lemma = InfoGetFields.getLemma(info);
			if (!NON_CONTENT_WORDS.contains(lemma))
			{
				PartOfSpeech posObject = InfoGetFields.getPartOfSpeechObject(info);
				SimplerCanonicalPosTag canonicalPos = SimplerCanonicalPosTag.OTHER;
				if (posObject!=null)
					canonicalPos=simplerPos(posObject.getCanonicalPosTag());
				if (SimplerCanonicalPosTag.VERB.equals(canonicalPos))
				{
					return infoIsContentVerb(info);
				}
				else if (CONTENT_CANONICAL_PART_OF_SPEECH.contains(canonicalPos))
				{
					ret = true;
				}
			}
		}
		return ret;
	}
	
	public static boolean insertOnlyLexModOfMultiWord(Info insertedChildInfo, Info parentInfo)
	{
		boolean ret = false;
		if (InfoGetFields.getRelation(insertedChildInfo).equals(MINIPAR_LEX_MOD_RELATION))
		{
			if (infoHasLemma(insertedChildInfo))
			{
				String parentLemma = InfoGetFields.getLemma(parentInfo);
				List<String> parentWords = StringUtil.stringToWords(parentLemma);
				String childLemma = InfoGetFields.getLemma(insertedChildInfo);
				for (String parentWord : parentWords)
				{
					if (parentWord.equalsIgnoreCase(childLemma))
					{
						ret = true;
						break;
					}
				}
			}
		}

		return ret;
	}
	
	
	


}
