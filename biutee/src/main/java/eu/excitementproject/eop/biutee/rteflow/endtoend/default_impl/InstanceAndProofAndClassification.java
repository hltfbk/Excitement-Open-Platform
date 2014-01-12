package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.io.Serializable;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 * @param <I>
 * @param <P>
 */
public class InstanceAndProofAndClassification<I extends Instance, P extends Proof> implements Serializable
{
	private static final long serialVersionUID = -1675166937988070570L;
	
	public InstanceAndProofAndClassification(
			InstanceAndProof<I, P> instanceAndProof, double score,
			boolean classification)
	{
		super();
		this.instanceAndProof = instanceAndProof;
		this.score = score;
		this.classification = classification;
	}
	
	
	
	public InstanceAndProof<I, P> getInstanceAndProof()
	{
		return instanceAndProof;
	}
	public double getScore()
	{
		return score;
	}
	public boolean getClassification()
	{
		return classification;
	}



	private final InstanceAndProof<I, P> instanceAndProof;
	private final double score;
	private final boolean classification;
}
