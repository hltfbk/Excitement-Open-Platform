package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.Serializable;
import java.util.Set;

/**
 * Represents the information about a <B>single</B> hypothesis
 * <B> in the gold standard </B> file.
 * In the gold standard, each hypothesis appears with:
 * <OL>
 * <LI>Its id</LI>
 * <LI>Its contents (the hypothesis sentence itself)</LI>
 * <LI>A set of sentences that entails it. Each
 * sentences appears as:
 * <OL>
 * <LI>The sentence identifier: document id and sentence id.</LI>
 * <LI>The sentence itself.</LI>
 * </OL>
 * </LI>
 * </OL>
 * 
 * @author Asher Stern
 *
 */
public final class HypothesisAnswer implements Serializable
{
	private static final long serialVersionUID = 2310640864419680729L;
	
	public HypothesisAnswer(String hypothesisSentence,
			Set<TextSentenceAnswer> textSentences)
	{
		super();
		this.hypothesisSentence = hypothesisSentence;
		this.textSentences = textSentences;
	}
	
	
	public String getHypothesisSentence()
	{
		return hypothesisSentence;
	}
	public Set<TextSentenceAnswer> getTextSentences()
	{
		return textSentences;
	}
	
	


	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((hypothesisSentence == null) ? 0 : hypothesisSentence
						.hashCode());
		result = prime * result
				+ ((textSentences == null) ? 0 : textSentences.hashCode());
		hashCodeValue = result;
		hashCodeSet = true;
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
		HypothesisAnswer other = (HypothesisAnswer) obj;
		if (hypothesisSentence == null)
		{
			if (other.hypothesisSentence != null)
				return false;
		} else if (!hypothesisSentence.equals(other.hypothesisSentence))
			return false;
		if (textSentences == null)
		{
			if (other.textSentences != null)
				return false;
		} else if (!textSentences.equals(other.textSentences))
			return false;
		return true;
	}




	private final String hypothesisSentence;
	private final Set<TextSentenceAnswer> textSentences;
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;
}
