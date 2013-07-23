package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class Results<I extends Instance, P extends Proof>
{
	protected Results(List<InstanceAndProof<I, P>> proofs, Classifier classifierForPredictions) throws BiuteeException
	{
		super();
		this.proofs = proofs;
		this.classifierForPredictions = classifierForPredictions;
	}
	
	public abstract void compute() throws BiuteeException;
	
	public abstract Double getSuccessRate() throws BiuteeException;
	public abstract String print() throws BiuteeException;
	public abstract Iterator<String> instanceDetailsIterator() throws BiuteeException;
	public abstract void save(File file) throws BiuteeException;
	
	protected final List<InstanceAndProof<I, P>> proofs;
	protected final Classifier classifierForPredictions;
}
