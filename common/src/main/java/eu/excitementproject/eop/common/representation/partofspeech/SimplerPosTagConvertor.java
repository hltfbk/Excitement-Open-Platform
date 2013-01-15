package eu.excitementproject.eop.common.representation.partofspeech;

/**
 * Convertors of {@link CanonicalPosTag} to/from {@link SimplerCanonicalPosTag}
 * 
 * @author Asher Stern
 * @since Jan 13, 2013
 *
 */
public class SimplerPosTagConvertor
{
	public static SimplerCanonicalPosTag simplerPos(CanonicalPosTag canonicalPosTag)
	{
		return simplerArray[canonicalPosTag.ordinal()];
	}
	
	public static CanonicalPosTag fromSimplerToCanonical(SimplerCanonicalPosTag simplerCanonicalPosTag)
	{
		return canonicalArray[simplerCanonicalPosTag.ordinal()];
	}

	private static final SimplerCanonicalPosTag[] simplerArray;
	private static final CanonicalPosTag[] canonicalArray;
	static
	{
		simplerArray = new SimplerCanonicalPosTag[CanonicalPosTag.values().length];
		for (CanonicalPosTag canonicalPosTag : CanonicalPosTag.values())
		{
			int ordinal = canonicalPosTag.ordinal();
			SimplerCanonicalPosTag simplerCanonicalPosTag = null;
			switch(canonicalPosTag)
			{
			case ADJ:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.ADJECTIVE;
				break;
			case ADV:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.ADVERB;
				break;
			case ART:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.DETERMINER;
				break;
			case CARD:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.OTHER;
				break;
			case CONJ:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.PREPOSITION;
				break;
			case N:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.NOUN;
				break;
			case NN:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.NOUN;
				break;
			case NP:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.NOUN;
				break;
			case O:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.OTHER;
				break;
			case OTHER:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.OTHER;
				break;
			case PP:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.PREPOSITION;
				break;
			case PR:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.PRONOUN;
				break;
			case PUNC:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.PUNCTUATION;
				break;
			case V:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.VERB;
				break;
			}
			simplerArray[ordinal] = simplerCanonicalPosTag;
		}
		
		canonicalArray = new CanonicalPosTag[SimplerCanonicalPosTag.values().length];
		for (SimplerCanonicalPosTag simplerCanonicalPosTag : SimplerCanonicalPosTag.values())
		{
			CanonicalPosTag canonicalPosTag = null;
			switch(simplerCanonicalPosTag)
			{
			case ADJECTIVE:
				canonicalPosTag = CanonicalPosTag.ADJ;
				break;
			case ADVERB:
				canonicalPosTag = CanonicalPosTag.ADV;
				break;
			case DETERMINER:
				canonicalPosTag = CanonicalPosTag.ART;
				break;
			case NOUN:
				canonicalPosTag = CanonicalPosTag.N;
				break;
			case OTHER:
				canonicalPosTag = CanonicalPosTag.OTHER;
				break;
			case PREPOSITION:
				canonicalPosTag = CanonicalPosTag.PP;
				break;
			case PRONOUN:
				canonicalPosTag = CanonicalPosTag.PR;
				break;
			case PUNCTUATION:
				canonicalPosTag = CanonicalPosTag.PUNC;
				break;
			case VERB:
				canonicalPosTag = CanonicalPosTag.V;
				break;
			}
			canonicalArray[simplerCanonicalPosTag.ordinal()] = canonicalPosTag;
		}
		
	}
}
