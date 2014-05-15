package eu.excitementproject.eop.common.utilities.linguistics.english.tense;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.linguistics.LinguisticsException;


/**
 * 
 * @author Asher Stern
 * @since October 9 2012
 *
 */
@LanguageDependent("english")
public class EnglishVerbTenseRetriever
{
	
	public static EnglishVerbFormsEntity getTenseForVerb(String verb) throws LinguisticsException
	{
		if (IrregularTenseTable.getTenseMap().containsKey(verb))
		{
			return IrregularTenseTable.getTenseMap().get(verb);
		}
		else
		{
			return buildForRegularVerb(verb);
		}
	}
	
	public static EnglishVerbFormsEntity buildForRegularVerb(String verb) throws LinguisticsException
	{
		verb = verb.trim();
		String pastTense;
		if (verb.length()<2)
		{
			// throw new LinguisticsException("Wrong verb: "+verb);
			pastTense = verb+"ed";
		}
		else
		{
			if ('e'==verb.charAt(verb.length()-1))
			{
				pastTense = verb+'d';
			}
			else
			{
				if ('y'==verb.charAt(verb.length()-1))
				{
					if (CONSONANTS.contains(verb.charAt(verb.length()-2)))
					{
						pastTense = verb.substring(0, verb.length()-1)+"ied";
					}
					else
					{
						pastTense = verb+"ed";
					}
				}
				else if ( (VOWELS.contains(verb.charAt(verb.length()-2))) && (CONSONANTS.contains(verb.charAt(verb.length()-1))))
				{
					if ('c'==verb.charAt(verb.length()-1))
					{
						pastTense = verb+"ked" +", "+verb+"ed"; // handling stressed and unstressed cases
					}
					else
					{
						pastTense = verb+verb.charAt(verb.length()-1)+"ed" +", "+verb+"ed"; // handling stressed and unstressed cases
					}
				}
				else
				{
					pastTense = verb+"ed";
				}
			}
		}
		// if (null==pastTense) throw new LinguisticsException("Internal bug");
		
		return new EnglishVerbFormsEntity(verb, pastTense, pastTense);
	}
	
	
	private static final Set<Character> CONSONANTS = Utils.arrayToCollection(
			new Character[]{'b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z'}
			, new HashSet<Character>());
	
	private static final Set<Character> VOWELS = Utils.arrayToCollection(
			new Character[]{'a','e','i','o','u'}
			, new HashSet<Character>());

}
