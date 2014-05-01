package eu.excitementproject.eop.lap.biu.uima;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.test.BIUFullLAPConfigured;

public class BIUFullLAP_Test extends BIU_LAP_Test {
	@Override
	protected LAPAccess getLAP() throws LAPException {
		return new BIUFullLAPConfigured();
	}
}
