package eu.excitementproject.eop.common;

import eu.excitementproject.eop.common.configuration.CommonConfig;

/** Any entailment core component that supports reconfiguration should implement 
 * the capability though this interface. This also includes EDAs. If EDA needs to 
 * support online reconfiguration, it should support the capability with this 
 * interface. Note that when an EDA provides this interface, the EDA should also 
 * update the states of its sub-components according to the configuration method 
 * argument. It is theresponsibility of EDA implementers to ensure that 
 * configuration changes are handed through to subcomponents, if applicable. This 
 * may mean simply reconfiguring some sub-components (which support this) and 
 * re-initializing others (which do not). [Spec 1.1DRAFT section 4.9] 
 * @author Gil
 *
 */
public interface Reconfigurable {

	/** The interface provides a single method, reconfigure(). 
	 * It shares the same single argument with initialization(), an instance of 
	 * CommonConfig. However, the contract is different. Instead of copying and 
	 * initializing all of the passed configuration value, the implementation must 
	 * check the passed configuration and only reconfigure the changed values.
	 */
	public void reconfigure(CommonConfig config);
	
}
