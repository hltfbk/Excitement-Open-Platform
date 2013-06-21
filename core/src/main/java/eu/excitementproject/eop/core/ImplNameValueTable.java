package eu.excitementproject.eop.core;

import java.io.File;
import java.util.HashMap;

import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * This class implements NameValueTable
 *
 * @author Roberto Zanoli
 *
*/
public class ImplNameValueTable extends NameValueTable {
	
	/*
	* this table contains key, value pairs
	*/
	private HashMap<String, String> table = null;
	
	
	/**
	 * The constructor
	 *
	*/
	public ImplNameValueTable() {
		
		this.table = new HashMap<String, String>();
		
	}
	
	
	@Override
	public String getString(String name) throws ConfigurationException {
		
		String result = null;
		
		if (!this.table.containsKey(name))
			return result;
		
		try {
			result = (String)this.table.get(name);
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@Override
	public Integer getInteger(String name) throws ConfigurationException {
		
		Integer result = null;
		
		if (!this.table.containsKey(name))
			return result;
		
		try {
			result = Integer.valueOf((String)this.table.get(name));
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@Override
	public Double getDouble(String name) throws ConfigurationException {
		
		Double result = null;
		
		if (!this.table.containsKey(name))
			return result;
		
		try {
			result = Double.valueOf((String)this.table.get(name));
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@Override
	public File getFile(String name) throws ConfigurationException {
		
		File result = null;
		
		if (!this.table.containsKey(name))
			return result;
		
		try {
			result = new File((String)this.table.get(name));
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@Override
	public File getDirectory(String name) throws ConfigurationException {
		
		File result = null;
		
		if (!this.table.containsKey(name))
			return result;
		
		try {
			result = new File((String)this.table.get(name));
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@Override
	public void setString(String name, String value) {
		
		this.table.put(name, value);
		
	}
	
	
	/*
	public void setInteger(String name, Integer value) {
		
		this.table.put(name, value);
		
	}
	
	
	public void setDouble(String name, Double value) {
		
		this.table.put(name, value);
		
	}
	
	
	public void setFile(String name, File value) {
		
		this.table.put(name, value);
		
	}
	
	
	public void setDirectory(String name, File value) {
		
		this.table.put(name, value);
		
	}
	*/
	
	
}
