package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import eu.excitementproject.eop.biutee.rteflow.endtoend.TimeStatistics;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultProof;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class THPairProof extends DefaultProof
{
	private static final long serialVersionUID = -7176685728191869434L;

	public THPairProof(TreeAndFeatureVector treeAndFeatureVector, String bestSentence, TreeHistory history, GapDescription gapDescription, TimeStatistics timeStatistics)
	{
		super(treeAndFeatureVector, bestSentence, history, gapDescription, timeStatistics);
	}
}
