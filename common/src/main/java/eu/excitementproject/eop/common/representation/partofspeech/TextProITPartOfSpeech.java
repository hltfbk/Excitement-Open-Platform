
package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * Class that sets the Part of Speech tags for the Italian TextPro tagset 
 * Maps each Italian POS tag onto the corresponding 
 * {@link CanonicalPosTag}.  
 * 
 * Edited version of DKProPartOfSpeech
 * 
 * @author vivi@fbk
 * 
 */
public class TextProITPartOfSpeech extends PartOfSpeech 
{
	
	private static final long serialVersionUID = 6238674224249428653L;

	/**
	 * All Italian TextPro pos tags 
	 */
	public enum TextProITPosTag {
		A, AS, AP, AN, 
		B, 
		C, CADV, CCHE, CCHI, 
		D, DS, DP, DN, 
		E, ES, EP, 
		I, 
		N, 
		P, PS, PP, PN, 
		R, RS, RP, 
		S, SS, SP, SN, SPN, 
		Q, QNS, QNP, SYM, 
		V, VI, VIY, VF, VFY, VSP, VSPY, VPP, VPPY, VG, VGY, VM, VMY,
		VFE, VFYE, VGE, VME, VPPE, VSPE, // these correspond to composite POS: VF+E, ...
		X, XPB, XPO, XPS, XPW, 
		Y, YA, YF,
		other
	};

	public static final Set<String> PUNCTUATION;
	public static final Set<String> SYMBOLS;
	static
	{
		PUNCTUATION = Utils.arrayToCollection(
				new String[]{},
				new HashSet<String>());
		
		SYMBOLS = Utils.arrayToCollection(new String[]{"#", "$"}, new HashSet<String>());
	}

	/**
	 * a bidirectional map between canonical POSs and their corresponding Italian TextPro POSs
	 */
	private static final SimpleValueSetMap<CanonicalPosTag, TextProITPosTag> TEXTPRO_IT_TO_CANONICAL_MAP = new SimpleValueSetMap<CanonicalPosTag, TextProITPosTag>();
	static
	{
		// map between all canonical POSs and DKPro POSs

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.V);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VI);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VIY);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VF);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VFY);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VSP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VSPY);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VPP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VPPY);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VG);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VGY);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VM);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VMY);

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VFE);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VFYE);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VGE);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VME);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VPPE);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.V, TextProITPosTag.VSPE);

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProITPosTag.S);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProITPosTag.SS);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProITPosTag.SP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProITPosTag.SN);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.N, TextProITPosTag.SPN);

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.A);				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.AN);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.AP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.AS);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.DN);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.DP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, TextProITPosTag.DS);

				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, TextProITPosTag.R);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, TextProITPosTag.RP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, TextProITPosTag.RS);
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProITPosTag.E);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProITPosTag.EP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProITPosTag.ES);
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, TextProITPosTag.B);
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProITPosTag.C);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, TextProITPosTag.CADV);
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.CCHE);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.CCHI);
				
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.P);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.PN);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.PP);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.PS);
				

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.Q);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.QNS);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PR, TextProITPosTag.QNP);

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.CARD, TextProITPosTag.N);
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProITPosTag.SYM);
				
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProITPosTag.X);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProITPosTag.XPO);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProITPosTag.XPS);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, TextProITPosTag.XPW);

				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProITPosTag.I);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProITPosTag.YA);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProITPosTag.YF);
				TEXTPRO_IT_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, TextProITPosTag.other);

	}
	
	// -----------------------------------------------------------------


	/**
	 * A constructor receiving a string and converts it to DKPro pos tag.
	 * When possible, use the constructor that accepts <code>TextProITPosTag</code> to identify errors at compilation time
	 * @param posTagString - a string that conforms with the DKPro pos tag set
	 * @throws UnsupportedPosTagString - in case a non valid string is sent
	 */
	public TextProITPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException {
		super(posTagString);
	}

	/**
	 * A constructor receiving a DKPro pos tag. This is a safer version than the one receiving string. When possible use this one.
	 * 
	 * @param dkproPos - one of the values of the DKPro tag set
	 * @throws UnsupportedPosTagString
	 */
	public TextProITPartOfSpeech(TextProITPosTag textproITPos) throws UnsupportedPosTagStringException {
		super(textproITPos.name());
		this.posTagString = textproITPos.name().toUpperCase();
	}
	
	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		TextProITPartOfSpeech ret = new TextProITPartOfSpeech(posTagString);
		return ret;
	}

	/**
	 * get the canonical Pos corresponding to the given TextPro pos
	 * 
	 * @param dkproPos
	 * @return
	 */
	public static CanonicalPosTag textproITPosToCannonical(TextProITPosTag textproITPos)
	{
		return TEXTPRO_IT_TO_CANONICAL_MAP.getKeysOf(textproITPos).iterator().next();
	}
	
	/**
	 * get the set of DKPro POSs corresponding to the given canonical Pos
	 * 
	 * @param canonPos
	 * @return
	 */
	public static ImmutableSet<TextProITPosTag> canonicalPosToTextProIT(CanonicalPosTag canonPos)
	{
		return TEXTPRO_IT_TO_CANONICAL_MAP.get(canonPos);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.representation.PartOfSpeech#setCanonicalPosTag()
	 */
	@Override
	protected void setCanonicalPosTag()
	{
		setTextProITPos(posTagString);
		if (null==_textproITPos)
			this.canonicalPosTag = CanonicalPosTag.OTHER;
		else
		{
			// look it up in the DKPro to Canonicals map
			this.canonicalPosTag = TEXTPRO_IT_TO_CANONICAL_MAP.containsValue(_textproITPos) ? TEXTPRO_IT_TO_CANONICAL_MAP.getKeysOf(_textproITPos).iterator().next()
					:	CanonicalPosTag.OTHER;	// default
		}
	}
	
	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException 
	{
		setTextProITPos(posTagString);
		if (_textproITPos==null)
			throw new UnsupportedPosTagStringException("pos tag '" + posTagString + "' is unsupported Italian TextPro pos tag");
	}
	
	protected void setTextProITPos(String posTagString)
	{
//		System.out.println("POS: " + posTagString);
		if (_textproITPos==null)
		{
			if (PUNCTUATION.contains(posTagString)){
				_textproITPos = TextProITPosTag.XPS;
			} else {
				if (posTagString.contains("+")) { // for composite POS (e.g. VF+E)
					posTagString.replaceAll("+", "");
				}

				try 
				{
					_textproITPos = TextProITPosTag.valueOf(posTagString);
				} catch (IllegalArgumentException e) 
				{
					_textproITPos = null;
				}
			}
		}
	}
	
	private TextProITPosTag _textproITPos = null;
}