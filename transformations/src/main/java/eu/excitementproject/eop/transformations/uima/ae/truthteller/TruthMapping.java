package eu.excitementproject.eop.transformations.uima.ae.truthteller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.predicatetruth.ClauseTruth;
import eu.excitement.type.predicatetruth.ClauseTruthNegative;
import eu.excitement.type.predicatetruth.ClauseTruthNotIdentified;
import eu.excitement.type.predicatetruth.ClauseTruthPositive;
import eu.excitement.type.predicatetruth.ClauseTruthUncertain;
import eu.excitement.type.predicatetruth.NegationAndUncertainty;
import eu.excitement.type.predicatetruth.NegationAndUncertaintyNegative;
import eu.excitement.type.predicatetruth.NegationAndUncertaintyPositive;
import eu.excitement.type.predicatetruth.NegationAndUncertaintyUncertain;
import eu.excitement.type.predicatetruth.PredicateSignature;
import eu.excitement.type.predicatetruth.PredicateSignatureNegativeNegative;
import eu.excitement.type.predicatetruth.PredicateSignatureNegativePositive;
import eu.excitement.type.predicatetruth.PredicateSignatureNegativeUncertain;
import eu.excitement.type.predicatetruth.PredicateSignaturePositiveNegative;
import eu.excitement.type.predicatetruth.PredicateSignaturePositivePositive;
import eu.excitement.type.predicatetruth.PredicateSignaturePositiveUncertain;
import eu.excitement.type.predicatetruth.PredicateSignatureUncertainNegative;
import eu.excitement.type.predicatetruth.PredicateSignatureUncertainPositive;
import eu.excitement.type.predicatetruth.PredicateSignatureUncertainUncertain;
import eu.excitement.type.predicatetruth.PredicateTruth;
import eu.excitement.type.predicatetruth.PredicateTruthNegative;
import eu.excitement.type.predicatetruth.PredicateTruthNotIdentified;
import eu.excitement.type.predicatetruth.PredicateTruthPositive;
import eu.excitement.type.predicatetruth.PredicateTruthUncertain;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;

/**
 * Conversion class from Truthteller's annotations to UIMA annotations
 * Each static function converts a different annotation type.
 * @author Gabi Stanovsky
 * @since Aug 2014
 */

public class TruthMapping {
	
	public static PredicateTruth mapPredicateTruth(PredTruth pt, JCas jcas, int begin, int end){
		Type type = jcas.getTypeSystem().getType(PRED_TRUTH_MAP.get(pt).getName());
		PredicateTruth ret = (PredicateTruth)jcas.getCas().createAnnotation(type, begin, end);
		return ret;
	}
	
	public static ClauseTruth mapClauseTruth(eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth ct, JCas jcas, List<Token> subtree,int begin,int end){
		int subtreeSize = subtree.size();
		Type type = jcas.getTypeSystem().getType(CLAUSE_TRUTH_MAP.get(ct).getName());
		ClauseTruth ret = (ClauseTruth)jcas.getCas().createAnnotation(type, begin, end);
	
		// set the subtree tokens as a feature structure		
		FSArray subtreeFSArray = new FSArray(jcas, subtreeSize);
		subtreeFSArray.copyFromArray(subtree.toArray(new Token[subtree.size()]), 0, 0, subtreeSize);
		ret.setClauseTokens(subtreeFSArray);
		return ret;
	}
	
	public static NegationAndUncertainty mapNegationAndUncertainty(eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty nu, JCas jcas,int begin,int end){
		Type type = jcas.getTypeSystem().getType(NU_MAP.get(nu).getName());
		NegationAndUncertainty ret = (NegationAndUncertainty)jcas.getCas().createAnnotation(type, begin, end);
		return ret;
	}
	
	public static PredicateSignature mapPredicateSignature(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature sig,JCas jcas,int begin,int end){
		Type type = jcas.getTypeSystem().getType(SIG_MAP.get(sig).getName());
		PredicateSignature ret = (PredicateSignature)jcas.getCas().createAnnotation(type, begin, end);
		return ret;
	}
	
	//static mapping from TruthTeller types to UIMA types
	public static Map<PredTruth, Class<? extends PredicateTruth>> PRED_TRUTH_MAP = new HashMap<PredTruth, Class<? extends PredicateTruth>>();
	public static Map<eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth, Class<?  extends ClauseTruth>> CLAUSE_TRUTH_MAP = new HashMap<eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth, Class<? extends ClauseTruth>>();
	public static Map<eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty, Class<? extends NegationAndUncertainty>> NU_MAP = new HashMap<eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty, Class<? extends NegationAndUncertainty>>();
	public static Map<eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature, Class<? extends PredicateSignature>> SIG_MAP = new HashMap<eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature, Class<?  extends PredicateSignature>>();
	static
	{
		// predicate truth mapping
		PRED_TRUTH_MAP.put(PredTruth.P, PredicateTruthPositive.class);
		PRED_TRUTH_MAP.put(PredTruth.N, PredicateTruthNegative.class);
		PRED_TRUTH_MAP.put(PredTruth.U, PredicateTruthUncertain.class);
		PRED_TRUTH_MAP.put(PredTruth.O, PredicateTruthNotIdentified.class);
		
		// clause truth mapping
		CLAUSE_TRUTH_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth.P, ClauseTruthPositive.class);
		CLAUSE_TRUTH_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth.N, ClauseTruthNegative.class);
		CLAUSE_TRUTH_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth.U, ClauseTruthUncertain.class);
		CLAUSE_TRUTH_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth.O, ClauseTruthNotIdentified.class);
		
		// negation and uncertainty mapping
		NU_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty.P, NegationAndUncertaintyPositive.class);
		NU_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty.N, NegationAndUncertaintyNegative.class);
		NU_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty.U, NegationAndUncertaintyUncertain.class);
		
		// predicate signature mapping
		// signature: -/-
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.N_N, PredicateSignatureNegativeNegative.class);
		// signature: -/+
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.N_P, PredicateSignatureNegativePositive.class);
		// signature: -/?
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.N_U, PredicateSignatureNegativeUncertain.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.N_U_InfP, PredicateSignatureNegativeUncertain.class);
		// signature: +/-
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_N, PredicateSignaturePositiveNegative.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_N_InfP, PredicateSignaturePositiveNegative.class);
		// signature: +/+
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_P, PredicateSignaturePositivePositive.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_P_FinP, PredicateSignaturePositivePositive.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_P_FinP_N_P_InfP, PredicateSignaturePositivePositive.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_P_FinP_N_U_InfP, PredicateSignaturePositivePositive.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_P_FinP_P_N_InfP, PredicateSignaturePositivePositive.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_P_FinP_P_U_InfP, PredicateSignaturePositivePositive.class);
		// signature: +/?
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_U, PredicateSignaturePositiveUncertain.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_U_FinP, PredicateSignaturePositiveUncertain.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.P_U_InfP, PredicateSignaturePositiveUncertain.class);
		// signature: ?/-
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.U_N, PredicateSignatureUncertainNegative.class);
		// signature: ?/+
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.U_P, PredicateSignatureUncertainPositive.class);	
		// signature: ?/? (default in unknown cases)
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.U_U, PredicateSignatureUncertainUncertain.class);
		SIG_MAP.put(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature.NOT_IN_LEXICON, PredicateSignatureUncertainUncertain.class);
	};
	

}
