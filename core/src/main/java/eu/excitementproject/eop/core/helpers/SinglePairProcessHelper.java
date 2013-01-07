package eu.excitementproject.eop.core.helpers;

import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.exception.ComponentException;

/**
 * <P>This interface is a convenience interface for "end users" of an entailment engine. The implementation
of this interface uses a specific implementation of EDA (EDABasic), and provides a method that allows
the user to run the complete engine including LAP and Entailment Core with a single call. The access
method receives two String objects as input (the text and the hypothesis, respectively), and returns
the result (an object that extends TEDecision) for them. </P>
 * <P> Since this is a convenience interface, it is limited with regard to functionality and efficiency. It does
not support context sentences, or multiple text/hypothesis situations. Also, calling the LAP for just one
sentence pair is presumably quite inefficient. </P>
 * <P> Note that, unlike MultipleTHModeHelper, the platform cannot share a single ProcessHelper imple-
mentation for all EDAs. Each EDA needs to run a different LAP, thus each EDA is expected to have
its own ProcessHelper. </P> 
 * @author tailblues
 * @param <T> One of TEDecision implementation. 
 */
public interface SinglePairProcessHelper<T extends TEDecision> {
	
	/**
	 * The method first calls needed LAP pipeline to analyze the given raw text and hypothesis. 
	 * Then, it uses <code>EDABasic process()</code> method to deliver the result. 
	 * @param text a String that will be read as "text"
	 * @param hypothesis a String that will be read as "hypothesis" 
	 * @return A TEDecision object with type T
	 */
	public T processRawInput(String text, String hypothesis) throws EDAException, ComponentException; 

}
