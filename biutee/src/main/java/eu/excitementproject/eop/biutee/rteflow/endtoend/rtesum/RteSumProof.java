package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultProof;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapDescription;

/**
 * 
 * @author Asher Stern
 * @since Jul 21, 2013
 *
 */
public class RteSumProof extends DefaultProof
{
	private static final long serialVersionUID = -8214560723352534128L;

	public RteSumProof(TreeAndFeatureVector treeAndFeatureVector, String bestSentence, TreeHistory history, GapDescription gapDescription)
	{
		super(treeAndFeatureVector, bestSentence, history, gapDescription);
	}

}
