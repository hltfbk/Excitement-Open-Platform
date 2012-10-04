package eu.excitementproject.eop.common;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * There is a small set of methods that are common to all components whatever their internal structure.
These methods are primarily concerned with the administrative aspects of components and their
interactions with the EDA. These methods form the Components interface. All more specific interfaces
as outlined in the following sections are subinterfaces of Components. [Spec section 4.4] 
 * @author Gil
 */
public interface Components {

	/**
	 * This method will be called by the component user as the signal for initializing 
	 * the component.All initialization (including connecting and preparing resources) 
	 * should be done within this method. Implementations must check the configuration and 
	 * raise exceptions if the provided configuration is not compatible with the implementation.
	 * 
	 * @param config a common configuration object. This configuration object holds the platform-
wide configuration. An implementation should process the object to retrieve relevant configuration
values for the component. 
	 */
	public void initialize(CommonConfig config) throws ConfigurationException, ComponentException;
	
	
	/**
	 * This method provides the (human-readable) name of the component. It is used to 
	 * identify the relevant section in the common configuration for the current component. 
	 * See Spec Section 5.1.2, “Overview of the common configuration ” and Section 4.9.3, 
	 * “Component name and instance name”.
	 */
	public String getComponentName();
	
	
	/** This method provides the (human-readable) name of the instance. It is used to 
	 * identify the relevant subsection in the common configuration for the current component. 
	 * See Spec Section 5.1.2, “Overview of the common configuration ” and Section 4.9.3, 
	 * “Component name and instance name”. Note that this method can return null value, if 
	 * and only if all instances of the component shares the same configuration.
	 */
	public String getInstanceName(); 
	
}
