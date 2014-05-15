package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.Serializable;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * Represents the result - the proof - of a text-hypothesis-pair.
 * <P>
 * TODO: may be this class and {@link PairResult} should be merged.
 * 
 * @author Asher Stern
 * @since Apr 3, 2011
 *
 */
public class PairProcessResult implements Serializable
{
	private static final long serialVersionUID = 4974819872201553736L;
	
	public PairProcessResult(ExtendedNode tree,
			Map<Integer, Double> featureVector, String originalSentence,
			ExtendedPairData pairData, TreeHistory history, LabeledSample labeledSample)
	{
		this(tree,featureVector,originalSentence,pairData,history,labeledSample,null,null);
	}

	public PairProcessResult(ExtendedNode tree,
			Map<Integer, Double> featureVector, String originalSentence,
			ExtendedPairData pairData, TreeHistory history, LabeledSample labeledSample,
			Long cpuTime, Long worldClockTime)
	{
		super();
		this.tree = tree;
		this.featureVector = featureVector;
		this.originalSentence = originalSentence;
		this.pairData = pairData;
		this.history = history;
		this.labeledSample = labeledSample;
		this.cpuTime = cpuTime;
		this.worldClockTime = worldClockTime;
	}

	
	
	public ExtendedNode getTree()
	{
		return tree;
	}
	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}
	public String getOriginalSentence()
	{
		return originalSentence;
	}
	public ExtendedPairData getPairData()
	{
		return pairData;
	}
	public TreeHistory getHistory()
	{
		return history;
	}
	public LabeledSample getLabeledSample()
	{
		return labeledSample;
	}
	public long getCpuTime()
	{
		return cpuTime;
	}
	public long getWorldClockTime()
	{
		return worldClockTime;
	}




	private ExtendedNode tree;
	private Map<Integer,Double> featureVector;
	private String originalSentence;
	private ExtendedPairData pairData;
	private TreeHistory history;
	private LabeledSample labeledSample;
	private final Long cpuTime;
	private final Long worldClockTime;
}
