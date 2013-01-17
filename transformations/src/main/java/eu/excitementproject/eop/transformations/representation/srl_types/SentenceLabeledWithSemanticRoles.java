package eu.excitementproject.eop.transformations.representation.srl_types;

import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2011
 *
 */
public class SentenceLabeledWithSemanticRoles implements Serializable
{
	private static final long serialVersionUID = -6683873824039015807L;

	public SentenceLabeledWithSemanticRoles(WordLabeledWithSemanticRole[] words)
	{
		super();
		this.words = words;
	}
	
	public WordLabeledWithSemanticRole getWord(int index)
	{
		return words[index];
	}
	
	public int getSentenceLength()
	{
		return words.length;
	}

	private final WordLabeledWithSemanticRole[] words;
}
