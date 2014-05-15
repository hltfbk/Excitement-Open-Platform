/**
 * 
 */
package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;

/**
 * Sanity check dataset sentences/lines and correct (what seem to be) small errors.
 * 
 * @author Amnon
 * @since Aug 23, 2011
 * 
 */
public class TextualNoiseReducer {

	/**
	 * 
	 */
	private static final String UNDERSCORE_STR = "_";
	/**
	 * 
	 */
	private static final char UNDERSCORE = '_';
	/**
	 * 
	 */
	private static final char COMMA = ',';

	/**
	 * Eliminate erroneous underscores that open sentences and replace underscores between words with commas.
	 * 
	 * @param textSentence
	 * @return
	 */
	public static String reduceNoise(String textSentence) {
		while(textSentence.startsWith(UNDERSCORE_STR))
			textSentence = textSentence.substring(1);
		textSentence = textSentence.replace(UNDERSCORE, COMMA);
		return textSentence;
	}
	

}
