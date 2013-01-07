package eu.excitementproject.eop.common;

import java.util.List;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.exception.ComponentException;

/** 
 * Multiple Text and/or Hypothesis interfaces: 
 * Each of the interface defines a method, namely <code>processMultiT</code>, <code>processMultiH</code>, 
 * and <code>processMultiTH</code>. EDAs may choose to support them or not, since supporting 
 * multiple T/H interfaces are optional. Multiple Texts and Hypotheses are marked 
 * in the CAS by multiple EXCITEMENT.entailment.Pair annotations. 
 */
public interface EDAMultiH<T extends TEDecision> extends EDABasic<T> {
	
	public List<T> processMultiH(JCas aCas) throws EDAException, ComponentException;
	
}
