package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.utilities.Utils;


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
		CC, CD, DT, EX, FW, IN, JJ, JJR, JJS, LCB, LRB, LS, MD, NN, NNS, NNP, NNPS,
		NP, NPS, PDT, POS, PP$, PP, PRP, PRP$, RB, RBR, RBS, RCB, RP, RRB, SENT,
		SYM, TO, UH, VB, VBD, VBG, VBN, VBP, VBZ, VH, VHD, VHG, VHN, VHP, VHZ, VV,
		VVD, VVG, VVN, VVP, VVZ, WDT, WP, WP$, WRB, PUNC, SYM1
	};

	public static final Set<String> PUNCTUATION;
	public static final Set<String> SYMBOLS;
	static
	{
		PUNCTUATION = Utils.arrayToCollection(
				new String[]{"", "\"", "'", "''", "(", ")", ",", "-", "-LCB-", "-LRB-", "-LRB-", "-RRB-", ".", ":", "``", ";", "?", "\\!", "\\:"},
				new HashSet<String>());
		
		SYMBOLS = Utils.arrayToCollection(new String[]{"#", "$", "*"}, new HashSet<String>());
	}

	/**
	 * a bidirectional map between canonical POSs and their corresponding Penn POSs
	 */
	private static final SimpleValueSetMap<CanonicalPosTag, PennPosTag> PENN_TO_CANONICAL_MAP = new SimpleValueSetMap<CanonicalPosTag, PennPosTag>();
	static
	{
		// map between all canonical POSs and Penn POSs
		
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.CONJ, PennPosTag.CC);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.CARD, PennPosTag.CD);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.DT);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.EX);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.FW);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.IN);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, PennPosTag.JJ);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, PennPosTag.JJR);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, PennPosTag.JJS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.LS); // letters and numbers when they are used to identify items in a list.
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.MD); // All verbs that do not take an "s" in third person singular present: can, could, (dare), may, might, must, ought, shall, should, will, would
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NN, PennPosTag.NN); // Noun, singular or mass
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NP, PennPosTag.NNP); // Proper noun, singular
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NP, PennPosTag.NNPS); // Proper noun, plural
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NN, PennPosTag.NNS); // Noun, plural
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NP, PennPosTag.NP); // TreeTagger NP==>NNP
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.NP, PennPosTag.NPS); // TreeTagger NPS==>NNPS
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.PDT); // *such* a good time. *all* the girls.
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.POS); // Possessive ending: John/NNP 's/POS idea.
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, PennPosTag.PP$); // TreeTagger PP$==>PRP$
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, PennPosTag.PP); // TreeTagger PP==>PRP
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, PennPosTag.PRP$); // Possessive pronoun. my, your, one's. Note that "mine", "ours" etc. are tagged as personal pronoun (PRP).
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, PennPosTag.PRP); // Personal pronoun. I, you, etc.
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.RB); // Adverb
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.RBR); // Adverb comparative. We are farther/RBR from home.
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.RBS);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.RP); // RP is particle. Bring the girls right up/RP. "off" in "badly off", "better off", etc. "to run it up/RP".
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.SENT); // TreeTagger. No parallel tag in Penn tags. In Penn, the end of sentence is given "as is" (the tag of "." is ".").
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.SYM);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.TO); // From Penn tag guide: "To is tagged TO, regardless of whether it is a preposition or an infinitival marker"
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.O, PennPosTag.UH); // Oh, Please, uh, well, yes. "My" in "My, what a gorgeous day."
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VB);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBD);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBG);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBN);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBP);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBZ);
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VH); // TreeTagger VH==>VB
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VHD); // TreeTagger VHD==>VBD
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VHG); // TreeTagger VHG==>VBG
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VHN); // TreeTagger VHN==>VBN
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VHP); // TreeTagger VHP==>VBP
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VHZ); // TreeTagger VHZ==>VBZ
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VV); // TreeTagger VV==>VB
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VVD); // TreeTagger VVD==>VBD
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VVG); // TreeTagger VVG==>VBG
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VVN); // TreeTagger VVN==>VBN
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VVP); // TreeTagger VVP==>VBP
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VVZ); // TreeTagger VVZ==>VBZ
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.WDT); // "Which" and "that" when used as a relative pronoun. "What/WDT kind do you want?", "Which one do you like better?" 
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, PennPosTag.WP$); // "whose"
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, PennPosTag.WP); // "what", "who", "whom". "Tell me what/WP you like to eat."
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.WRB); // "how", "where", "why". "when" if it is used as termporal sense. In the sense of "if" it is "IN".
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.PUNC);	// not a Penn Treebank pos-tag
		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.SYM1);	// symbols not included in Penn Treebank SYM


		
		///////     OLD WRONG LIST     ///////
		
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBD);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBG);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBN);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBP);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.V, PennPosTag.VBZ);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.N, PennPosTag.NN);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.N, PennPosTag.NNS);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.N, PennPosTag.NNP);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.N, PennPosTag.NNPS);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, PennPosTag.JJ);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, PennPosTag.JJR);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, PennPosTag.JJS);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.DT);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.WDT);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, PennPosTag.PDT);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.PRP);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.PRP$);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.WP);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.WP$);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.RB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.RBR);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.RBS);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, PennPosTag.WRB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.CC);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.IN);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.RP);
//			// particles, like not, infinitival to, anyway... see http://en.wikipedia.org/wiki/Grammatical_particle
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, PennPosTag.TO);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.PUNC);	// not a Penn Treebank pos-tag
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.RCB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.LCB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.LRB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.RRB);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, PennPosTag.LS);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.SYM);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.SYM1);	// symbols not included in Penn Treebank SYM
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.CD);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.EX);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.FW);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.MD);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.POS);
//		PENN_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, PennPosTag.UH);
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