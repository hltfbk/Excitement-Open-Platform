package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

public class RteSumResultsFactory extends ResultsFactory<RteSumInstance, RteSumProof>
{

	@Override
	public Results<RteSumInstance, RteSumProof> createResults(
			List<InstanceAndProof<RteSumInstance, RteSumProof>> proofs,
			Classifier classifierForPredictions) throws BiuteeException
	{
		return new RteSumResults(proofs, classifierForPredictions, true);
	}

}
