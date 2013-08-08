package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_ENGINE_GAP_HYBRID_MODE;
/**
 * A factory of {@link GapToolBox}.
 * 
 * @author Asher Stern
 * @since Aug 5, 2013
 *
 */
public class GapToolBoxFactory
{
	public GapToolBoxFactory(ConfigurationFile configurationFile,
			ConfigurationParams configurationParams,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		super();
		this.configurationFile = configurationFile;
		this.configurationParams = configurationParams;
		this.alignmentCriteria = alignmentCriteria;
	}
	
	public GapToolBox<ExtendedInfo, ExtendedNode> createGapToolBox() throws GapException
	{
		try
		{
			boolean hybridMode = false;
			if (configurationParams.containsKey(RTE_ENGINE_GAP_HYBRID_MODE))
			{
				hybridMode = configurationParams.getBoolean(RTE_ENGINE_GAP_HYBRID_MODE);
			}
			
			if (hybridMode)
			{
				logger.info("In hybrid gap mode.");
				return createGapToolBoxForHybridMode();
			}
			else
			{
				logger.info("In pure transformation mode.");
				return new GapToolBox<ExtendedInfo, ExtendedNode>()
				{
					@Override
					public boolean isHybridMode() throws GapException
					{
						return false;
					}
					
					@Override
					public GapToolsFactory<ExtendedInfo, ExtendedNode> getGapToolsFactory() throws GapException
					{
						return null;
					}
				};
			}
		}
		catch(ConfigurationException e)
		{
			throw new GapException("Failed to read configuration file parameter \""+RTE_ENGINE_GAP_HYBRID_MODE+"\"",e);
		}

	}
	
	
	private GapToolBox<ExtendedInfo, ExtendedNode> createGapToolBoxForHybridMode() throws GapException
	{
		// create a GapToolBox. This tool box is global to the system, and is able to
		// construct tools for specific hypotheses.
		return new GapToolBox<ExtendedInfo, ExtendedNode>()
		{
			@Override
			public boolean isHybridMode() throws GapException
			{
				return true;
			}
			
			@Override
			public GapToolsFactory<ExtendedInfo, ExtendedNode> getGapToolsFactory() throws GapException
			{
				// The GapToolsFactory constructs tools for specific hypotheses.
				return new GapToolsFactory<ExtendedInfo, ExtendedNode>()
				{
					@Override
					public GapToolInstances<ExtendedInfo, ExtendedNode> createInstances(
							final TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
							final LinearClassifier classifierForSearch) throws GapException
					{
						// Create a GapFeatureVectorGenerator - the one that really does
						// the job. It returns the feature vector which resembels the
						// gap between the parse trees.
						final GapFeatureVectorGenerator gapFeatureVectorGenerator = new GapFeatureVectorGenerator(alignmentCriteria);
						
						final GapFeaturesUpdate<ExtendedInfo, ExtendedNode> gapFeaturesUpdate = new GapFeaturesUpdate<ExtendedInfo, ExtendedNode>()
						{
							@Override
							public Map<Integer, Double> updateForGap(
									TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
									Map<Integer, Double> featureVector) throws GapException
							{
								Map<Integer, Double> ret = new LinkedHashMap<>();
								Map<Integer, Double> gapVector = gapFeatureVectorGenerator.createFeatureVector(tree, hypothesis);
								Set<Integer> features = new LinkedHashSet<>();
								features.addAll(featureVector.keySet());
								features.addAll(gapVector.keySet());
								for (Integer feature : features)
								{
									double value = 0.0;
									if (featureVector.get(feature)!=null) value+=featureVector.get(feature);
									if (gapVector.get(feature)!=null) value+=gapVector.get(feature);
									ret.put(feature,value);
								}
								return ret;
							}
						};
						
						GapHeuristicMeasure<ExtendedInfo, ExtendedNode> gapHeuristicMeasure = new GapHeuristicMeasure<ExtendedInfo, ExtendedNode>()
						{
							@Override
							public double measure(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree, Map<Integer, Double> featureVector) throws GapException
							{
								Map<Integer, Double> featureVectorWithGap = gapFeaturesUpdate.updateForGap(tree,featureVector);
								try{return (-classifierForSearch.getProduct(featureVectorWithGap));}
								catch(ClassifierException e){throw new GapException("Failed to calculate gap measure, due to a problem in the classifier.",e);}
							}
						};
						
						return new GapToolInstances<>(gapFeaturesUpdate, gapHeuristicMeasure);
						
					}
				};
			}
		};
	}
	
//	private PredicateArgumentStructureBuilderFactory<ExtendedInfo, ExtendedNode> createPredArgsFactory() throws GapException
//	{
//		try
//		{
//			PredicateArgumentStructureBuilderFactoryFactory<ExtendedInfo, ExtendedNode> factoryFactory =
//					new PredicateArgumentStructureBuilderFactoryFactory<ExtendedInfo, ExtendedNode>(configurationFile);
//			return factoryFactory.createBuilderFactory();
//		}
//		catch (ConfigurationException | PredicateArgumentIdentificationException e)
//		{
//			throw new GapException("Failed to build predicate argument structure builder factory.");
//		}
//	}
	
	
	
	@SuppressWarnings("unused")
	private final ConfigurationFile configurationFile;
	private final ConfigurationParams configurationParams;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	
	
	private static final Logger logger = Logger.getLogger(GapToolBoxFactory.class);
}
