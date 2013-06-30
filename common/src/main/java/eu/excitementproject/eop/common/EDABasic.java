package eu.excitementproject.eop.common;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * This interface defines the basic capability of EDAs that all EDAs must 
 * support. It has four methods. <code>process()</code> is main access method 
 * of textual entailment decision. It uses JCas as input type, and TEDecision 
 * as output type.
 * 
 * @author Gil
 * @param <T> a type that extends TEDecision. An instance of T will be returned 
 * as the processing result. 
 */
public interface EDABasic<T extends TEDecision> {
	
	/** This method will be called by the top level programs as the signal for 
	 * initializing the EDA. All initialization of an EDA like setting up sub 
	 * components and connecting resources must be done in this method. An EDA 
	 * implementation must check the configuration and raise exceptions if the 
	 * provided configuration is not compatible. Initialize is also responsible 
	 * for passing the configuration to common sub-components. 
	 * 
	 * <P>
	 * At the initialization of core components (like distance calculation 
	 * components or knowledge resource components), they will raise exceptions 
	 * if the configuration is not compatible with the component. initialize() 
	 * must pass through all exceptions that occurred in the subcomponent 
	 * initialization to the top level.(See spec section 4.9.1 for more info)
	 * @param config a CommonConfig instance. This configuration object holds platform-wide configuration.
An EDA should process the object to retrieve relevant configuration values for the EDA.
	 */
	public void initialize (CommonConfig config) throws ConfigurationException, EDAException, ComponentException;

	 
	/** This is the main access point for the top level. The top level 
	 * application can only use this method when the EDA is properly configured 
	 * and initialized. Each time this method is called, the EDA should check the 
	 * input for its compatibility. Within the EXCITEMENT platform, EDA 
	 * implementations are decoupled with linguistic analysis pipelines, and you 
	 * cannot blindly assume that CAS input is valid. EDA implementation must check 
	 * the existence of proper annotation layers corresponding to the configuration 
	 * of the EDA. The TE decision is returned as an object that extends TEDecision 
	 * interface which essentially holds the decision as enum value, numeric 
	 * confidence value and additional info.

	 * @param aCas
	 * @return
	 * @throws EDAException
	 * @throws ComponentException
	 */
	public T process(JCas aCas) throws EDAException, ComponentException; 
	
	/**
	 * This method provides a graceful exit for the EDA. This method will be called by top-level as a signal
to disengage all resources. All resources allocated by the EDA should be released when this method
is called.
	 */
	public void shutdown(); 
	
	
	/**
	 * startTraining interface is the common interface for EDA training. 
	 * The interface signals the start of the training with the given 
	 * configuration c.
	 * 
	 * Please see Spec1.1 section 4.2.1.4 and 4.2.1.7 for startTraining() issue.  
	 */
	
	public void startTraining(CommonConfig c) throws ConfigurationException, EDAException, ComponentException; 
	
}
