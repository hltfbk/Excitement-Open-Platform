package eu.excitementproject.eop.common.utilities.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Wrapper over <code>java.util.Properties</code> that returns the values as other types
 * rather than String.
 * Properties values can be returned as:
 * <UL>
 * <LI>String</LI>
 * <LI>int</LI>
 * <LI>double</LI>
 * <LI>File</LI>
 * <LI>File (existence validated)</LI>
 * <LI>File (existence of a directory that is represented by that file is validated)</LI>
 * <LI>boolean</LI>
 * <LI>enum</LI>
 * <LI>set of enum values</LI>
 * </UL>
 * 
 * @author Asher Stern
 *
 */
public class TypedProperties
{
	public static final String BOOLEAN_CONSTANT_TRUE = "true";
	public static final String BOOLEAN_CONSTANT_FALSE = "false";
	public static final String SEPARATOR = ",";
	
	public static final boolean DEFAULT_ALWAYS_TRIM_VALUES = true;
	
	public TypedProperties(Properties properties) throws PropertiesException
	{
		if (null==properties) throw new PropertiesException("null==properties");
		this.properties = properties;
	}
	
	public static TypedProperties fromFile(String filename) throws PropertiesException
	{
		if (null==filename) throw new PropertiesException("null file name");
		File file = new File(filename);
		if (!file.exists()) throw new PropertiesException("file: "+file.getAbsolutePath()+" does not exist.");
		if (!file.isFile()) throw new PropertiesException("file: "+file.getAbsolutePath()+" is not a file.");
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			Properties realProperties = new Properties();
			realProperties.load(stream);
			return new TypedProperties(realProperties);
		}
		catch (FileNotFoundException e)
		{
			throw new PropertiesException("load problem",e);
		}
		catch (IOException e)
		{
			throw new PropertiesException("load problem",e);
		}
		finally
		{
			if (stream!=null)
			{
				try{stream.close();}
				catch (IOException e){}
			}
		}
	}
	
	public boolean isExist(String key) throws PropertiesException
	{
		if (null==key) throw new PropertiesException("null==key");
		boolean ret = false;
		if (this.properties.getProperty(key)!=null)
			ret = true;
		return ret;
	}
	
	public boolean areExist(List<String> keys) throws PropertiesException
	{
		boolean ret = true;
		if (keys!=null)
		{
			for (String key : keys)
			{
				if (isExist(key)) ;
				else ret = false;
			}
		}
		return ret;
	}
	
	public String getString(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		return value;
	}
	
	public boolean getBoolean(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		boolean ret = false;
		if (value.equalsIgnoreCase(BOOLEAN_CONSTANT_TRUE))
			ret = true;
		else if (value.equalsIgnoreCase(BOOLEAN_CONSTANT_FALSE))
			ret = false;
		else
			throw new PropertiesException("Given value: "+value+" for key: " +key+" is not a boolean.");
		
		return ret;
	}
	
	public <T extends Enum<T>> Set<T> getEnumSet(Class<T> enumType, String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);		
		Set<T> ret = new HashSet<T>();
		String[] values = value.split(SEPARATOR);
		try
		{
			for (String val : values)
			{
				val = val.trim();
				ret.add(Enum.valueOf(enumType, val));
			}
		}
		catch(IllegalArgumentException e)
		{
			throw new PropertiesException("bad argument",e);
		}
		catch(NullPointerException e)
		{
			throw new PropertiesException("One of the arguments was illegal. See nested exception",e);
		}

		return ret;
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> enumType, String key) throws PropertiesException
	{
		T ret = null;
		String value = getNormalizedValueForKey(key);
		try
		{
			ret = Enum.valueOf(enumType, value);
		}
		catch(IllegalArgumentException e)
		{
			throw new PropertiesException("bad argument",e);
		}
		catch(NullPointerException e)
		{
			throw new PropertiesException("One of the arguments was illegal. See nested exception",e);
		}
		
		return ret;
	}
	
	public int getInt(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		int ret = 0;
		try
		{
			ret = Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			throw new PropertiesException("key: "+key+" has the value: "+value+" which is not an integer number");
		}
		return ret;
	}
	
	public double getDouble(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		double ret = 0;
		try
		{
			ret = Double.parseDouble(value);
		}
		catch(NumberFormatException e)
		{
			throw new PropertiesException("key: "+key+" has the value: "+value+" which is not a number");
		}
		return ret;
	}
	
	public File getExistFile(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		File ret = null;
		ret = new File(value);
		if (!ret.exists()) throw new PropertiesException("The file: "+value+" for property: "+key+" does not exist.");
		if (!ret.isFile()) throw new PropertiesException("The file: "+value+" for property: "+key+" is not a file.");
		return ret;
	}
	
	public File getExistDirectory(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		File ret = null;
		ret = new File(value);
		if (!ret.exists()) throw new PropertiesException("The directory: "+value+" for property: "+key+" does not exist.");
		if (!ret.isDirectory()) throw new PropertiesException("The directory: "+value+" for property: "+key+" is not a directory.");
		return ret;
	}
	
	public File getValueAsFile(String key) throws PropertiesException
	{
		String value = getNormalizedValueForKey(key);
		File ret = new File(value);
		return ret;
	}
	
	
	public boolean isAlwaysTrimValues()
	{
		return alwaysTrimValues;
	}

	public void setAlwaysTrimValues(boolean alwaysTrimValues)
	{
		this.alwaysTrimValues = alwaysTrimValues;
	}

	protected String getNormalizedValueForKey(String key) throws PropertiesException
	{
		if (null==key) throw new PropertiesException("null==key");
		String value = properties.getProperty(key);
		if (null==value) throw new PropertiesException("Key: "+key+" does not exist.");
		if (alwaysTrimValues) value = value.trim();
		return value;
	}
	
	
	
	protected Properties properties;
	protected boolean alwaysTrimValues = DEFAULT_ALWAYS_TRIM_VALUES;

}
