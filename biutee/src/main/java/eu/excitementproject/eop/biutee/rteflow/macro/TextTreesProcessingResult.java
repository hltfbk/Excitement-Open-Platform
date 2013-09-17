package eu.excitementproject.eop.biutee.rteflow.macro;

/**
 * 
 * @author Asher Stern
 * @since Aug 4, 2013
 *
 */
public class TextTreesProcessingResult
{
	public TextTreesProcessingResult(TreeAndFeatureVector tree,
			String sentence, TreeHistory history)
	{
		super();
		this.tree = tree;
		this.sentence = sentence;
		this.history = history;
	}
	
	
	
	public TreeAndFeatureVector getTree()
	{
		return tree;
	}
	public String getSentence()
	{
		return sentence;
	}
	public TreeHistory getHistory()
	{
		return history;
	}



	private final TreeAndFeatureVector tree;
	private final String sentence;
	private final TreeHistory history;
}
