package eu.excitementproject.eop.biutee.script;
import java.io.Serializable;

import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * Stores the hypothesis sentence and its parse-tree.
 * Used by {@link OperationsScript}, in the method {@link OperationsScript#setHypothesisInformation(HypothesisInformation)}. 
 * 
 * @author Asher Stern
 * @since Jul 17, 2011
 *
 */
public class HypothesisInformation implements Serializable
{
	private static final long serialVersionUID = -6195039194680431552L;
	
	public HypothesisInformation(String hypothesisSentence,
			ExtendedNode hypothesisTree)
	{
		super();
		this.hypothesisSentence = hypothesisSentence;
		this.hypothesisTree = hypothesisTree;
	}
	
	
	
	public String getHypothesisSentence()
	{
		return hypothesisSentence;
	}
	public ExtendedNode getHypothesisTree()
	{
		return hypothesisTree;
	}

	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((hypothesisSentence == null) ? 0 : hypothesisSentence
						.hashCode());
		result = prime * result
				+ ((hypothesisTree == null) ? 0 : hypothesisTree.hashCode());
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
		HypothesisInformation other = (HypothesisInformation) obj;
		if (hypothesisSentence == null)
		{
			if (other.hypothesisSentence != null)
				return false;
		} else if (!hypothesisSentence.equals(other.hypothesisSentence))
			return false;
		if (hypothesisTree == null)
		{
			if (other.hypothesisTree != null)
				return false;
		} else if (!hypothesisTree.equals(other.hypothesisTree))
			return false;
		return true;
	}




	private final String hypothesisSentence;
	private final ExtendedNode hypothesisTree;
}
