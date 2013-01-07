package ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged.localcreative;

import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative.LocalCreativeTreeElement;


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
