package eu.excitementproject.eop.lexicalminer.Italian.language.textpro;


import java.util.HashSet;

import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * A TextPro pos-tag
 * The list is taken from: {@linkplain link http://textpro.fbk.eu/tagset_it.html}
 * Examples can be found here: {@linkplain link http://textpro.fbk.eu/tagset_it.html}
 * <p>
 * The tag's string must conform with the list. 
 * 
 * @author Alberto Lavelli
 * @since Sep 2012
 */
public class TextProPartOfSpeech extends PartOfSpeech 
{
	// -----------------------------------------------------------------------

	private static final long serialVersionUID = -4787167211959166319L;

	/**
	 * All TextPro pos tags
	 */
	public enum TextProPosTag {
	    AS, AP, AN, B, C, CADV, CCHE, CCHI, DS, DP, DN, E, ES, EP, I, N, PS, PP, PN, RS, RP, SS, SP, SN, SPN, 
		QNS, QNP, VI, VIY, VF, VFY, VSP, VSPY, VPP, VPPY, VG, VGY, VM, VMY, XPS, XPW, XPB, XPO, YA, YF
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
	 * a bidirectional map between canonical POSs and their corresponding TextPro POSs
	 */
	private static final SimpleValueSetMap<CanonicalPosTag, TextProPosTag> TEXTPRO_TO_CANONICAL_MAP = new SimpleValueSetMap<CanonicalPosTag, TextProPosTag>();
	static
	{
		// map between all canonical POSs and TextPro POSs
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VI);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VIY);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VF);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VFY);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VSP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VSPY);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VPP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VPPY);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VG);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VGY);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VM);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProPosTag.VMY);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProPosTag.SS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProPosTag.SP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProPosTag.SN);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProPosTag.SPN);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProPosTag.AS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProPosTag.AP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProPosTag.AN);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProPosTag.DS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProPosTag.DP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProPosTag.DN);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, TextProPosTag.RS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, TextProPosTag.RP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProPosTag.PS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProPosTag.PP);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProPosTag.PN);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProPosTag.QNS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProPosTag.QNP);

		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, TextProPosTag.B);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, TextProPosTag.CADV);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProPosTag.E);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProPosTag.ES);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProPosTag.EP);
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.RCB);	// not a TextPro pos-tag
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.LCB);	// not a TextPro pos-tag
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.LRB);	// not a TextPro pos-tag
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNCTUATION, TextProPosTag.RRB);	// not a TextPro pos-tag
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.XPS);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.XPW);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.XPB);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProPosTag.XPO);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.C);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.CCHE);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.CCHI);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.N);
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.EX);
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.FW);
//		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.MD);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.YA);
		TEXTPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProPosTag.YF);
	}
	
	// -----------------------------------------------------------------


	/**
	 * A constructor receiving a string and converts it to TextPro pos tag.
	 * When possible, use the constructor that accepts <code>TextProPosTag</code> to identify errors at compilation time
	 * @param posTagString - a string that conforms with the TextPro pos tag set
	 * @throws UnsupportedPosTagString - in case a non valid string is sent
	 */
	public TextProPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException {
		super(posTagString);
	}

	/**
	 * A constructor receiving a TextPro pos tag. This is a safer version than the one receiving string. When possible use this one.
	 * 
	 * @param textproPos - one of the values of the TextPro tag set
	 * @throws UnsupportedPosTagString
	 */
	public TextProPartOfSpeech(TextProPosTag textproPos) throws UnsupportedPosTagStringException {
		super(textproPos.name());
		this.posTagString = textproPos.name().toUpperCase();
	}
	
	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		TextProPartOfSpeech ret = new TextProPartOfSpeech(posTagString);
		return ret;
	}

	/**
	 * get the canonical Pos corresponding to the given TextPro pos
	 * 
	 * @param textproPos
	 * @return
	 */
	public static CanonicalPosTag textproPosToCannonical(TextProPosTag textproPos)
	{
		return TEXTPRO_TO_CANONICAL_MAP.getKeysOf(textproPos).iterator().next();
	}
	
	/**
	 * get the set of TextPro POSs corresponding to the given canonical Pos
	 * 
	 * @param canonPos
	 * @return
	 */
	public static ImmutableSet<TextProPosTag> canonicalPosToTextPro(CanonicalPosTag canonPos)
	{
		return TEXTPRO_TO_CANONICAL_MAP.get(canonPos);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.representation.PartOfSpeech#setCanonicalPosTag()
	 */
	@Override
	protected void setCanonicalPosTag()
	{
		setTextProPos(posTagString);
		if (null==_textproPos)
			this.canonicalPosTag = CanonicalPosTag.OTHER;
		else
		{
			// look it up in the TextPro to Canonicals map
			this.canonicalPosTag = TEXTPRO_TO_CANONICAL_MAP.containsValue(_textproPos) ? TEXTPRO_TO_CANONICAL_MAP.getKeysOf(_textproPos).iterator().next()
					:	CanonicalPosTag.OTHER;	// default
		}
	}
	
	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException 
	{
		setTextProPos(posTagString);
		if (_textproPos==null)
			throw new UnsupportedPosTagStringException("pos tag '" + posTagString + "' is unsupported TextPro pos tag");
	}
	
	protected void setTextProPos(String posTagString)
	{
		if (_textproPos==null)
		{
		    // Modified by Alessio Palmero Aprosio (2012-09-24)
		    String[] parts = posTagString.toUpperCase().split("\\+");
    		posTagString = parts[0];
    		
			_textproPos = null;
			if (PUNCTUATION.contains(posTagString)){
//				_textproPos = TextProPosTag.PUNC;
				_textproPos = TextProPosTag.XPB;
			}
			else if(SYMBOLS.contains(posTagString)){
//				_textproPos = TextProPosTag.SYM1;
				_textproPos = TextProPosTag.C;
			}else{
				try 
				{
					_textproPos = TextProPosTag.valueOf(posTagString);
				} catch (IllegalArgumentException e) 
				{
					_textproPos = null;
				}
			}
		}
	}
	
	private TextProPosTag _textproPos = null;
}