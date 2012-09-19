package eu.excitementproject.eop.core.configuration;

public abstract class NameValueTable {

	abstract public String getString(String name);
	
	abstract public Integer getInteger(String name);
	
	abstract public Double getDouble(String name);
	
	abstract public void setString(String name, String value);
	
	abstract public void setInteger(String name, Integer value); 
	
	abstract public void setDouble(String name, Double value); 
	
}
