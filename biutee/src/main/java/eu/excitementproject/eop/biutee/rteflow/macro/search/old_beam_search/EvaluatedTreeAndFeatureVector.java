package eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;


/**
 * Used by {@link BeamSearchTextTreesProcessor}. Used to sort trees by their "evaluation",
 * which is an estimation composed of the classification value of the tree's feature-vector,
 * and a portion of missing elements in that tree with respect to the hypothesis' tree.
 * 
 * @author Asher Stern
 * 
 *
 */
public class EvaluatedTreeAndFeatureVector implements Comparable<EvaluatedTreeAndFeatureVector>
{
	public EvaluatedTreeAndFeatureVector(double evaluation, TreeAndFeatureVector treeAndFeatureVector)
	{
		this.evaluation = evaluation;
		this.treeAndFeatureVector = treeAndFeatureVector;
	}
	
	
	public double getEvaluation()
	{
		return evaluation;
	}



	public TreeAndFeatureVector getTreeAndFeatureVector()
	{
		return treeAndFeatureVector;
	}



	public int compareTo(EvaluatedTreeAndFeatureVector o)
	{
		if (evaluation<o.evaluation)
			return (-1);
		else if (evaluation==o.evaluation)
			return 0;
		else
			return 1;
	}
	
	private double evaluation;
	private TreeAndFeatureVector treeAndFeatureVector;
}
