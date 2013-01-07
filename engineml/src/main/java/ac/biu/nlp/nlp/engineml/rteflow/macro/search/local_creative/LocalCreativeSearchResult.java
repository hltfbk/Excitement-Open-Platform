package ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative;

import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;

public class LocalCreativeSearchResult
{
	public LocalCreativeSearchResult(TreeAndFeatureVector tree,
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
