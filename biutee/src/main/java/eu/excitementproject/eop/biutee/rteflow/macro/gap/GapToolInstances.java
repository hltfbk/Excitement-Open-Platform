package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
public class GapToolInstances<I, S extends AbstractNode<I, S>>
{
	public GapToolInstances(GapFeaturesUpdate<I, S> gapFeaturesUpdate,
			GapHeuristicMeasure<I, S> gapHeuristicMeasure)
	{
		super();
		this.gapFeaturesUpdate = gapFeaturesUpdate;
		this.gapHeuristicMeasure = gapHeuristicMeasure;
	}
	
	
	
	public GapFeaturesUpdate<I, S> getGapFeaturesUpdate()
	{
		return gapFeaturesUpdate;
	}
	public GapHeuristicMeasure<I, S> getGapHeuristicMeasure()
	{
		return gapHeuristicMeasure;
	}



	private final GapFeaturesUpdate<I, S> gapFeaturesUpdate;
	private final GapHeuristicMeasure<I, S> gapHeuristicMeasure;
}
