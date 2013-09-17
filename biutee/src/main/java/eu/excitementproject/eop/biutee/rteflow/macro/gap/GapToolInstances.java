package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * A class that holds tools for measuring gaps between text parse trees to
 * hypothesis parse trees.
 * In the "macro" phase, an instance of {@link GapToolInstances} becomes a protected member
 * field of {@link InitializationTextTreesProcessor}, so it becomes available
 * to the search algorithms, and updates the feature vector of the "best" tree.
 * 
 * @see GapToolBox
 * @see InitializationTextTreesProcessor
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
@NotThreadSafe
public class GapToolInstances<I, S extends AbstractNode<I, S>>
{
	public GapToolInstances(GapFeaturesUpdate<I, S> gapFeaturesUpdate,
			GapHeuristicMeasure<I, S> gapHeuristicMeasure,
			GapDescriptionGenerator<I, S> gapDescription)
	{
		super();
		this.gapFeaturesUpdate = gapFeaturesUpdate;
		this.gapHeuristicMeasure = gapHeuristicMeasure;
		this.gapDescription = gapDescription;
	}
	
	
	
	public GapFeaturesUpdate<I, S> getGapFeaturesUpdate()
	{
		return gapFeaturesUpdate;
	}
	public GapHeuristicMeasure<I, S> getGapHeuristicMeasure()
	{
		return gapHeuristicMeasure;
	}
	public GapDescriptionGenerator<I, S> getGapDescriptionGenerator()
	{
		return gapDescription;
	}




	private final GapFeaturesUpdate<I, S> gapFeaturesUpdate;
	private final GapHeuristicMeasure<I, S> gapHeuristicMeasure;
	private final GapDescriptionGenerator<I, S> gapDescription;
}
