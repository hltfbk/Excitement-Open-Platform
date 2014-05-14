package eu.excitementproject.eop.lap.biu.uima.ae.postagger;

/**
 * This class is used for accessing DKPro's Penn POS mapping (currently it
 * only provides a static strin of the .map file's location).
 * <BR>
 * May be used by POS taggers that work with Penn POS.
 * <BR>
 * 
 * <b>NOTE:</b> There are slight differences between this mapping and the mapping provided
 * by BIU's PennPartOfSpeech:
 * <ul>
 * <li>All symbols in the list PennPartOfSpeech.PUNCTUATION are mapped to PUNC in BIU, whereas in DKPro
 * only some of them do (the others by default are mapped to O).
 * <li>All symbols in the list PennPartOfSpeech.SYMBOLS go explicitly to OTHER in BIU, whereas in DKPro
 * they are implicitly mapped to O (no practical difference).
 * <li>DKPro's list of top-level POS type is slightly larger than BIU's SimplerCanonicalPosTag, so some
 * mappings are different.
 * </ul>
 * Currently this BIU code ignores these differences.
 * 
 * @author Ofer Bronstein
 * @since December 2012
 *
 */
public class PennPOSMapping {
	public static final String MAPPING_LOCATION = "classpath:/de/tudarmstadt/ukp/dkpro/" +
			"core/api/lexmorph/tagset/${language}-${tagger.tagset}-tagger.map";
}
