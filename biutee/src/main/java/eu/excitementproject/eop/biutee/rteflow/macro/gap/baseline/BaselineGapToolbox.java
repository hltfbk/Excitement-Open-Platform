package eu.excitementproject.eop.biutee.rteflow.macro.gap.baseline;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolBox;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolInstances;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolsFactory;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * 
 * @author Asher Stern
 * @since Sep 1, 2013
 *
 * @param <I>
 * @param <S>
 */
public class BaselineGapToolbox<I extends Info, S extends AbstractNode<I, S>> implements GapToolBox<I, S>
{
	public BaselineGapToolbox(UnigramProbabilityEstimation mleEstimation,
			ImmutableSet<String> stopWords)
	{
		super();
		this.mleEstimation = mleEstimation;
		this.stopWords = stopWords;
	}

	@Override
	public boolean isHybridMode() throws GapException
	{
		return true;
	}

	@Override
	public GapToolsFactory<I, S> getGapToolsFactory() throws GapException
	{
		return new GapToolsFactory<I, S>()
		{
			@Override
			public GapToolInstances<I, S> createInstances(
					TreeAndParentMap<I, S> hypothesis,
					LinearClassifier classifierForSearch) throws GapException
			{
				GapBaselineTools<I, S> tools = new  GapBaselineTools<>(hypothesis, classifierForSearch, mleEstimation, stopWords);
				return new GapToolInstances<>(tools, tools, tools);
			}
		};
		
	}

	private final UnigramProbabilityEstimation mleEstimation;
	private final ImmutableSet<String> stopWords;
}
