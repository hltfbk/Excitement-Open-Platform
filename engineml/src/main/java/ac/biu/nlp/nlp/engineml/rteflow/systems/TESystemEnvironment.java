package ac.biu.nlp.nlp.engineml.rteflow.systems;

import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.generic.truthteller.SynchronizedAtomicAnnotator;
import ac.biu.nlp.nlp.engineml.plugin.PluginRegistry;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.UnigramProbabilityEstimation;


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
			ImmutableSet<String> stopWords)
	{
		super();
		this.ruleBasesToRetrieveMultiWords = ruleBasesToRetrieveMultiWords;
		this.mleEstimation = mleEstimation;
		this.treeAnnotator = treeAnnotator;
		this.pluginRegistry = pluginRegistry;
		this.featureVectorStructureOrganizer = featureVectorStructureOrganizer;
		this.alignmentCriteria = alignmentCriteria;
		this.stopWords = stopWords;
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







	private final Set<String> ruleBasesToRetrieveMultiWords;
	private final UnigramProbabilityEstimation mleEstimation;
	private final SynchronizedAtomicAnnotator treeAnnotator;
	private final PluginRegistry pluginRegistry;
	private final FeatureVectorStructureOrganizer featureVectorStructureOrganizer;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	private final ImmutableSet<String> stopWords;
}
