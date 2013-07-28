package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * Holds the entailment-decisions of a full dataset of T-H pairs.
 * After caling the constructor, the user should call {@link #compute()} to
 * classify all the proofs, and get scores (confidences) for each proof.
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
	
	/**
	 * Returns success rates (e.g., accuracy and F1) for the whole dataset. 
	 * @return the success rates.
	 * @throws BiuteeException
	 */
	public abstract Double getSuccessRate() throws BiuteeException;
	
	/**
	 * Returns a string that summarizes the results
	 * @return a string that summarizes the results
	 * @throws BiuteeException
	 */
	public abstract String print() throws BiuteeException;
	
	/**
	 * Returns an iterator of String, such that each string describes
	 * an instance (T-H pair), its proof and the classification (score and Y/N) of the proof.
	 * @return Iterator over strings that describe the proofs.
	 * @throws BiuteeException
	 */
	public abstract Iterator<String> instanceDetailsIterator() throws BiuteeException;
	
	/**
	 * Saves the results of the whole dataset into a file.
	 * The file format and its contents are implementation dependent.
	 * @param file
	 * @throws BiuteeException
	 */
	public abstract void save(File file) throws BiuteeException;
	
	protected final List<InstanceAndProof<I, P>> proofs;
	protected final Classifier classifierForPredictions;
}
