package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * Given instances (T-H pairs) and their proofs, create a {@link Results} object
 * for them (for which the method {@link Results#compute()} should be called to
 * get actual scores).
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
