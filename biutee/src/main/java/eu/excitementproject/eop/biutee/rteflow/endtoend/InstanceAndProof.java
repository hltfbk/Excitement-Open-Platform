package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.Serializable;

/**
 * An {@link Instance} with a {@link Proof}.
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 * @param <P>
 */
public class InstanceAndProof<I extends Instance, P extends Proof> implements Serializable
{
	private static final long serialVersionUID = 2855069839358780144L;
	
	public InstanceAndProof(I instance, P proof)
	{
		super();
		this.instance = instance;
		this.proof = proof;
	}
	
	
	public I getInstance()
	{
		return instance;
	}
	public P getProof()
	{
		return proof;
	}

	private final I instance;
	private final P proof;
}
