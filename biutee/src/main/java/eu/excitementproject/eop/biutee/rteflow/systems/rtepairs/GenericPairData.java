package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;

/**
 * Contains information about Text-Hypothesis pair of the official RTE dataset (1-5).
 * That information is the information about the pair as it is in the dataset
 * ({@link TextHypothesisPair}), and representation of the pair after preprocessing
 * (text divided into sentences, parsing, coreference resolution) 
 * 
 * @author Asher Stern
 * @since Apr 7, 2011
 *
 * @param <I>
 * @param <S>
 */
public class GenericPairData<I, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 3490517244039265114L;
	
	public GenericPairData(TextHypothesisPair pair, List<S> textTrees, S hypothesisTree, Map<S, String> mapTreesToSentences, TreeCoreferenceInformation<S> coreferenceInformation, String datasetName)
	{
		this.pair = pair;
		this.textTrees = textTrees;
		this.hypothesisTree = hypothesisTree;
		this.mapTreesToSentences = mapTreesToSentences;
		this.coreferenceInformation = coreferenceInformation;
		this.datasetName = datasetName;
	}

	public GenericPairData(TextHypothesisPair pair, List<S> textTrees, S hypothesisTree, Map<S, String> mapTreesToSentences, TreeCoreferenceInformation<S> coreferenceInformation)
	{
		this(pair,textTrees,hypothesisTree,mapTreesToSentences,coreferenceInformation,null);
	}
	
	
	/**
	 * Returns information that represents merely the text and meta data about the pair, as
	 * created by {@link RTEMainReader}.
	 * @return
	 */
	public TextHypothesisPair getPair()
	{
		return pair;
	}
	
	/**
	 * Returns a list of the text's parse trees. One tree for each text sentence.  
	 * @return
	 */
	public List<S> getTextTrees()
	{
		return textTrees;
	}
	
	/**
	 * Returns the hypothesis parse tree.
	 * @return
	 */
	public S getHypothesisTree()
	{
		return hypothesisTree;
	}

	/**
	 * Returns a map from each text's parse tree, to its original sentence (as a string)
	 * @return
	 */
	public Map<S, String> getMapTreesToSentences()
	{
		return mapTreesToSentences;
	}
	
	/**
	 * Returns co-reference information about the pair's text.
	 * @return
	 */
	public TreeCoreferenceInformation<S> getCoreferenceInformation()
	{
		return coreferenceInformation;
	}

	/**
	 * Returns the data-set name (like RTE1, RTE2, etc.) of this text-hypothesis-pair.
	 * @return the data-set name
	 */
	public String getDatasetName()
	{
		return datasetName;
	}







	private String datasetName;
	private TextHypothesisPair pair;
	private List<S> textTrees;
	private S hypothesisTree;
	private Map<S, String> mapTreesToSentences;
	private TreeCoreferenceInformation<S> coreferenceInformation;
}
