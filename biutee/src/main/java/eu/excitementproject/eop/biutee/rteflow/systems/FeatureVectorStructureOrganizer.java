package eu.excitementproject.eop.biutee.rteflow.systems;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.RuleBasesAndPluginsContainer;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamples;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamplesUtils;
import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.transformations.datastructures.BooleanAndString;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Builds and stores the structure of the feature-vectors used in the system.
 * <P>
 * The system uses built-in features, specified in {@link Feature} enumeration. In
 * addition, the system uses features for each rule-base, and features for plug-ins,
 * and other dynamic features.
 * <BR>
 * The system must keep track on which feature-index corresponds to which feature.
 * For example, the system might decide that WordNet feature is feature-number 27.
 * Where is this decision stored? well - here, in this class.
 * <BR>
 * This class is crucial to enforce consistency between train and test.
 * The mapping of features to feature-indexes (e.g. WordNet is 27) must be identical
 * in train and test. To check this, use the method {@link #isCompatible(FeatureVectorStructureOrganizer)}.
 * <P>
 * An instance of this class is created by {@link SystemInitialization}
 * and is stored in {@link TESystemEnvironment}.
 * It is created in two phases: first by the {@link SystemInitialization#init()} method,
 * but then it is not yet ready to use. The method {@link SystemInitialization#completeInitializationWithScript(RuleBasesAndPluginsContainer)}
 * must be called to complete the initialization of this instance of {@link FeatureVectorStructureOrganizer}.
 * <BR>
 * (The reason is that the {@link OperationsScript} is not given in {@link SystemInitialization},
 * since each thread uses its own instance of {@link OperationsScript}, and there is
 * no "global" instance of {@link OperationsScript} in {@link SystemInitialization}).
 * 
 * @see SystemInitialization
 * @see TESystemEnvironment
 * @see SafeSamples
 * @see SafeSamplesUtils
 * @see FeatureUpdate
 * 
 * @author Asher Stern
 * @since Mar 23, 2012
 *
 */
@ThreadSafe
public class FeatureVectorStructureOrganizer implements FeatureVectorStructure,Serializable
{
	private static final long serialVersionUID = -8274369456468701113L;
	
	///////////////////////////// PUBLIC ///////////////////////////////////////
	
	@Override
	public ImmutableMap<String,Integer> getRuleBasesFeatures() throws TeEngineMlException
	{
		if (!built) throw new TeEngineMlException("Not yet built!");
		return this.ruleBasesFeatures;

	}

	@Override
	public ImmutableMap<String,Integer> getPluginFeatures() throws TeEngineMlException
	{
		if (!built) throw new TeEngineMlException("Not yet built!");
		return this.pluginFeatures;
	}

	@Override
	public ImmutableMap<String,Integer> getDynamicGlobalFeatures() throws TeEngineMlException
	{
		if (!built) throw new TeEngineMlException("Not yet built!");
		return this.dynamicGlobalFeatures;		
	}

	@Override
	public LinkedHashSet<String> getPredefinedFeaturesNames() throws TeEngineMlException
	{
		return predefinedFeaturesNames;
	}
	


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.systems.FeatureVectorStructure#isCompatibleWithString(ac.biu.nlp.nlp.engineml.rteflow.systems.FeatureVectorStructure, boolean)
	 */
	@Override
	public BooleanAndString isCompatibleWithString(FeatureVectorStructure other, boolean withString) throws TeEngineMlException
	{
		if (null==other) throw new TeEngineMlException("FeatureVectorStructureOrganizer.isCompatible() failed. Reason: Other is null");
		if (!isBuilt()) throw new TeEngineMlException("FeatureVectorStructureOrganizer.isCompatible() failed. Reason: Not yet built!");
		
		return isCompatibleWithString(this,other,withString);
	}


	
	
	/**
	 * @see FeatureVectorStructure#isCompatibleWithString(FeatureVectorStructure, boolean)
	 */
	public static BooleanAndString isCompatibleWithString(FeatureVectorStructure given, FeatureVectorStructure other, boolean withString) throws TeEngineMlException
	{
		BooleanAndString ret = null;
		

		String thisString = "this feature vector structure";
		String otherString = "other feature vector structure";
		BooleanAndString ruleBasesEquals = DsUtils.immutableMapEquals(given.getRuleBasesFeatures(), other.getRuleBasesFeatures(), withString, thisString, otherString);
		BooleanAndString pluginsEquals = DsUtils.immutableMapEquals(given.getPluginFeatures(), other.getPluginFeatures(), withString, thisString, otherString);
		BooleanAndString globalEquals = DsUtils.immutableMapEquals(given.getDynamicGlobalFeatures(), other.getDynamicGlobalFeatures(), withString, thisString, otherString);
		BooleanAndString predefinedEquals;
		boolean booleanPredefinedEquals = DsUtils.linkedHashSetEquals(given.getPredefinedFeaturesNames(),other.getPredefinedFeaturesNames());
		if (booleanPredefinedEquals)
			predefinedEquals = new BooleanAndString(true, null);
		else
			predefinedEquals = new BooleanAndString(true, "predefined features are not equal");
		
				

		
		boolean retBool = 
		(
				ruleBasesEquals.getBooleanValue()
				&&
				pluginsEquals.getBooleanValue()
				&&
				globalEquals.getBooleanValue()
				&&
				predefinedEquals.getBooleanValue()
				);
		
		if (true==retBool)
		{
			ret = new BooleanAndString(retBool,null);
		}
		else
		{
			if (!withString)
			{
				ret = new BooleanAndString(retBool, null);
			}
			else
			{
				StringBuffer sb = new StringBuffer();
				sb.append("Feature vector structures are not equal\n");
				BooleanAndString[] allComponents = new BooleanAndString[]
						{ruleBasesEquals,pluginsEquals,globalEquals,predefinedEquals};
				boolean firstIteration = true;
				for (BooleanAndString bas : allComponents)
				{
					if (false==bas.getBooleanValue())
					{
						if (firstIteration)firstIteration=false;
						else sb.append(", ");
						sb.append(bas.getString());
					}
				}
				
				ret = new BooleanAndString(retBool, sb.toString());
			}
		}
		return ret;
	}

	
	
	
	

	public BooleanAndString isCompatibleWithString(FeatureVectorStructureOrganizer other, boolean withString) throws TeEngineMlException
	{
		if (!other.isBuilt()) throw new TeEngineMlException("FeatureVectorStructureOrganizer.isCompatible() failed. Reason: Other not yet built!");
		return isCompatibleWithString((FeatureVectorStructure)other,withString);
	}

	
	public Set<Integer> getAllIndexesOfFeatures() throws TeEngineMlException
	{
		if (!built) throw new TeEngineMlException("FeatureVectorStructureOrganizer.getNumberOfFeatures() failed. Reason: Not yet built!");
		Set<Integer> ret = new LinkedHashSet<Integer>();
		for (Feature feature : Feature.values())
		{
			ret.add(feature.getFeatureIndex());
		}
		
		@SuppressWarnings("unchecked")
		ImmutableMap<String,Integer>[] maps = (ImmutableMap<String,Integer>[]) new ImmutableMap[]{
			getRuleBasesFeatures(),getPluginFeatures(),getDynamicGlobalFeatures()};
		for (ImmutableMap<String,Integer> map : maps)
		{
			for (String key : map.keySet())
			{
				ret.add(map.get(key));
			}
		}
		return ret;
	}

	
	
	
	
	
	
	
	
	
	

	public Map<Integer, String> createMapOfFeatureNames() throws TeEngineMlException
	{
		if (!built) throw new TeEngineMlException("Not yet built!");
		Map<Integer, String> ret = new LinkedHashMap<Integer, String>();
		for (Feature feature : Feature.values())
		{
			addKeyValueToMap(ret,feature.getFeatureIndex(),feature.name());
		}
		
		@SuppressWarnings("unchecked")
		ImmutableMap<String,Integer>[] maps = (ImmutableMap<String,Integer>[]) new ImmutableMap[]{ruleBasesFeatures,pluginFeatures,dynamicGlobalFeatures};
		for (ImmutableMap<String,Integer> map : maps)
		{
			for (String str : map.keySet())
			{
				addKeyValueToMap(ret,map.get(str),str);
			}
		}
		
		return ret;
	}
	
	public boolean isBuilt()
	{
		return built;
	}

	
	public synchronized void setPluginRegistry(PluginRegistry pluginRegistry) throws TeEngineMlException
	{
		if (this.pluginRegistry!=null)
		{
			if (pluginRegistry==this.pluginRegistry)
			{} // all OK
			else
			{
				throw new TeEngineMlException("A different plugin registry has already been set!");
			}
		}
		else
		{
			this.pluginRegistry = pluginRegistry;
		}
	}

	public synchronized void setRuleBasesContainer(RuleBasesAndPluginsContainer<?, ?> ruleBasesContainer) throws TeEngineMlException
	{
		LinkedHashSet<String> givenRuleBasesNames = ruleBasesContainer.getRuleBasesNames();
		if (null==givenRuleBasesNames) throw new TeEngineMlException("The rule bases names is null. Seems that rule-bases-container was not initialized.");
		setRuleBasesNames(givenRuleBasesNames);
	}

	
	public synchronized void setRuleBasesNames(LinkedHashSet<String> ruleBasesNames) throws TeEngineMlException
	{
		if (this.ruleBasesNames!=null)
		{
			if (DsUtils.linkedHashSetEquals(ruleBasesNames, this.ruleBasesNames))
			{} // all OK
			else
			{
				throw new TeEngineMlException("rule bases names have already been set, and differ from the given.");
			}
		}
		else
		{
			this.ruleBasesNames = ruleBasesNames;
		}
	}
	
	public void setDynamicGlobalFeatureNames(LinkedHashSet<String> dynamicGlobalFeatureNames)
	{
		this.dynamicGlobalFeatureNames = dynamicGlobalFeatureNames;
	}

	public synchronized void buildStructure() throws TeEngineMlException
	{
		TeEngineMlException notAllSetException = allSet();
		if (notAllSetException!=null) throw notAllSetException;
		try
		{
			if (!built)
			{
				int current = Feature.largestFeatureIndex()+1;
				Map<String, Integer> mapRuleBasesFeatures = new LinkedHashMap<String, Integer>();
				for (String ruleBaseName : ruleBasesNames)
				{
					mapRuleBasesFeatures.put(ruleBaseName, current);
					++current;
				}
				Map<String,Integer> mapPluginFeatures = new LinkedHashMap<String, Integer>();
				for (String pluginFeatureName : pluginRegistry.getSortedCustomFeatures())
				{
					mapPluginFeatures.put(pluginFeatureName, current);
					++current;
				}

				Map<String,Integer> mapDynamicGlobalFeatures = new LinkedHashMap<String, Integer>();
				if (dynamicGlobalFeatureNames!=null)
				{
					for (String dynamicGlobalFeatureName : dynamicGlobalFeatureNames )
					{
						mapDynamicGlobalFeatures.put(dynamicGlobalFeatureName, current);
						++current;
					}
				}

				
				this.ruleBasesFeatures = new ImmutableMapWrapper<String, Integer>(mapRuleBasesFeatures);
				this.pluginFeatures = new ImmutableMapWrapper<String, Integer>(mapPluginFeatures);
				this.dynamicGlobalFeatures = new ImmutableMapWrapper<String, Integer>(mapDynamicGlobalFeatures);
			}
			built = true;
			logStructure();
		}
		catch(PluginAdministrationException e)
		{
			throw new TeEngineMlException("Failed to initialize FeatureVectorStructureOrganizer",e);
		}
	}
	

	
	//////////////////////////////// PRIVATE //////////////////////////////////////
	
	private TeEngineMlException allSet()
	{
		if  ((pluginRegistry!=null)&&(ruleBasesNames!=null))
			return null;
		else
			return new TeEngineMlException(
					"Missing: "+
					( (null==pluginRegistry)?"pluginRegistry ":"")+
					( (null==ruleBasesNames)?"ruleBasesNames ":"")
					);
	}
	
	private void logStructure()
	{
		StringBuffer sb = new StringBuffer();
		if (built)
		{
			sb.append("FeatureVectorStructureOrganizer has been built").append("\n");
			for (Feature feature : Feature.values())
			{
				sb.append(feature.name()).append(": ").append(feature.getFeatureIndex()).append("\n");
			}
			sb.append("\n");
			printMap(ruleBasesFeatures,sb);
			sb.append("\n");
			printMap(pluginFeatures,sb);
			sb.append("\n");
			printMap(dynamicGlobalFeatures,sb);
		}
		else
		{
			sb.append("FeatureVectorStructureOrganizer has NOT been built yet!");
		}
		logger.info(sb.toString());
	}
	
	private void printMap(ImmutableMap<String, Integer> map, StringBuffer sb)
	{
		for (String key : map.keySet())
		{
			sb.append(key).append(": ").append(map.get(key)).append("\n");
		}
	}
	
	private static <K,V> void addKeyValueToMap(Map<K,V> map, K key, V value) throws TeEngineMlException
	{
		if (map.containsKey(key)) throw new TeEngineMlException("Key "+key.toString()+" already exists in the map.");
		map.put(key, value);
	}

	
	private transient LinkedHashSet<String> ruleBasesNames = null;
	private transient PluginRegistry pluginRegistry = null;
	private transient LinkedHashSet<String> dynamicGlobalFeatureNames;
	
	private boolean built = false;
	private LinkedHashSet<String> predefinedFeaturesNames = DsUtils.copySet(Feature.getAllFeaturesNames());
	private ImmutableMap<String,Integer> ruleBasesFeatures = null;
	private ImmutableMap<String,Integer> pluginFeatures = null;
	private ImmutableMap<String,Integer> dynamicGlobalFeatures = null;
	
	private static final Logger logger = Logger.getLogger(FeatureVectorStructureOrganizer.class);
}
