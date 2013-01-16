package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.localcreative;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTreeElement;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * 
 * @author Asher Stern
 * @since Oct 31, 2011
 *
 */
public class KStagedLocalCreativeElement extends LocalCreativeTreeElement
{
	private static final long serialVersionUID = -6020682996669227576L;

	public KStagedLocalCreativeElement(ExtendedNode tree, TreeHistory history,
			Map<Integer, Double> featureVector, int localIteration,
			int globalIteration, Set<ExtendedNode> affectedNodes, double cost,
			double gap, KStagedLocalCreativeElement base, String originalSentence)
	{
		super(tree, history, featureVector, localIteration, globalIteration,
				affectedNodes, cost, gap);
		this.base = base;
		this.originalSentence = originalSentence;
	}
	
	

	public KStagedLocalCreativeElement getBase()
	{
		return base;
	}
	
	public String getOriginalSentence()
	{
		return originalSentence;
	}

	private final KStagedLocalCreativeElement base;
	private final String originalSentence;
}
