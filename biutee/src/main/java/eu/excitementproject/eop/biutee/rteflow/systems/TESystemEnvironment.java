package eu.excitementproject.eop.biutee.rteflow.systems;

import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolBox;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.generic.truthteller.SynchronizedAtomicAnnotator;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;


/**
 * Several objects (data-structures, utilities, etc.) which are used globally in the
 * system.
 * 
 * @author Asher Stern
 * @since Oct 4, 2011
 *
 */
public class TESystemEnvironment
{
	public TESystemEnvironment(Set<String> ruleBasesToRetrieveMultiWords,
			UnigramProbabilityEstimation mleEstimation,
			SynchronizedAtomicAnnotator treeAnnotator,
			PluginRegistry pluginRegistry,
			FeatureVectorStructureOrganizer featureVectorStructureOrganizer,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria,
			ImmutableSet<String> stopWords,
			PARSER parser, boolean collapseMode,
			GapToolBox<ExtendedInfo, ExtendedNode> gapToolBox,
			ClassifierFactory classifierFactory)
	{
		super();
		this.ruleBasesToRetrieveMultiWords = ruleBasesToRetrieveMultiWords;
		this.mleEstimation = mleEstimation;
		this.treeAnnotator = treeAnnotator;
		this.pluginRegistry = pluginRegistry;
		this.featureVectorStructureOrganizer = featureVectorStructureOrganizer;
		this.alignmentCriteria = alignmentCriteria;
		this.stopWords = stopWords;
		this.parser = parser;
		this.collapseMode = collapseMode;
		this.gapToolBox = gapToolBox;
		this.classifierFactory = classifierFactory;
	}
	
	
	
	public Set<String> getRuleBasesToRetrieveMultiWords()
	{
		return ruleBasesToRetrieveMultiWords;
	}
	public UnigramProbabilityEstimation getMleEstimation()
	{
		return mleEstimation;
	}
	public SynchronizedAtomicAnnotator getTreeAnnotator()
	{
		return treeAnnotator;
	}
	public PluginRegistry getPluginRegistry()
	{
		return pluginRegistry;
	}
	public FeatureVectorStructureOrganizer getFeatureVectorStructureOrganizer()
	{
		return featureVectorStructureOrganizer;
	}
	public AlignmentCriteria<ExtendedInfo, ExtendedNode> getAlignmentCriteria()
	{
		return alignmentCriteria;
	}
	public ImmutableSet<String> getStopWords()
	{
		return stopWords;
	}
	public PARSER getParser()
	{
		return parser;
	}
	public boolean isCollapseMode()
	{
		return collapseMode;
	}
	public GapToolBox<ExtendedInfo, ExtendedNode> getGapToolBox()
	{
		return gapToolBox;
	}
	public ClassifierFactory getClassifierFactory()
	{
		return classifierFactory;
	}












	private final Set<String> ruleBasesToRetrieveMultiWords;
	private final UnigramProbabilityEstimation mleEstimation;
	private final SynchronizedAtomicAnnotator treeAnnotator;
	private final PluginRegistry pluginRegistry;
	private final FeatureVectorStructureOrganizer featureVectorStructureOrganizer;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	private final ImmutableSet<String> stopWords;
	private final PARSER parser;
	private final boolean collapseMode;
	private final GapToolBox<ExtendedInfo, ExtendedNode> gapToolBox;
	private final ClassifierFactory classifierFactory;
}
