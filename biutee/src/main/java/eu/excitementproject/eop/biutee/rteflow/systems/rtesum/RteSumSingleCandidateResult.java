package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.io.Serializable;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class RteSumSingleCandidateResult implements Serializable
{
	private static final long serialVersionUID = -3312931755434265158L;
	
	public RteSumSingleCandidateResult(SentenceIdentifier sentenceIdentifier,
			String hypothesisId, String sentenceString,
			String hypothesisString, ExtendedNode tree,
			Map<Integer, Double> featureVector, TreeHistory history)
	{
		this(sentenceIdentifier, hypothesisId, sentenceString,
				hypothesisString, tree, featureVector, history,
				null, null, null);
	}
	
	public RteSumSingleCandidateResult(SentenceIdentifier sentenceIdentifier,
			String hypothesisId, String sentenceString,
			String hypothesisString, ExtendedNode tree,
			Map<Integer, Double> featureVector, TreeHistory history,
			Long cpuTime, Long numberOfExpanded, Long numberOfGenerated)
	{
		super();
		this.sentenceIdentifier = sentenceIdentifier;
		this.hypothesisId = hypothesisId;
		this.sentenceString = sentenceString;
		this.hypothesisString = hypothesisString;
		this.tree = tree;
		this.featureVector = featureVector;
		this.history = history;
		
		this.cpuTime = cpuTime;
		this.numberOfExpanded = numberOfExpanded;
		this.numberOfGenerated = numberOfGenerated;
	}
	
	
	
	
	
	public SentenceIdentifier getSentenceIdentifier()
	{
		return sentenceIdentifier;
	}
	public String getHypothesisId()
	{
		return hypothesisId;
	}
	public String getSentenceString()
	{
		return sentenceString;
	}
	public String getHypothesisString()
	{
		return hypothesisString;
	}
	public ExtendedNode getTree()
	{
		return tree;
	}
	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}
	public TreeHistory getHistory()
	{
		return history;
	}
	
	public Long getCpuTime()
	{
		return cpuTime;
	}

	public Long getNumberOfExpanded()
	{
		return numberOfExpanded;
	}

	public Long getNumberOfGenerated()
	{
		return numberOfGenerated;
	}








	private final SentenceIdentifier sentenceIdentifier;
	private final String hypothesisId;
	private final String sentenceString;
	private final String hypothesisString;
	private final ExtendedNode tree;
	private final Map<Integer,Double> featureVector;
	private final TreeHistory history;
	
	private final Long cpuTime;
	private final Long numberOfExpanded;
	private final Long numberOfGenerated;

}
