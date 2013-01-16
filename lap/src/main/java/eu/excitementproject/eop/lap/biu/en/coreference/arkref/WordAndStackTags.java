package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.util.Stack;

/**
 * 
 * @author Asher Stern
 * @since Jun 21, 2012
 *
 */
public class WordAndStackTags
{
	public WordAndStackTags(String word, Stack<String> tags)
	{
		super();
		this.word = word;
		this.tags = tags;
	}
	
	public WordAndStackTags(String word)
	{
		this(word,new Stack<String>());
	}


	public String getWord()
	{
		return word;
	}
	public Stack<String> getTags()
	{
		return tags;
	}



	private final String word;
	private final Stack<String> tags;
}
