package eu.excitementproject.eop.common.configuration;

import java.io.File;
import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * <P> This abstract class outlines the capability of NameValueTable. 
 * 
 *  <P> 
 * Configuration data is stored as text strings in the XML files. 
 * In this outline we only show get methods for a few basic types, like string, 
 * integer, double and File. Note that the actual implementation can provide many more 
 * (like get methods that will return enum, list of enums or other basic types, etc), 
 * depending on the need of the Component writers. 
 * 
 * <P>
 * All get methods have a single string argument, the name part of a name-value pair. A get method returns
the corresponding value from the name-value pair. Each get method will try to convert the XML value
into the requested type. If the conversion fails, the get method will raise a conversion exception (one
of ConfigurationException).
* <P> Set methods are provided for editing existing values or writing new values. The values added/modified
by set methods will only affect the XML file by saveConfiguration().
Note that set methods are provided mainly for user level or transduction layer level access. The methods
are not expected to be called from a entailment core component.
*/

public abstract class NameValueTable {
	
	abstract public String getString(String name) throws ConfigurationException;
	
	abstract public Integer getInteger(String name) throws ConfigurationException;
	
	abstract public Double getDouble(String name) throws ConfigurationException;
	
	abstract public File getFile(String name) throws ConfigurationException;
	
	abstract public File getDirectory(String name) throws ConfigurationException;
	
	abstract public void setString(String name, String value);
	
	/*
	abstract public void setInteger(String name, Integer value); 
	
	abstract public void setDouble(String name, Double value); 
	
	abstract public void setFile(String name, File value); 
	
	abstract public void setDirectory(String name, File value); 
	*/
	
}
