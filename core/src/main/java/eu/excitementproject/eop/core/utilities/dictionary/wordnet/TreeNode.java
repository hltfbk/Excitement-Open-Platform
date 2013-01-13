package eu.excitementproject.eop.core.utilities.dictionary.wordnet;
import java.util.Set;

/**
 * A tree node.
 * Currently this class is not used. However, if
 * in the future a WordNet's method that returns a tree
 * of WordNet's synsets will be declared - it can use
 * this class to represent the tree.  
 * @author Asher Stern
 *
 * @param <T>
 */
public abstract class TreeNode<T>
{
	public abstract T getContents();
	public abstract Set<TreeNode<T>> getChildren();
	
}
