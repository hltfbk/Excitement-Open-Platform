package eu.excitementproject.eop.lap.biu.test;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.BIUAndOpenNlpLAP;

/***
 * A version of {@link BIUAndOpenNlpLAP} that is configured to paths relative to the biutee/workdir folder.
 * Should be used only for testing, as these values should be read for configuration in other scenarios.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class BIUAndOpenNlpLAPConfigured extends BIUAndOpenNlpLAP {

	public BIUAndOpenNlpLAPConfigured() throws LAPException {
		super(	BiuTestParams.STANFORD_NER_CLASSIFIER_PATH,
				BiuTestParams.EASYFIRST_HOST,
				BiuTestParams.EASYFIRST_PORT);
	}
}
