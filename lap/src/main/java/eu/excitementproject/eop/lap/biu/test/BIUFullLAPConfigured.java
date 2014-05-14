package eu.excitementproject.eop.lap.biu.test;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;

/***
 * A version of {@link BIUFullLAP} that is configured to paths relative to the biutee/workdir folder.
 * Should be used only for testing, as these values should be read for configuration in other scenarios.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class BIUFullLAPConfigured extends BIUFullLAP {

	public BIUFullLAPConfigured() throws LAPException {
		super(	BiuTestParams.MAXENT_POS_TAGGER_MODEL_FILE,
				BiuTestParams.STANFORD_NER_CLASSIFIER_PATH,
				BiuTestParams.EASYFIRST_HOST,
				BiuTestParams.EASYFIRST_PORT);
	}
}
