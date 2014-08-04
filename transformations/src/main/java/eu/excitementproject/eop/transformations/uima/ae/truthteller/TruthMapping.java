package eu.excitementproject.eop.transformations.generic.truthteller;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/* 
 * Conversion class from Truthteller's annotations to UIMA annotations
 * Each static function converts a different annotation type.
 */

public class TruthMapping {
	
	public static PredicateTruth mapPredicateTruth(PredTruth pt, JCas jcas, int begin, int end){
		 switch (pt) {
         case P: return new PredicateTruthPositive(jcas,begin,end); 
         case N: return new PredicateTruthNegative(jcas,begin,end); 
         case U: return new PredicateTruthUncertain(jcas,begin,end); 
         default: return new PredicateTruthNotIdentified(jcas,begin,end); //case O
		 }
	}
	
	public static ClauseTruth mapClauseTruth(eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth ct, JCas jcas, Set<Token> subtree,int begin,int end){
		int subtreeSize = subtree.size();
		ClauseTruth ret = null;
		
		//decide on type of clause truth
		
		switch(ct){
			case N: ret = new ClauseTruthNegative(jcas,begin,end);
				break;
			case O: ret = new ClauseTruthNotIdentified(jcas,begin,end);
				break;
			case P: ret = new ClauseTruthPositive(jcas,begin,end);
				break;
			case U: ret = new ClauseTruthUncertain(jcas,begin,end);
				break;
			default:
				break;
		}
		
		// set the subtree token as a feature structure		
		FSArray subtreeFSArray = new FSArray(jcas, subtreeSize);
		subtreeFSArray.copyFromArray(subtree.toArray(new Token[subtree.size()]), 0, 0, subtreeSize);
		ret.setClauseTokens(subtreeFSArray);
		return ret;
	}
	
	public static NegationAndUncertainty mapNegationAndUncertainty(eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty nu, JCas jcas,int begin,int end){
		switch(nu){
			case N: return new NegationAndUncertaintyNegative(jcas,begin,end);
			case P: return new NegationAndUncertaintyPositive(jcas, begin, end);
			case U: return new NegationAndUncertaintyUncertain(jcas, begin, end);
			default: return null;
			
			}
	}
	
	public static PredicateSignature mapPredicateSignature(eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature sig,JCas jcas,int begin,int end){
		switch (sig){
		
		// signature: -/-
		case N_N: 
			return new PredicateSignatureNegativeNegative(jcas,begin,end);
		
		// signature: -/+
		case N_P:
			return new PredicateSignatureNegativePositive(jcas,begin,end);
			
		// signature: -/?
		case N_U:
		case N_U_InfP:
			return new PredicateSignatureNegativeUncertain(jcas,begin,end);
			
		// signature: +/-
		case P_N:
		case P_N_InfP:
			return new PredicateSignaturePositiveNegative(jcas,begin,end);
		
		// signature: +/+
		case P_P:	
		case P_P_FinP:
		case P_P_FinP_N_P_InfP:			
		case P_P_FinP_N_U_InfP:
		case P_P_FinP_P_N_InfP:
		case P_P_FinP_P_U_InfP:
			return new PredicateSignaturePositivePositive(jcas,begin,end);
		
		// signature: +/?
		case P_U:
		case P_U_FinP:
		case P_U_InfP:
			return new PredicateSignaturePositiveUncertain(jcas,begin,end);
		
		// signature: ?/-
		case U_N: 
			return new PredicateSignatureUncertainNegative(jcas,begin,end);
			
		// signature: ?/+
		case U_P: 
			return new PredicateSignatureUncertainPositive(jcas,begin,end);
			
		// signature: ?/? (default in unknown cases)
		case NOT_IN_LEXICON:
		default: 
			return new PredicateSignatureUncertainUncertain(jcas,begin,end);
		
		}
		
	}
	

}
