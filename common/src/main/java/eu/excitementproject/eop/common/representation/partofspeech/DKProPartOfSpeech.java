
package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * Class that sets the Part of Speech tags for the DKPro tagset 
 * Maps each DKPro tag onto the corresponding 
 * {@link CanonicalPosTag}.  
 * 
 * @author Roberto Zanoli
 * 
 */
public class DKProPartOfSpeech extends PartOfSpeech 
{
	
	private static final long serialVersionUID = 6238674224249428653L;

	/**
	 * All DKPro pos tags
	 */
	public enum DKProPosTag {
		ADJ, ADV, CONJ, PR, PP, O, CARD, ART, NN, NP, PUNC, V, POS
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
	 * a bidirectional map between canonical POSs and their corresponding DKPro POSs
	 */
	private static final SimpleValueSetMap<CanonicalPosTag, DKProPosTag> DKPRO_TO_CANONICAL_MAP = new SimpleValueSetMap<CanonicalPosTag, DKProPosTag>();
	static
	{
		// map between all canonical POSs and DKPro POSs
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.V, DKProPosTag.V);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.N, DKProPosTag.NN);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.N, DKProPosTag.NP);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADJ, DKProPosTag.ADJ);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ART, DKProPosTag.ART);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, DKProPosTag.PR);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, DKProPosTag.PP);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.ADV, DKProPosTag.ADV);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PP, DKProPosTag.CONJ);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.PUNC, DKProPosTag.PUNC);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, DKProPosTag.CARD);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, DKProPosTag.O);
				DKPRO_TO_CANONICAL_MAP.put(CanonicalPosTag.OTHER, DKProPosTag.POS);
	}
	
	// -----------------------------------------------------------------


	/**
	 * A constructor receiving a string and converts it to DKPro pos tag.
	 * When possible, use the constructor that accepts <code>DKProPosTag</code> to identify errors at compilation time
	 * @param posTagString - a string that conforms with the DKPro pos tag set
	 * @throws UnsupportedPosTagString - in case a non valid string is sent
	 */
	public DKProPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException {
		super(posTagString);
	}

	/**
	 * A constructor receiving a DKPro pos tag. This is a safer version than the one receiving string. When possible use this one.
	 * 
	 * @param dkproPos - one of the values of the DKPro tag set
	 * @throws UnsupportedPosTagString
	 */
	public DKProPartOfSpeech(DKProPosTag dkproPos) throws UnsupportedPosTagStringException {
		super(dkproPos.name());
		this.posTagString = dkproPos.name().toUpperCase();
	}
	
	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		DKProPartOfSpeech ret = new DKProPartOfSpeech(posTagString);
		return ret;
	}

	/**
	 * get the canonical Pos corresponding to the given DKPro pos
	 * 
	 * @param dkproPos
	 * @return
	 */
	public static CanonicalPosTag dkproPosToCannonical(DKProPosTag dkproPos)
	{
		return DKPRO_TO_CANONICAL_MAP.getKeysOf(dkproPos).iterator().next();
	}
	
	/**
	 * get the set of DKPro POSs corresponding to the given canonical Pos
	 * 
	 * @param canonPos
	 * @return
	 */
	public static ImmutableSet<DKProPosTag> canonicalPosToDKPro(CanonicalPosTag canonPos)
	{
		return DKPRO_TO_CANONICAL_MAP.get(canonPos);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.representation.PartOfSpeech#setCanonicalPosTag()
	 */
	@Override
	protected void setCanonicalPosTag()
	{
		setDKProPos(posTagString);
		if (null==_dkproPos)
			this.canonicalPosTag = CanonicalPosTag.OTHER;
		else
		{
			// look it up in the DKPro to Canonicals map
			this.canonicalPosTag = DKPRO_TO_CANONICAL_MAP.containsValue(_dkproPos) ? DKPRO_TO_CANONICAL_MAP.getKeysOf(_dkproPos).iterator().next()
					:	CanonicalPosTag.OTHER;	// default
		}
	}
	
	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException 
	{
		setDKProPos(posTagString);
		if (_dkproPos==null)
			throw new UnsupportedPosTagStringException("pos tag '" + posTagString + "' is unsupported DKPro pos tag");
	}
	
	protected void setDKProPos(String posTagString)
	{
		if (_dkproPos==null)
		{
			_dkproPos = null;
			if (PUNCTUATION.contains(posTagString)){
				_dkproPos = DKProPosTag.PUNC;
			}
			//else if(SYMBOLS.contains(posTagString)){
				//_dkproPos = DKProPosTag.SYM1;
			//}
			else{
				try 
				{
					_dkproPos = DKProPosTag.valueOf(posTagString);
				} catch (IllegalArgumentException e) 
				{
					_dkproPos = null;
				}
			}
		}
	}
	
	private DKProPosTag _dkproPos = null;
}