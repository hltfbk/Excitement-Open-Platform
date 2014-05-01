/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;


/**
 * Contains getters for {@link BasicRuleAnnotations}
 * @author Amnon Lotan
 *
 * @since Jul 5, 2012
 */
public class BasicAnnotationsGetFields {

	

	public static String getClauseTruth(BasicRuleAnnotations annotations)
	{
		return getClauseTruth(annotations,"");
	}


	public static String getClauseTruth(BasicRuleAnnotations annotations, String defaultValue)
	{
		if (annotations!=null) if ( annotations.getClauseTruth() != null)
			return annotations.getClauseTruth().getValue();

		return defaultValue;
	}

	public static String getPredTruth(BasicRuleAnnotations annotations)
	{
		return getPredTruth(annotations,"");
	}

	public static String getPredTruth(BasicRuleAnnotations annotations, String defaultValue)
	{
		if (annotations!=null && annotations.getPredTruth() != null)
			return annotations.getPredTruth().getValue();
		else
			return defaultValue;
	}
	
	public static String getNegationAndUncertainty(BasicRuleAnnotations annotations)
	{
		return getNegationAndUncertainty(annotations,"");
	}

	public static String getNegationAndUncertainty(BasicRuleAnnotations annotations, String defaultValue)
	{
		if (annotations!=null && annotations.getNegationAndUncertainty() != null)
			return annotations.getNegationAndUncertainty().getValue();
		else
			return defaultValue;
	}
	

	public static String getPredicateSignature(BasicRuleAnnotations annotations)
	{
		return getPredicateSignature(annotations,"");
	}

	public static String getPredicateSignature(BasicRuleAnnotations annotations, String defaultValue)
	{
		if (annotations!=null && annotations.getPredicateSignature() != null)
			return annotations.getPredicateSignature().display;
		else
			return defaultValue;
	}
	

	public static String getMonotonicity(BasicRuleAnnotations annotations)
	{
		return getMonotonicity(annotations,"");
	}

	public static String getMonotonicity(BasicRuleAnnotations annotations, String defaultValue)
	{
		if (annotations!=null && annotations.getMonotonicity() != null)
			return annotations.getMonotonicity().name();
		else
			return defaultValue;
	}
}
