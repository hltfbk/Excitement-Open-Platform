package eu.excitementproject.eop.common.component;

//import eu.excitementproject.eop.common.configuration.CommonConfig;
//import eu.excitementproject.eop.common.exception.ComponentException;
//import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * <P>
 * There is a small set of methods that are common to all components whatever their internal structure.
These methods are primarily concerned with the administrative aspects of components and their
interactions with the EDA. These methods form the Components interface. All more specific interfaces
as outlined in the following sections are subinterfaces of Component.
 * 
 * <P>Previously, the method void initialize(CommonConfig c) was included in the interface 
 * Component. This means that all components are supposed to be initialized by this method, 
 * with a configuration object. Since specification verion 1.1.2, the method is removed from 
 * the interface. This changes the expected behavior of a component for its initialization.
 * 
 * All component implementations should satisfy the following conditions.
 * <OL>
 * <LI> All configurable/settable parameters are exposed in the constructor. So basically, 
 * a user can initialize and run a component; even without a configuration 
 * (CommonConfiguration) object, as long as the user supplies correct values for the arguments.
 * <LI> Components that need to read from the configuration shall provide a overridden constructor, 
 * which gets a CommonConfig object. (For example, GermaNet(CommonConfig c), or 
 * EnglishWordNet(CommonConfig c).) This constructor code knows which part of the common 
 * configuration is relevant to the component, and reads the configuration values from the 
 * configuration, and call the first constructor.
 * </OL>
 *
 * @author Gil
 */
public interface Component {
	
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
