package eu.excitementproject.eop.lap.biu.uima;

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
		super(	"../third-party/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger",
				"../third-party/stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz",
				"localhost",
				8080);
	}
}
