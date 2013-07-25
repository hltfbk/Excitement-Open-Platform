package eu.excitementproject.eop.biutee.rteflow.endtoend;

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
public abstract class ResultsFactory<I extends Instance, P extends Proof>
{
	public abstract Results<I, P> createResults(List<InstanceAndProof<I, P>> proofs, Classifier classifierForPredictions) throws BiuteeException;
}
