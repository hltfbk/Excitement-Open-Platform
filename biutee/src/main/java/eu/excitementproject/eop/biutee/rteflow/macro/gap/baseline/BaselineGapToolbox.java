package eu.excitementproject.eop.biutee.rteflow.macro.gap.baseline;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolBox;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolInstances;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolsFactory;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * 
 * @author Asher Stern
 * @since Sep 1, 2013
 *
 */
public class BaselineGapToolbox implements GapToolBox<ExtendedInfo, ExtendedNode>
{
	public BaselineGapToolbox(UnigramProbabilityEstimation mleEstimation,
			ImmutableSet<String> stopWords,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		super();
		this.mleEstimation = mleEstimation;
		this.stopWords = stopWords;
		this.alignmentCriteria = alignmentCriteria;
	}

	@Override
	public boolean isHybridMode() throws GapException
	{
		return true;
	}

	@Override
	public GapToolsFactory<ExtendedInfo, ExtendedNode> getGapToolsFactory() throws GapException
	{
		return new GapToolsFactory<ExtendedInfo, ExtendedNode>()
		{
			@Override
			public GapToolInstances<ExtendedInfo, ExtendedNode> createInstances(
					TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
					LinearClassifier classifierForSearch) throws GapException
			{
				//GapBaselineV1Tools tools = new  GapBaselineV1Tools(hypothesis, classifierForSearch, mleEstimation, stopWords, alignmentCriteria);
				GapBaselineV2Tools tools = new  GapBaselineV2Tools(hypothesis, classifierForSearch, mleEstimation, alignmentCriteria);
				return new GapToolInstances<>(tools, tools, tools);
			}
		};
		
	}

	private final UnigramProbabilityEstimation mleEstimation;
	@SuppressWarnings("unused")
	private final ImmutableSet<String> stopWords;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
}
