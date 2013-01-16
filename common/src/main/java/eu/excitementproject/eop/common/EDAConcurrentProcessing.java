package eu.excitementproject.eop.common;

import java.util.List;

import org.apache.uima.jcas.JCas;


/** The goal of this interface is to provide an access method for 
 * EDAs (and/or EDA wrappers) that can process a set of entailment 
 * decision problems concurrently.
 * 
 * <P> The interface receives a set of TE decision problems as a list of JCAS. 
 * Each CAS object holds an entailment problem (as that of EDABasic.process() argument). 
 * The result is returned as a list of TEDecision objects. The interface is not an 
 * asynchronous interface: it will block until it process all the given problem list, 
 * and return the results only after all processing is done. 
 * 
 * <P> The expected behavior of 
 * implementation of this interface is to process the given dataset concurrently with 
 * a number of threads, using general Java concurrent capabilities like Executors 
 * and thread pools. The number of threads and other configurations should be defined in 
 * the common configuration. Proper initialization (for example, initializing multiple 
 * instances for non thread-safe components) should be ensured by the implementation so 
 * that the user does not need to care about sub-components and how they are being run.
 * 
 * <P> This interface can be directly implemented by an EDA (i.e., a class that already 
 * implements EDABasic and/or other interfaces). It can be also implemented by a concurrent 
 * running wrapper (a "runner") for the EDA, if the implementation of this interface does 
 * not lend itself naturally to the internal structure of the EDA.
 * 
 * @author Gil
 * [see Spec 1.1DRAFT Section 4.8 for complete info] 
 */

public interface EDAConcurrentProcessing {

	public List<TEDecision> processDataSetConcurrently(List<JCas> casList);
	
}
