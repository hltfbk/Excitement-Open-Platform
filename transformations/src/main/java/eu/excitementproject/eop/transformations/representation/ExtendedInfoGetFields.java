package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;
import eu.excitementproject.eop.transformations.representation.srl_informations.SemanticRoleLabelSet;
import eu.excitementproject.eop.transformations.representation.srl_informations.SrlPredicateId;

/**
 * Easy get the fields of {@link ExtendedInfo}, {@link AnnotatedNodeInfo} and {@link EdgeInfo}, with no null
 * in the return value.
 * 
 * @author Asher Stern
 * @since Apr 11, 2010
 *
 */
public class ExtendedInfoGetFields extends InfoGetFields
{
	public static String getCorefGroupId(ExtendedInfo info)
	{
		Integer retInteger = null;
		if (info!=null)
		{
			if (info.getAdditionalNodeInformation()!=null)
			{
				retInteger = info.getAdditionalNodeInformation().getCorefGroupId();
			}
		}
		if (null==retInteger)
			return "";
		else
			return retInteger.toString();
	}
	
	
	
	/**
	 * This method may return <code>null</code>
	 * @param info
	 * @return
	 */
	public static ExtendedInfo getContentAncestor(ExtendedInfo info)
	{
		ExtendedInfo ret = null;
		if (info!=null)
		{
			if (info.getAdditionalNodeInformation()!=null)
			{
				ret = info.getAdditionalNodeInformation().getContentAncestor();
			}
		}
		return ret;
	}
	
	public static OriginalInfoTrace getOriginalInfoTrace(ExtendedInfo info)
	{
		OriginalInfoTrace ret = null;
		if (info!=null)
		{
			if (info.getAdditionalNodeInformation()!=null)
			{
				ret = info.getAdditionalNodeInformation().getOriginalInfoTrace();
			}
		}
		return ret;
	}


	public static ClauseTruth getClauseTruthObj(AdditionalNodeInformation nodeInfo)
	{
		ClauseTruth ret = null;
		if (nodeInfo!=null)if (nodeInfo.getClauseTruth()!=null)
			ret = nodeInfo.getClauseTruth();
		
		return ret;
	}
	
	public static String getClauseTruth(AdditionalNodeInformation nodeInfo)
	{
		return getClauseTruth(nodeInfo, "");
	}
	
	public static String getClauseTruth(AdditionalNodeInformation nodeInfo, String defaultValue)
	{
		ClauseTruth obj = getClauseTruthObj(nodeInfo);
		if (obj != null)
			return obj.name();
		else
			return defaultValue;
	}

	public static ClauseTruth getClauseTruthObj(ExtendedInfo info)
	{
		return getClauseTruthObj(info, null);
	}


	public static ClauseTruth getClauseTruthObj(ExtendedInfo info, ClauseTruth defaultValue) 
	{
		ClauseTruth obj = getClauseTruthObj(info.getAdditionalNodeInformation());
		if (obj != null)
			return obj;
		else
			return defaultValue;
	}


	public static String getClauseTruth(ExtendedInfo info)
	{
		return getClauseTruth(info,"");
	}


	public static String getClauseTruth(ExtendedInfo info, String defaultValue)
	{
		if (info!=null)
			return getClauseTruth(info.getAdditionalNodeInformation(),defaultValue);
		else
			return defaultValue;
	}

	public static String getMonotonicity(AdditionalNodeInformation nodeInfo)
	{
		return getMonotonicity(nodeInfo, "");
	}

	public static String getMonotonicity(AdditionalNodeInformation nodeInfo, String defaultValue)
	{
		String ret = defaultValue;
		if (nodeInfo!=null)if (nodeInfo.getMonotonicity()!=null)
			ret = nodeInfo.getMonotonicity().name();
		
		return ret;
	}


	public static String getMonotonicity(ExtendedInfo info)
	{
		return getMonotonicity(info,"");
	}


	public static String getMonotonicity(ExtendedInfo info, String defaultValue)
	{
		if (info!=null)
			return getMonotonicity(info.getAdditionalNodeInformation(),defaultValue);
		else
			return defaultValue;
	}

	public static String getNegationAndUncertainty(AdditionalNodeInformation nodeInfo)
	{
		return getNegationAndUncertainty(nodeInfo, "");
	}
	
	public static String getNegationAndUncertainty(AdditionalNodeInformation nodeInfo,	String defaultValue) 
	{
		String ret = defaultValue;
		if (nodeInfo!=null)if (nodeInfo.getNegationAndUncertainty()!=null)
			ret = nodeInfo.getNegationAndUncertainty().name();
		
		return ret;
	}


	public static String getNegationAndUncertainty(ExtendedInfo info) 
	{
		return getNegationAndUncertainty(info,"");
	}


