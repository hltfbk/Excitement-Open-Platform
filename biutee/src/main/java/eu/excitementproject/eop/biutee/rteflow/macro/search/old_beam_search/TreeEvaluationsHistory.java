package eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search;
import java.util.Vector;

import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Jun 10, 2011
 *
 */
public class TreeEvaluationsHistory
{
	public TreeEvaluationsHistory(TreeEvaluationsHistory previous, SingleTreeEvaluations currnet) throws TeEngineMlException
	{
		if (null==previous) throw new TeEngineMlException("Null previous");
		if (null==currnet) throw new TeEngineMlException("Null currnet");
		evaluations = new Vector<SingleTreeEvaluations>(previous.getEvaluations().size()+1);
		this.evaluations.addAll(previous.getEvaluations());
		this.evaluations.add(currnet);
	}
	
	public TreeEvaluationsHistory(SingleTreeEvaluations currnet) throws TeEngineMlException
	{
		if (null==currnet) throw new TeEngineMlException("Null currnet");
		evaluations = new Vector<SingleTreeEvaluations>(1);
		evaluations.add(currnet);
	}

	
	public Vector<SingleTreeEvaluations> getEvaluations()
	{
		return evaluations;
	}


	private Vector<SingleTreeEvaluations> evaluations;
}
