package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.utilities.TreeHistoryUtilities;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class THPairProof extends Proof
{
	private static final long serialVersionUID = 3098646256825860528L;
	
	public THPairProof(TreeAndFeatureVector treeAndFeatureVector,
			String bestSentence, TreeHistory history)
	{
		super();
		this.treeAndFeatureVector = treeAndFeatureVector;
		this.bestSentence = bestSentence;
		this.history = history;
	}

	@Override
	public String toString()
	{
		return TreeHistoryUtilities.historyToString(history);
	}

	@Override
	public Map<Integer, Double> getFeatureVector()
	{
		return treeAndFeatureVector.getFeatureVector();
	}
	
	
	

	public TreeAndFeatureVector getTreeAndFeatureVector()
	{
		return treeAndFeatureVector;
	}

	public String getBestSentence()
	{
		return bestSentence;
	}

	public TreeHistory getHistory()
	{
		return history;
	}




	private final TreeAndFeatureVector treeAndFeatureVector;
	private final String bestSentence;
	private final TreeHistory history;
}