	public static String getNegationAndUncertainty(ExtendedInfo info, String defaultValue)
	{
		if (info!=null)
			return getNegationAndUncertainty(info.getAdditionalNodeInformation(),defaultValue);
		else
			return defaultValue;
	}

	public static String getPredTruth(AdditionalNodeInformation nodeInfo)
	{
		return getPredicateSignature(nodeInfo, "");
	}
	
	public static String getPredTruth(AdditionalNodeInformation nodeInfo,	String defaultValue) 
	{
		String ret = defaultValue;
		if (nodeInfo!=null)if (nodeInfo.getPredTruth()!=null)
			ret = nodeInfo.getPredTruth().name();
		
		return ret;
	}


	public static String getPredTruth(ExtendedInfo info) 
	{
		return getPredTruth(info,"");
	}

	public static String getPredTruth(ExtendedInfo info, String defaultValue)
	{
		if (info!=null)
			return getPredTruth(info.getAdditionalNodeInformation(),defaultValue);
		else
			return defaultValue;
	}

	public static String getPredicateSignature(AdditionalNodeInformation nodeInfo)
	{
		return getPredicateSignature(nodeInfo, "");
	}

	public static String getPredicateSignature(AdditionalNodeInformation nodeInfo, String defaultValue)
	{
		String ret = defaultValue;
		if (nodeInfo!=null)if (nodeInfo.getPredicateSignature()!=null)
			ret = nodeInfo.getPredicateSignature().toString();
		
		return ret;
	}


	public static String getPredicateSignature(ExtendedInfo info)
	{
		return getPredicateSignature(info,"");
	}


	public static String getPredicateSignature(ExtendedInfo info, String defaultValue)
	{
		if (info!=null)
			return getPredicateSignature(info.getAdditionalNodeInformation(),defaultValue);
		else
			return defaultValue;
	}

	public static NegationAndUncertainty getNegationAndUncertaintyObj(ExtendedInfo info) {
		return getNegationAndUncertaintyObj(info.additionalNodeInformation);
	}
	
	public static NegationAndUncertainty getNegationAndUncertaintyObj(AdditionalNodeInformation nodeInfo)
	{
		NegationAndUncertainty ret = null;
		if (nodeInfo!=null)if (nodeInfo.getNegationAndUncertainty()!=null)
			ret = nodeInfo.getNegationAndUncertainty();
		
		return ret;
	}


	public static PredicateSignature getPredicateSignatureObj(ExtendedInfo info) {
		return getPredicateSignatureObj(info, null);
	}

	public static PredicateSignature getPredicateSignatureObj(ExtendedInfo info, PredicateSignature defaultValue) 
	{
		PredicateSignature obj = getPredicateSignatureObj(info.getAdditionalNodeInformation());
		if (obj != null)
			return obj;
		else
			return defaultValue;
	}
	
	public static PredicateSignature getPredicateSignatureObj(AdditionalNodeInformation nodeInfo)
	{
		PredicateSignature ret = null;
		if (nodeInfo!=null)if (nodeInfo.getPredicateSignature()!=null)
			ret = nodeInfo.getPredicateSignature();
		
		return ret;
	}

	public static PredTruth getPredTruthObj(ExtendedInfo info) {
		return getPredTruthObj(info, null);
	}
	
	public static PredTruth getPredTruthObj(ExtendedInfo info, PredTruth defaultValue) 
	{
		PredTruth obj = getPredTruthObj(info.getAdditionalNodeInformation());
		if (obj != null)
			return obj;
		else
			return defaultValue;
	}
	
	public static PredTruth getPredTruthObj(AdditionalNodeInformation nodeInfo)
	{
		PredTruth ret = null;
		if (nodeInfo!=null)if (nodeInfo.getPredTruth()!=null)
			ret = nodeInfo.getPredTruth();
		
		return ret;
	}

	public static AdditionalNodeInformation getAdditionalNodeInformation(ExtendedInfo info)
	{
		AdditionalNodeInformation ret = null;
		if (info!=null){if (info.getAdditionalNodeInformation()!=null)
		{
			ret = info.getAdditionalNodeInformation();
		}}
		if (null==ret)
		{
			ret = AdditionalInformationServices.emptyInformation();
		}
		return ret;
	}
	
	public static SrlPredicateId getSrlPredicateId(ExtendedInfo info)
	{
		SrlPredicateId ret = null;
		if (info!=null){if(info.getAdditionalNodeInformation()!=null)
		{
			ret = info.getAdditionalNodeInformation().getSrlPredicateId();
		}}
		return ret;
	}
	
	public static SemanticRoleLabelSet getSemanticRoleLabelSet(ExtendedInfo info)
	{
		SemanticRoleLabelSet ret = null;
		if (info!=null){if (info.getAdditionalNodeInformation()!=null)
		{
			ret = info.getAdditionalNodeInformation().getSrlSet();
		}}
		return ret;
	}
	
	
	
}
