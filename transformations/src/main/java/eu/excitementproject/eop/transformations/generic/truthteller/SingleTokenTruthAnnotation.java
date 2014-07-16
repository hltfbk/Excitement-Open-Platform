package eu.excitementproject.eop.transformations.generic.truthteller;

import java.util.Set;

import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/* 
 * A container for the result of truth annotation on a single token
 * contains Predicate Truth, Clause Truth, Negation and Uncertainty, and Predicate Signature
 * If any of these is missing, an empty string is expected.
 */

public class SingleTokenTruthAnnotation {
	
	private PredTruth predicateTruthValue;
	private ClauseTruth clauseTruthValue;
	private NegationAndUncertainty nuValue;
	private PredicateSignature predicateSignatureValue;
	/*
	private String SpredicateTruthValue;
	private String SclauseTruthValue;
	private String SnuValue;
	private String SpredicateSignatureValue;
	*/
	
	private Set<ExtendedNode> subtree;
	private int subtreeMinimalIndex,subtreeMaximalIndex;
	
	public SingleTokenTruthAnnotation(PredTruth pt,ClauseTruth ct, NegationAndUncertainty nu, PredicateSignature sig, Set<ExtendedNode> sub){
		predicateTruthValue = pt;
		clauseTruthValue = ct;
		nuValue = nu;
		predicateSignatureValue = sig;
		subtree=sub;
		
		/*
		switch(pt){
			case N: SpredicateTruthValue = "eu.excitement.type.predicatetruth.PredicateTruthNegative";
				break;
			case O: SpredicateTruthValue = "eu.excitement.type.predicatetruth.PredicateTruthNotIdentified";
				break;
			case P: SpredicateTruthValue = "eu.excitement.type.predicatetruth.PredicateTruthPositive";
				break;
			case U: SpredicateTruthValue = "eu.excitement.type.predicatetruth.PredicateTruthUncertain";
				break;
			default:
				break;
			
		}*/
	}

	public SingleTokenTruthAnnotation(PredTruth pt,ClauseTruth ct, NegationAndUncertainty nu, PredicateSignature sig){
		this(pt,ct,nu,sig,null);
	}
	
	
	
	public int getSubtreeMinimalIndex() {
		return subtreeMinimalIndex;
	}

	public void setSubtreeMinimalIndex(int subtreeMinimalIndex) {
		this.subtreeMinimalIndex = subtreeMinimalIndex;
	}

	public int getSubtreeMaximalIndex() {
		return subtreeMaximalIndex;
	}

	public void setSubtreeMaximalIndex(int subtreeMaximalIndex) {
		this.subtreeMaximalIndex = subtreeMaximalIndex;
	}

	public PredTruth getPredicateTruthValue() {
		return predicateTruthValue;
	}

	public ClauseTruth getClauseTruthValue() {
		return clauseTruthValue;
	}

	public NegationAndUncertainty getNuValue() {
		return nuValue;
	}

	public PredicateSignature getPredicateSignatureValue() {
		return predicateSignatureValue;
	}

	public Set<ExtendedNode> getSubtree() {
		return subtree;
	}


	public void setSubtree(Set<ExtendedNode> subtree) {
		this.subtree = subtree;
	}
	
	
	

}
