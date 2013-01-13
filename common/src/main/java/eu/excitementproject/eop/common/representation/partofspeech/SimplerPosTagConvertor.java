package eu.excitementproject.eop.common.representation.partofspeech;

/**
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

	private static final SimplerCanonicalPosTag[] simplerArray;
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
				simplerCanonicalPosTag = SimplerCanonicalPosTag.ADJECTIVE;
				break;
			case CONJ:
				simplerCanonicalPosTag = SimplerCanonicalPosTag.OTHER;
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
	}
}
