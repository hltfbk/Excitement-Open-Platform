package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;


/**
 * Holds a tree and a sentence (from which that tree was created).
 * 
 * @author Asher Stern
 * @since October 2, 2012
 *
 */
public class TreeAndSentence implements Serializable
{
	private static final long serialVersionUID = -6342877867677633913L;
	
	public TreeAndSentence(String sentence, BasicNode tree)
	{
		super();
		this.sentence = sentence;
		this.tree = tree;
	}
	
	
	
	public String getSentence()
	{
		return sentence;
	}
	public BasicNode getTree()
	{
		return tree;
	}



	private final String sentence;
	private final BasicNode tree;
}
