package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * 
 * @author Asher Stern
 * @since Aug 5, 2013
 *
 */
public class GapToolBoxFactory
{
	public GapToolBoxFactory(ConfigurationFile configurationFile,
			ConfigurationParams configurationParams)
	{
		super();
		this.configurationFile = configurationFile;
		this.configurationParams = configurationParams;
	}
	
	public GapToolBox<ExtendedInfo, ExtendedNode> createGapToolBox() throws GapException
	{
		logger.info("Create a dummy gap tool box.");
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
				return new GapToolsFactory<ExtendedInfo, ExtendedNode>()
				{
					@Override
					public GapToolInstances<ExtendedInfo, ExtendedNode> createInstances(
							TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
							LinearClassifier classifierForSearch) throws GapException
					{
						final TreeAndParentMap<ExtendedInfo, ExtendedNode> finalHypothesis = hypothesis;
						final LinearClassifier finalClassifierForSearch = classifierForSearch;
						
						final GapFeatureVectorGenerator gapFeatureVectorGenerator = new GapFeatureVectorGenerator();
						
						final GapFeaturesUpdate<ExtendedInfo, ExtendedNode> gapFeaturesUpdate = new GapFeaturesUpdate<ExtendedInfo, ExtendedNode>()
						{
							@Override
							public Map<Integer, Double> updateForGap(
									TreeAndParentMap<ExtendedInfo, ExtendedNode> tree,
									Map<Integer, Double> featureVector) throws GapException
							{
								Map<Integer, Double> ret = new LinkedHashMap<>();
								Map<Integer, Double> gapVector = gapFeatureVectorGenerator.createFeatureVector(tree, finalHypothesis);
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
								try{return (-finalClassifierForSearch.getProduct(featureVectorWithGap));}
								catch(ClassifierException e){throw new GapException("Failed to calculate gap measure, due to a problem in the classifier.",e);}
							}
						};
						
						return new GapToolInstances<>(gapFeaturesUpdate, gapHeuristicMeasure);
						
					}
				};
			}
		};
	}
	
	
	
	@SuppressWarnings("unused")
	private final ConfigurationFile configurationFile;
	@SuppressWarnings("unused")
	private final ConfigurationParams configurationParams;
	
	
	private static final Logger logger = Logger.getLogger(GapToolBoxFactory.class);
}
