package eu.excitementproject.eop.transformations.biu.en.predicatetruth;

import java.util.List;

import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;

/** 
 * A container for the result of truth annotation on a single token
 * contains Predicate Truth, Clause Truth, Negation and Uncertainty, and Predicate Signature
 * If any of these is missing, an empty string is expected.
 * @author Gabi Stanovsky
 * @since Aug 2014
 */

public class SingleTokenTruthAnnotation {
	
	private PredTruth predicateTruthValue;
	private ClauseTruth clauseTruthValue;
	private NegationAndUncertainty nuValue;
	private PredicateSignature predicateSignatureValue;
	
	private List<ExtendedNode> subtree;
	private Integer subtreeMinimalIndex,subtreeMaximalIndex;
	
	public SingleTokenTruthAnnotation(PredTruth pt,ClauseTruth ct, NegationAndUncertainty nu, PredicateSignature sig, List<ExtendedNode> sub){
		predicateTruthValue = pt;
		clauseTruthValue = ct;
		nuValue = nu;
		predicateSignatureValue = sig;
		subtree=sub;
		subtreeMaximalIndex = null;
		subtreeMinimalIndex = null;
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

	public List<ExtendedNode> getSubtree() {
		return subtree;
	}


	public void setSubtree(List<ExtendedNode> subtree) {
		this.subtree = subtree;
	}
	
	
	

}
