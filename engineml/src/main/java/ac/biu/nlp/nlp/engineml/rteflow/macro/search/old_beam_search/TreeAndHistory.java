package ac.biu.nlp.nlp.engineml.rteflow.macro.search.old_beam_search;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;

/**
 * Encapsulates a tree, its feature-vector, and its history (history is a list
 * of operations done on that tree, represented as {@link Specification}).
 * 
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class TreeAndHistory
{
	public TreeAndHistory(TreeAndFeatureVector tree, TreeHistory history) throws TeEngineMlException
	{
		super();
		if (null==tree) throw new TeEngineMlException("Null tree");
		if (null==history) throw new TeEngineMlException("Null history");
		
		this.tree = tree;
		this.history = history;
	}
	
	public TreeAndFeatureVector getTree()
	{
		return tree;
	}
	public TreeHistory getHistory()
	{
		return history;
	}
	
	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		result = prime * result + ((tree == null) ? 0 : tree.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeAndHistory other = (TreeAndHistory) obj;
		if (history == null)
		{
			if (other.history != null)
				return false;
		} else if (!history.equals(other.history))
			return false;
		if (tree == null)
		{
			if (other.tree != null)
				return false;
		} else if (!tree.equals(other.tree))
			return false;
		return true;
	}




	private final TreeAndFeatureVector tree;
	private final TreeHistory history;
}
