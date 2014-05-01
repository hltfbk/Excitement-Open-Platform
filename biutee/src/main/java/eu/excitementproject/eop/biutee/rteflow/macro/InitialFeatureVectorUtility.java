package eu.excitementproject.eop.biutee.rteflow.macro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.FindMainVerbHeuristic;

/**
 * Used to create an initial feature-vector, i.e. a feature vector of a tree from the 
 * original text-trees. Since no operation was done on an original tree, its feature-vector
 * ought to be a vector of zero elements (0,0,0,0,...,0).
 * However, there are a few features that do not represent the proof, like "hypothesis length".
 * This class assigns the appropriate values to those features, while leaving all
 * features that represent proof steps as 0.
 *  
 * 
 * @author Asher Stern
 * @since May 25, 2011
 *
 */
public class InitialFeatureVectorUtility
{
	public InitialFeatureVectorUtility(
			FeatureVectorStructureOrganizer featureVectorStructure,
			ExtendedNode hypothesisTree, int hypothesisNumberOfNodes)
	{
		super();
		this.featureVectorStructure = featureVectorStructure;
		this.hypothesisTree = hypothesisTree;
		this.hypothesisNumberOfNodes = hypothesisNumberOfNodes;
	}
	
	/**
	 * Calling this method is optional.
	 * @param information
	 * @throws TeEngineMlException 
	 */
	public void setGlobalPairInformation(GlobalPairInformation information) throws TeEngineMlException
	{
		if (null==information) throw new TeEngineMlException("Null information");
		this.globalPairInformation = information;
	}
	
	public Map<Integer, Double> initialFeatureVector() throws PluginAdministrationException, TeEngineMlException
	{
		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
		// ret.put(0,0.0); - NO!!! There is no feature 0!
		for (Integer key : featureVectorStructure.getAllIndexesOfFeatures())
		{
			ret.put(key, 0.0);
		}
		if (BiuteeConstants.USE_HYPOTHESIS_LENGTH_FEATURE)
		{
			if (BiuteeConstants.INVERSE_HYPOTHESIS_LENGTH_IS_HYPOTHESIS_LENGTH)
			{
				ret.put(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex(),(double)hypothesisNumberOfNodes);
			}
			else
			{
				ret.put(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex(),1.0/(double)hypothesisNumberOfNodes);
			}
		}
		
		if (new FindMainVerbHeuristic().topContentVerbs(hypothesisTree).size()==0)
		{
			ret.put(Feature.HYPOTHESIS_MAIN_PREDICATE_IS_VERB.getFeatureIndex(), 0.0);
		}
		else
		{
			ret.put(Feature.HYPOTHESIS_MAIN_PREDICATE_IS_VERB.getFeatureIndex(), 1.0);
		}
		
		if (globalPairInformation!=null)
		{
			if (globalPairInformation.getTaskName()!=null)
			{
				String taskName = globalPairInformation.getTaskName();
				for (Feature globalFeature : Feature.getGlobalFeatures())
				{
					if (globalFeature.getTaskName()!=null)
					{
						if (taskName.equals(globalFeature.getTaskName()))
						{
							ret.put(globalFeature.getFeatureIndex(), 1.0);
						}
					}
				}
			}
			
			if (globalPairInformation.getDatasetName()!=null)
			{
				String datasetName = globalPairInformation.getDatasetName();
				ImmutableMap<String,Integer> dynamicGlobalFeatures = featureVectorStructure.getDynamicGlobalFeatures();
				boolean containsOK = false;
				if (dynamicGlobalFeatures.containsKey(datasetName)) { if (dynamicGlobalFeatures.get(datasetName)!=null)
				{
					containsOK = true;
					Integer featureIndexForDatasetName = dynamicGlobalFeatures.get(datasetName);
					ret.put(featureIndexForDatasetName, 1.0);
					if (logger.isDebugEnabled()){logger.debug("Setting feature "+featureIndexForDatasetName+" for dataset-name \""+datasetName+"\" value of 1.0");}
				}}
				if (!containsOK)
				{
					throw new TeEngineMlException("dataset name is "+datasetName+", but it does not exist in the feature-vector structure.");
				}
			}
		}
		
		return ret;
	}

	protected FeatureVectorStructureOrganizer featureVectorStructure;
	protected ExtendedNode hypothesisTree;
	protected int hypothesisNumberOfNodes = 0;
	protected GlobalPairInformation globalPairInformation=null;
	
	private static final Logger logger = Logger.getLogger(InitialFeatureVectorUtility.class);
}
