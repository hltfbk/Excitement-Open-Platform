package ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;

/**
 * Contains information about Text-Hypothesis pair - the original T-H pair and
 * the results of pre processing on it. See {@link GenericPairData}.
 * 
 * @see GenericPairData
 * 
 * @author Asher Stern
 * @since Apr 7, 2011
 *
 */
public class ExtendedPairData extends GenericPairData<ExtendedInfo, ExtendedNode> implements Serializable
{
	private static final long serialVersionUID = 9215073116781086682L;

	public ExtendedPairData(TextHypothesisPair pair,
			List<ExtendedNode> textTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> mapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			String datasetName)
	{
		super(pair, textTrees, hypothesisTree, mapTreesToSentences,
				coreferenceInformation, datasetName);
	}



	public ExtendedPairData(TextHypothesisPair pair,
			List<ExtendedNode> textTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> mapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation)
	{
		super(pair, textTrees, hypothesisTree, mapTreesToSentences, coreferenceInformation);
	}
}
