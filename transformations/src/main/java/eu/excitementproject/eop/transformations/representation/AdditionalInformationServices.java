package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation.Monotonicity;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;
import eu.excitementproject.eop.transformations.representation.srl_informations.SemanticRoleLabelSet;

/**
 * Methods to set values to {@link AdditionalNodeInformation}
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class AdditionalInformationServices
{
	public static AdditionalNodeInformation emptyInformation()
	{
		return new AdditionalNodeInformation();
	}
	
	public static AdditionalNodeInformation generateFromCorefGroup(Integer corefGroupId, Integer uniqueIdForCoref)
	{
		if (corefGroupId==null)
			return new AdditionalNodeInformation();
		else
			return new AdditionalNodeInformation(corefGroupId,uniqueIdForCoref,(AdditionalNodeInformation)null);
		
	}

	public static AdditionalNodeInformation setContentAncestor(AdditionalNodeInformation original, ExtendedInfo contentAncestor)
	{
		return new AdditionalNodeInformation(contentAncestor,original);
	}
	
	public static AdditionalNodeInformation setPredicateSignature(AdditionalNodeInformation original, PredicateSignature predicateType)
	{
		return new AdditionalNodeInformation(predicateType,original);
	}
	
	public static AdditionalNodeInformation setNegationAndUncertainty(AdditionalNodeInformation original, NegationAndUncertainty negationAndUncertainty)
	{
		return new AdditionalNodeInformation(negationAndUncertainty,original);
	}
	
	public static AdditionalNodeInformation setPredTruth(AdditionalNodeInformation original, PredTruth predTruth)
	{
		return new AdditionalNodeInformation(predTruth,original);
	}
	
	public static AdditionalNodeInformation setClauseTruth(AdditionalNodeInformation original, ClauseTruth clauseTruth)
	{
		return new AdditionalNodeInformation(clauseTruth,original);
	}
	
	public static AdditionalNodeInformation setMonotonicity(AdditionalNodeInformation original, Monotonicity monotonicity)
	{
		return new AdditionalNodeInformation(monotonicity,original);
	}
	
	public static AdditionalNodeInformation setOriginalInfoTrace(AdditionalNodeInformation original, OriginalInfoTrace originalInfoTrace)
	{
		return new AdditionalNodeInformation(originalInfoTrace, original);
	}

	public static AdditionalNodeInformation generateFromAnnotations(PredicateSignature predType, NegationAndUncertainty negation,
			PredTruth predTruth, ClauseTruth clauseTruth, Monotonicity monotonicity) {
		return new AdditionalNodeInformation(null, null, null, predType, negation, predTruth, clauseTruth, monotonicity, null,null, null);
	}
	
	public static AdditionalNodeInformation setSrl(AdditionalNodeInformation original, SemanticRoleLabelSet srlSet)
	{
		return new AdditionalNodeInformation(srlSet,original);
	}
	
	/**
	 * Many annotated nodes that are not predicates (auxiliaries, determiners, prepositions...) 
	 * are left with a <i>null</i> set of annotations: null NU, null signature, null (or U) CT and null (or U) PT 
	 * @param info
	 * @return
	 */
	public static boolean hasNullAnnotation(AdditionalNodeInformation info)
	{
		NegationAndUncertainty nu 		= ExtendedInfoGetFields.getNegationAndUncertaintyObj(info);
		PredicateSignature signature = ExtendedInfoGetFields.getPredicateSignatureObj(info);
		ClauseTruth ct 		= ExtendedInfoGetFields.getClauseTruthObj(info);
		PredTruth pt 		= ExtendedInfoGetFields.getPredTruthObj(info);
		
		return nu == null | signature == null | (ct == null) | (pt == null);
		
	}
}
