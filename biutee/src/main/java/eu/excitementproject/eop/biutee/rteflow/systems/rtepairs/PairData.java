package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;

/**
 * Contains information about a "pair" in the RTE 1-5 "main task" data-sets. The information
 * is the objects returned by pre-processing utilities, such as parse-trees and co-reference
 * information.
 * 
 * @author Asher Stern
 * 
 * @see PairsPreProcessor
 *
 */
public class PairData extends GenericPairData<Info, BasicNode> implements Serializable
{
	private static final long serialVersionUID = 8113653440646836737L;

	public PairData(TextHypothesisPair pair, List<BasicNode> textTrees,
			BasicNode hypothesisTree,
			Map<BasicNode, String> mapTreesToSentences,
			TreeCoreferenceInformation<BasicNode> coreferenceInformation,
			String datasetName)
	{
		super(pair, textTrees, hypothesisTree, mapTreesToSentences,coreferenceInformation, datasetName);
	}

	public PairData(TextHypothesisPair pair, List<BasicNode> textTrees,
			BasicNode hypothesisTree,
			Map<BasicNode, String> mapTreesToSentences,
			TreeCoreferenceInformation<BasicNode> coreferenceInformation)
	{
		super(pair, textTrees, hypothesisTree, mapTreesToSentences,coreferenceInformation);
	}

	
	
	

}
