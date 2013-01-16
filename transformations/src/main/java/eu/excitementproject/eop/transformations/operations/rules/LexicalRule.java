package eu.excitementproject.eop.transformations.operations.rules;
import java.io.Serializable;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * Lexical rule is:
 * lemma and part-of-speech for the left hand side,
 * lemma and part-of-speech for the left hand side,
 * and confidence (in the interval [0,1])
 * 
 * @author Asher Stern
 * @since Feb 2, 2011
 *
 */
public class LexicalRule implements Serializable
{
	private static final long serialVersionUID = 6552337423457684366L;

	public LexicalRule(String lhsLemma, PartOfSpeech lhsPos, String rhsLemma, PartOfSpeech rhsPos, double confidence)
	{
		super();
		this.lhsLemma = lhsLemma;
		this.lhsPos = lhsPos;
		this.rhsLemma = rhsLemma;
		this.rhsPos = rhsPos;
		this.confidence = confidence;
	}
	
	

	public String getLhsLemma()
	{
		return lhsLemma;
	}

	public PartOfSpeech getLhsPos()
	{
		return lhsPos;
	}

	public String getRhsLemma()
	{
		return rhsLemma;
	}

	public PartOfSpeech getRhsPos()
	{
		return rhsPos;
	}

	public double getConfidence()
	{
		return confidence;
	}
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(confidence);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((lhsLemma == null) ? 0 : lhsLemma.hashCode());
		result = prime * result + ((lhsPos == null) ? 0 : lhsPos.hashCode());
		result = prime * result
				+ ((rhsLemma == null) ? 0 : rhsLemma.hashCode());
		result = prime * result + ((rhsPos == null) ? 0 : rhsPos.hashCode());
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
		LexicalRule other = (LexicalRule) obj;
		if (Double.doubleToLongBits(confidence) != Double
				.doubleToLongBits(other.confidence))
			return false;
		if (lhsLemma == null)
		{
			if (other.lhsLemma != null)
				return false;
		} else if (!lhsLemma.equals(other.lhsLemma))
			return false;
		if (lhsPos == null)
		{
			if (other.lhsPos != null)
				return false;
		} else if (!lhsPos.equals(other.lhsPos))
			return false;
		if (rhsLemma == null)
		{
			if (other.rhsLemma != null)
				return false;
		} else if (!rhsLemma.equals(other.rhsLemma))
			return false;
		if (rhsPos == null)
		{
			if (other.rhsPos != null)
				return false;
		} else if (!rhsPos.equals(other.rhsPos))
			return false;
		return true;
	}

	
	



	@Override
	public String toString() {
		return getLhsLemma() + ":" + getLhsPos() + "\t" + getRhsLemma() + ":" + getRhsPos()
				+ "\t" + getConfidence();
	}






	private final String lhsLemma;
	private final PartOfSpeech lhsPos;
	
	private final String rhsLemma;
	private final PartOfSpeech rhsPos;
	
	private final double confidence;
}
