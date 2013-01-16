package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.Serializable;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;



/**
 * Represents the result - the proof - of a text-hypothesis-pair.
 * <P>
 * TODO: may be this class and {@link PairProcessResult} should be merged.
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class PairResult implements Serializable
{
	private static final long serialVersionUID = -3041534450791728152L;
	
	public PairResult(TreeAndFeatureVector bestTree, String bestTreeSentence,
			TreeHistory bestTreeHistory,
			Long cpuTime, Long worldClockTime,
			Long numberOfExpandedElements, Long numberOfGeneratedElements)
	{
		super();
		this.bestTree = bestTree;
		this.bestTreeSentence = bestTreeSentence;
		this.bestTreeHistory = bestTreeHistory;
		this.cpuTime = cpuTime;
		this.worldClockTime = worldClockTime;
		this.numberOfExpandedElements = numberOfExpandedElements;
		this.numberOfGeneratedElements = numberOfGeneratedElements;
	}

	public PairResult(TreeAndFeatureVector bestTree, String bestTreeSentence,
			TreeHistory bestTreeHistory)
	{
		this(bestTree,bestTreeSentence,bestTreeHistory,null,null,null,null);
	}

	
	public TreeAndFeatureVector getBestTree()
	{
		return bestTree;
	}
	public String getBestTreeSentence()
	{
		return bestTreeSentence;
	}
	public TreeHistory getBestTreeHistory()
	{
		return bestTreeHistory;
	}
	public Long getCpuTime()
	{
		return cpuTime;
	}
	public Long getWorldClockTime()
	{
		return worldClockTime;
	}
	public Long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}

	public Long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}




	private final TreeAndFeatureVector bestTree;
	private final String bestTreeSentence;
	private final TreeHistory bestTreeHistory;
	
	private final Long cpuTime;
	private final Long worldClockTime;
	private final Long numberOfExpandedElements;
	private final Long numberOfGeneratedElements;
	
}
