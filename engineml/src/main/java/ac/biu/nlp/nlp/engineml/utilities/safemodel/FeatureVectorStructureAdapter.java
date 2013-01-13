package ac.biu.nlp.nlp.engineml.utilities.safemodel;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import ac.biu.nlp.nlp.engineml.rteflow.systems.FeatureVectorStructure;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public class FeatureVectorStructureAdapter extends XmlAdapter<AdaptedFeatureVectorStructure, FeatureVectorStructure>
{

	@Override
	public FeatureVectorStructure unmarshal(AdaptedFeatureVectorStructure v) throws Exception
	{
		return v;
	}

	@Override
	public AdaptedFeatureVectorStructure marshal(FeatureVectorStructure v) throws Exception
	{
		return new AdaptedFeatureVectorStructure(v.getRuleBasesFeatures().getMutableCopy(),v.getPluginFeatures().getMutableCopy(),v.getDynamicGlobalFeatures().getMutableCopy(),v.getPredefinedFeaturesNames());
	}

}
