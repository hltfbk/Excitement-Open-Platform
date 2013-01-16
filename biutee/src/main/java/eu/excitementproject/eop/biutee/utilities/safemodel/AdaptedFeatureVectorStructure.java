package eu.excitementproject.eop.biutee.utilities.safemodel;
import java.util.LinkedHashSet;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructure;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.transformations.datastructures.BooleanAndString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public class AdaptedFeatureVectorStructure implements FeatureVectorStructure
{
	private static final long serialVersionUID = 1155177862041085935L;
	
	public AdaptedFeatureVectorStructure(){}
	
	public AdaptedFeatureVectorStructure(
			Map<String, Integer> ruleBasesFeatures_,
			Map<String, Integer> pluginFeatures_,
			Map<String, Integer> dynamicGlobalFeatures_,
			LinkedHashSet<String> predefinedFeaturesNames_)
	{
		super();
		this.ruleBasesFeatures_ = ruleBasesFeatures_;
		this.pluginFeatures_ = pluginFeatures_;
		this.dynamicGlobalFeatures_ = dynamicGlobalFeatures_;
		this.predefinedFeaturesNames_ = predefinedFeaturesNames_;
	}





	public Map<String, Integer> getRuleBasesFeatures_()
	{
		return ruleBasesFeatures_;
	}

	public void setRuleBasesFeatures_(Map<String, Integer> ruleBasesFeatures_)
	{
		this.ruleBasesFeatures_ = ruleBasesFeatures_;
	}

	public Map<String, Integer> getPluginFeatures_()
	{
		return pluginFeatures_;
	}

	public void setPluginFeatures_(Map<String, Integer> pluginFeatures_)
	{
		this.pluginFeatures_ = pluginFeatures_;
	}

	public Map<String, Integer> getDynamicGlobalFeatures_()
	{
		return dynamicGlobalFeatures_;
	}

	public void setDynamicGlobalFeatures_(
			Map<String, Integer> dynamicGlobalFeatures_)
	{
		this.dynamicGlobalFeatures_ = dynamicGlobalFeatures_;
	}

	public LinkedHashSet<String> getPredefinedFeaturesNames_()
	{
		return predefinedFeaturesNames_;
	}

	public void setPredefinedFeaturesNames_(
			LinkedHashSet<String> predefinedFeaturesNames_)
	{
		this.predefinedFeaturesNames_ = predefinedFeaturesNames_;
	}







	@Override
	public ImmutableMap<String, Integer> getRuleBasesFeatures() throws TeEngineMlException
	{
		synchronized(this){
			if (null==ruleBasesFeatures){ruleBasesFeatures = new ImmutableMapWrapper<String, Integer>(ruleBasesFeatures_);}}
		return ruleBasesFeatures;
	}

	@Override
	public ImmutableMap<String, Integer> getPluginFeatures() throws TeEngineMlException
	{
		synchronized(this){
			if (null==pluginFeatures){pluginFeatures = new ImmutableMapWrapper<String, Integer>(pluginFeatures_);}}
		return pluginFeatures;
	}

	@Override
	public ImmutableMap<String, Integer> getDynamicGlobalFeatures() throws TeEngineMlException
	{
		synchronized(this){
			if (null == dynamicGlobalFeatures){dynamicGlobalFeatures = new ImmutableMapWrapper<String, Integer>(dynamicGlobalFeatures_);}}
		return dynamicGlobalFeatures;
	}

	@Override
	public LinkedHashSet<String> getPredefinedFeaturesNames() throws TeEngineMlException
	{
		return predefinedFeaturesNames_;
	}

	@Override
	public BooleanAndString isCompatibleWithString(FeatureVectorStructure other, boolean withString) throws TeEngineMlException
	{
		return FeatureVectorStructureOrganizer.isCompatibleWithString(this, other, withString);
	}


	
	
	private Map<String, Integer> ruleBasesFeatures_;
	private Map<String, Integer> pluginFeatures_;
	private Map<String, Integer> dynamicGlobalFeatures_;
	private LinkedHashSet<String> predefinedFeaturesNames_;
	
	
	private ImmutableMap<String, Integer> ruleBasesFeatures = null;
	private ImmutableMap<String, Integer> pluginFeatures = null;
	private ImmutableMap<String, Integer> dynamicGlobalFeatures = null;

}
