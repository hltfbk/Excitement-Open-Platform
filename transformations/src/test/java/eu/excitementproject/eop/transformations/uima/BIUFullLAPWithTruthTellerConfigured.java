package eu.excitementproject.eop.transformations.uima;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.test.BiuTestParams;

/***
 * A version of {@link BIUFullLAPWithTruthTeller} that is configured to paths relative to the biutee/workdir folder.
 * Should be used only for testing, as these values should be read for configuration in other scenarios.
 * 
 * @author Gabi Stanovsky
 * @since August 2014
 */


public class BIUFullLAPWithTruthTellerConfigured extends BIUFullLAPWithTruthTeller {
	public BIUFullLAPWithTruthTellerConfigured() throws LAPException {
		super(	BiuTestParams.MAXENT_POS_TAGGER_MODEL_FILE,
				BiuTestParams.STANFORD_NER_CLASSIFIER_PATH,
				BiuTestParams.EASYFIRST_HOST,
				BiuTestParams.EASYFIRST_PORT,
				BiuTestParams.TRUTH_TELLER_MODEL_FILE);
	}

}
