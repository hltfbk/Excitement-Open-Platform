package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultAbstractResults;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.InstanceAndProofAndClassification;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ResultsToXml;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ResultsToXml.ScoreAndRTEClassificationType;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEClassificationType;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class RtePairsResults extends DefaultAbstractResults<THPairInstance, THPairProof>
{
	public RtePairsResults(
			List<InstanceAndProof<THPairInstance, THPairProof>> proofs,
			Classifier classifierForPredictions, boolean f1_optimized)
			throws BiuteeException
	{
		super(proofs, classifierForPredictions, f1_optimized);
	}
	

	@Override
	public void save(File file) throws BiuteeException
	{
		Map<String, ScoreAndRTEClassificationType> mapResults = new LinkedHashMap<>();
		for (InstanceAndProofAndClassification<THPairInstance, THPairProof> classification : classifications)
		{
			RTEClassificationType type = classification.getClassification()?RTEClassificationType.ENTAILMENT:RTEClassificationType.UNKNOWN;
			ScoreAndRTEClassificationType scoreAndRTEClassificationType =
					new ScoreAndRTEClassificationType(classification.getScore(),type);
			
			Integer idInteger = classification.getInstanceAndProof().getInstance().getPairData().getPair().getId();
			String id = "null";
			if (idInteger!=null){id = String.valueOf(idInteger);}
			
			mapResults.put(id, scoreAndRTEClassificationType);
		}
		
		ResultsToXml resultsToXml = new ResultsToXml(mapResults,file);
		try
		{
			resultsToXml.output();
		}
		catch (TeEngineMlException e)
		{
			throw new BiuteeException("Failed to write the results to the given file",e);
		}
	}
	
	
	

	@Override
	protected String detailsOfProof(InstanceAndProofAndClassification<THPairInstance, THPairProof> proof) throws BiuteeException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(proof.getInstanceAndProof().getInstance().toString()).append("\n");
		sb.append("Text = ").append(proof.getInstanceAndProof().getInstance().getPairData().getPair().getText()).append("\n");
		sb.append("Hypothesis = ").append(proof.getInstanceAndProof().getInstance().getPairData().getPair().getHypothesis()).append("\n");
		Boolean gs = proof.getInstanceAndProof().getInstance().getBinaryLabel();
		boolean classification = proof.getClassification();
		double score = proof.getScore();
		sb.append("Real annotation = "+ ((gs==null)?"unknown":String.valueOf(gs.booleanValue())) ).append(", ");
		sb.append("Classification = ").append(classification).append(". Score = ").append(String.format("%-3.4f", score)).append("\n");
		sb.append("Proof:\n").append(proof.getInstanceAndProof().getProof().toString()).append("\n");
		
		return sb.toString();
	}

}
