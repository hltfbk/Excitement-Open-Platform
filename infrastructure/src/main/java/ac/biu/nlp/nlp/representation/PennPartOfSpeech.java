package ac.biu.nlp.nlp.representation;

import java.util.HashSet;
import java.util.Set;

import ac.biu.nlp.nlp.general.SimpleValueSetMap;
import ac.biu.nlp.nlp.general.Utils;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;

/**
 * A Penn Treebank pos-tag
 * The list is taken from: {@linkplain link http://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html}
 * Examples can be found here: {@linkplain link http://www.comp.leeds.ac.uk/amalgam/tagsets/upenn.html}
 * <p>
 * The tag's string must conform with the list. 
 * 
 * @author Shachar Mirkin
 * @since Jan 2011
 */
public class PennPartOfSpeech extends PartOfSpeech 
{
	// -----------------------------------------------------------------------

	private static final long serialVersionUID = -4787167211959166319L;

	/**
	 * All Penn pos tags
	 */
	public enum PennPosTag {
		CC, CD, DT, EX, FW, IN, JJ, JJR, JJS, LCB, LRB, LS, MD, NN, NNS, NNP, NNPS, PDT, POS, PRP, PRP$, 
		RB, RBR, RBS, RCB, RP, RRB, SYM, TO, UH, VB, VBD, VBG, VBN, VBP, VBZ, WDT, WP, WP$, WRB, PUNC, SYM1
	};

	public static final Set<String> PUNCTUATION;
	public static final Set<String> SYMBOLS;
	static
	{
		PUNCTUATION = Utils.arrayToCollection(
				new String[]{"", "\"", "'", "''", "(", ")", ",", "-", "-LCB-", "-LRB-", "-LRB-", "-RRB-", ".", ":", "``"},
				new HashSet<String>());
		
		SYMBOLS = Utils.arrayToCollection(new String[]{"#", "$"}, new HashSet<String>());
	}

	/**
	 * a bidirectional map between canonical POSs and their corresponding Penn POSs
	 */
	private static final SimpleValueSetMap<CanonicalPosTag, PennPosTag> PENN_TO_CANONICAL_MAP = new SimpleValueSetMap<CanonicalPosTag, PennPosTag>();
	static
	{
		// map between all canonical POSs and Penn POSs
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.VERB, PennPosTag.VB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.VERB, PennPosTag.VBD);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.VERB, PennPosTag.VBG);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.VERB, PennPosTag.VBN);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.VERB, PennPosTag.VBP);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.VERB, PennPosTag.VBZ);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NOUN, PennPosTag.NN);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NOUN, PennPosTag.NNS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NOUN, PennPosTag.NNP);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NOUN, PennPosTag.NNPS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJECTIVE, PennPosTag.JJ);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJECTIVE, PennPosTag.JJR);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJECTIVE, PennPosTag.JJS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.DETERMINER, PennPosTag.DT);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.DETERMINER, PennPosTag.WDT);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.DETERMINER, PennPosTag.PDT);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PRONOUN, PennPosTag.PRP);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PRONOUN, PennPosTag.PRP$);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PRONOUN, PennPosTag.WP);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PRONOUN, PennPosTag.WP$);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADVERB, PennPosTag.RB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADVERB, PennPosTag.RBR);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADVERB, PennPosTag.RBS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADVERB, PennPosTag.WRB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PREPOSITION, PennPosTag.CC);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PREPOSITION, PennPosTag.IN);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PREPOSITION, PennPosTag.RP);
			// particles, like not, infinitival to, anyway... see http://en.wikipedia.org/wiki/Grammatical_particle
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PREPOSITION, PennPosTag.TO);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, PennPosTag.PUNC);	// not a Penn Treebank pos-tag
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, PennPosTag.RCB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, PennPosTag.LCB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, PennPosTag.LRB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, PennPosTag.RRB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, PennPosTag.LS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.SYM);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.SYM1);	// symbols not included in Penn Treebank SYM
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.CD);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.EX);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.FW);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.MD);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.POS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.UH);
	}
	
	// -----------------------------------------------------------------


	/**
	 * A constructor receiving a string and converts it to Penn pos tag.
	 * When possible, use the constructor that accepts <code>PennPosTag</code> to identify errors at compilation time
	 * @param posTagString - a string that conforms with the Penn pos tag set
	 * @throws UnsupportedPosTagString - in case a non valid string is sent
	 */
	public PennPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException {
		super(posTagString);
	}

	/**
	 * A constructor receiving a Penn pos tag. This is a safer version than the one receiving string. When possible use this one.
	 * 
	 * @param pennPos - one of the values of the Penn tag set
	 * @throws UnsupportedPosTagString
	 */
	public PennPartOfSpeech(PennPosTag pennPos) throws UnsupportedPosTagStringException {
		super(pennPos.name());
		this.posTagString = pennPos.name().toUpperCase();
	}
	
	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		PennPartOfSpeech ret = new PennPartOfSpeech(posTagString);
		return ret;
	}

	/**
	 * get the canonical Pos corresponding to the given Penn pos
	 * 
	 * @param pennPos
	 * @return
	 */
	public static CanonicalPosTag pennPosToCannonical(PennPosTag pennPos)
	{
		return PENN_TO_CANONICAL_MAP.getKeysOf(pennPos).iterator().next();
	}
	
	/**
	 * get the set of Penn POSs corresponding to the given canonical Pos
	 * 
	 * @param canonPos
	 * @return
	 */
	public static ImmutableSet<PennPosTag> canonicalPosToPenn(CanonicalPosTag canonPos)
	{
		return PENN_TO_CANONICAL_MAP.get(canonPos);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.representation.PartOfSpeech#setCanonicalPosTag()
	 */
	@Override
	protected void setCanonicalPosTag()
	{
		setPennPos(posTagString);
		if (null==_pennPos)
			this.canonicalPosTag = CanonicalPosTag.OTHER;
		else
		{
			// look it up in the Penn to Canonicals map
			this.canonicalPosTag = PENN_TO_CANONICAL_MAP.containsValue(_pennPos) ? PENN_TO_CANONICAL_MAP.getKeysOf(_pennPos).iterator().next()
					:	CanonicalPosTag.OTHER;	// default
		}
	}
	
	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException 
	{
		setPennPos(posTagString);
		if (_pennPos==null)
			throw new UnsupportedPosTagStringException("pos tag '" + posTagString + "' is unsupported Penn pos tag");
	}
	
	protected void setPennPos(String posTagString)
	{
		if (_pennPos==null)
		{
			_pennPos = null;
			if (PUNCTUATION.contains(posTagString)){
				_pennPos = PennPosTag.PUNC;
			}
			else if(SYMBOLS.contains(posTagString)){
				_pennPos = PennPosTag.SYM1;
			}else{
				try 
				{
					_pennPos = PennPosTag.valueOf(posTagString);
				} catch (IllegalArgumentException e) 
				{
					_pennPos = null;
				}
			}
		}
	}
	
	private PennPosTag _pennPos = null;
}