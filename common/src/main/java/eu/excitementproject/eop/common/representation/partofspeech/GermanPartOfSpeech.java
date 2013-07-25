
package eu.excitementproject.eop.common.representation.partofspeech;



/**
 * Class that sets the Part of Speech tags for the German tagset 
 * called STTS. Maps each STTS tag onto the corresponding 
 * {@link CanonicalPosTag}.  
 * 
 * @author zeller
 *
 */
public class GermanPartOfSpeech extends PartOfSpeech {

	private static final long serialVersionUID = 6295826063805145349L;

	public GermanPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
	}


	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException {
		return new GermanPartOfSpeech(posTagString);
	}

	/**
	 * Assuming the field {@link #posTagString} is already set, this function chooses
	 * one of the appropriate values of {@link CanonicalPosTag} enumeration, and <B>sets
	 * the field</B> {@link #canonicalPosTag}.
	 * @throws UnsupportedPosTagStringException if the given string is not supported
	 */
	protected void setCanonicalPosTag() {
		assert posTagString != null && !posTagString.equals("");

		posTagString = mapOntoStts(posTagString);
		
		try {
			canonicalPosTag = CanonicalPosTag.valueOf(posTagString.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			canonicalPosTag = CanonicalPosTag.OTHER;
		}
	}
	
	

	/**
	 * Maps the STTS tags onto the corresponding {@link CanonicalPosTag}s.
	 * 
	 * @param posTagString a string that represents part-of-speech in STTS format.
	 * @return the {@link CanonicalPosTag} that corresponds to the given STTS tag.
	 */
	
	// Mapping STTS -> DKPro POS can be found at 
	// http://code.google.com/p/dkpro-core-asl/source/browse/de.tudarmstadt.ukp.dkpro.core-asl/trunk/de.tudarmstadt.ukp.dkpro.core.api.lexmorph-asl/src/main/resources/de/tudarmstadt/ukp/dkpro/core/api/lexmorph/tagset/de-stts-tagger.map
	private String mapOntoStts(String posTagString) {

		if (posTagString.startsWith("ADJ")) {
			posTagString = "ADJ";
		} else if (posTagString.equals("ADV")) {
			posTagString = "ADV";
		} else if (posTagString.startsWith("KO")) {
			posTagString = "CONJ";
		} else if (posTagString.equals("NE") ) {
			posTagString = "NP";
		} else if (posTagString.equals("ITJ") || posTagString.equals("FM")) {
			posTagString = "O"; 
		} else if (posTagString.startsWith("AP")) {
			posTagString = "PP";
		} else if (posTagString.startsWith("$")) {
			posTagString = "PUNC";
		} else if (posTagString.startsWith("P") && !posTagString.startsWith("PTK")) {
			if (posTagString.equals("PAV")) {
				posTagString = "ADV";
			} else {
				posTagString = "PR";
			}
		} else if (posTagString.equals("PTKZU")) {
			posTagString = "PR";
		} else if (posTagString.equals("PTKA")) {
			posTagString = "ADV";
		//} else if (posTagString.equals("TRUNC")) {
			//posTagString = "O";
		} else if (posTagString.startsWith("V") || posTagString.equals("PTKVZ")) {
			posTagString = "V";
		} else if (posTagString.equals("XY")) {
			posTagString = "O";
		} else if (posTagString.equals("PTKNEG")) {
			posTagString = "O"; 
		} else if (posTagString.equals("TRUNC") || posTagString.equals("PTKANT")) {
			// Rui: quick fix for the "illegal" POS tag
			posTagString = "O"; 
		}
		
		// do nothing for "ART", "CARD", "NN", since it has correct form already
		
		return posTagString;
	}


	/**
	 * Validates that the given string is one of the expected strings for that
	 * sub-class of {@link PartOfSpeech}.
	 * @param posTagString a string that represents part-of-speech. Usually, that string
	 * is given by an external resource, e.g. pos-tagger.
	 * @throws UnsupportedPosTagStringException if the string is not supported.
	 */
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException {
		
		posTagString = mapOntoStts(posTagString);
		
		try {
			CanonicalPosTag.valueOf(posTagString.toUpperCase());
		}
		catch (NullPointerException e) {
			throw new UnsupportedPosTagStringException("posTagString must not be null");
		}
		catch (IllegalArgumentException e) {
			// Rui: I changed this for debugging usage
			throw new UnsupportedPosTagStringException(posTagString + " is not a valid GermanPartOfSpeech specifier");
		}

	}
}

