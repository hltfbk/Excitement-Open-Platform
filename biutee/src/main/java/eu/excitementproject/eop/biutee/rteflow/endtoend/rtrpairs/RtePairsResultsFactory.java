package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class RtePairsResultsFactory extends ResultsFactory<THPairInstance, THPairProof>
{
	@Override
	public Results<THPairInstance, THPairProof> createResults(
			List<InstanceAndProof<THPairInstance, THPairProof>> proofs,
			Classifier classifierForPredictions) throws BiuteeException
	{
		return new RtePairsResults(proofs, classifierForPredictions, false);
	}
}
