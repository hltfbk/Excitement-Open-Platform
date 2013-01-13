package eu.excitementproject.eop.common.representation.partofspeech;


/**
 * This is a singleton PartOfSpeech, with no public constructor, which returns static instance(s) of POSs that don't really describe any 
 * part of speech, but hold special meaning in the context of a {@link PartOfSpeech}. Like a <i>wildcard</i> POS. 
 * 
 * @author Amnon Lotan
 * @since 26/04/2011
 * 
 */
public class WildcardPartOfSpeech extends PartOfSpeech 
{
	private static final long serialVersionUID = -341996501929041672L;

	/**
	 *	DO NOT CHANGE!	This string is used in many manual XML files of syntactic entailment rules and annotation rules 
	 */
	public static final String WILDCARD_POS_STR = "*";
	
	// construct the static POSs of this class 
	private static WildcardPartOfSpeech myWildcardPOS;
	static
	{
		try {
			myWildcardPOS = new WildcardPartOfSpeech(WILDCARD_POS_STR) ;
		} catch (UnsupportedPosTagStringException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * private Ctor
	 * @param string 
	 * @param posTagString
	 * @throws UnsupportedPosTagStringException
	 */
	private WildcardPartOfSpeech(String string) throws UnsupportedPosTagStringException {
		super(string);
	}
	
	/**
	 * the only way to get your hands on a WildcardPastOfSpeech instance
	 * @return
	 */
	public static WildcardPartOfSpeech getWildcardPOS()
	{
		return myWildcardPOS;
	}
	
	/**
	 * tests whether or not this PartOfSpeech is a wildcard
	 * @param pos
	 * @return
	 */
	public static boolean isWildCardPOS(PartOfSpeech pos)
	{
		return myWildcardPOS.equals(pos);
	}

	public PartOfSpeech createNewPartOfSpeech(String posTagString)
			throws UnsupportedPosTagStringException {
		return myWildcardPOS;
	}
	
	@Override
	protected void setCanonicalPosTag() {
		canonicalPosTag = CanonicalPosTag.OTHER;
	}

	@Override
	protected void validatePosTagString(String posTagString)
			throws UnsupportedPosTagStringException 
	{}
}
