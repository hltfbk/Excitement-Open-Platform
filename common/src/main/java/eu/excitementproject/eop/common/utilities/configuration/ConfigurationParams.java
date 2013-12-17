package eu.excitementproject.eop.common.utilities.configuration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.StringUtil;

public interface ConfigurationParams
{
	/**
	 * get a String value from the map, assuring both key and value aren't null. <BR>
	 * If working in a mode of expanding environment variables (default: not),
	 * then it also expands the environment variables before continuing. (see
	 * {@link #setExpandingEnvironmentVariables(boolean)})
	 * 
	 * @param paramName
	 * @return string value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public String get(String paramName) throws ConfigurationException;
	
	/**
	 * Indicates whether the values are processed to expand environment
	 * variables. default: <tt>false</tt>
	 * 
	 * @see #setExpandingEnvironmentVariables(boolean)
	 * @return
	 */
	public boolean isExpandingEnvironmentVariables();
	
	/**
	 * Set to <tt>true</tt> if you want that each value in the configuration
	 * file will first be processed such that environment variables are
	 * expanded, then processed according to the appropriate method getXXX. <BR>
	 * <B>Default: <tt>false</tt></B>
	 * 
	 * @see StringUtil#expandEnvironmentVariables(String)
	 * @param expandingEnvironmentVariables
	 */
	public void setExpandingEnvironmentVariables(boolean expandingEnvironmentVariables);
	
	/**
	 * get a boolean param
	 * 
	 * @param paramName
	 * @return param value
	 * @throws ConfigurationException
	 *             if the parameter value is missing or invalid, 'cos if its
	 *             missing, then parseBoolean() returns "false", which is
	 *             misleading.
	 */
	public boolean getBoolean(String paramName) throws ConfigurationException;
	
	/**
	 * return File matching paramName.
	 * 
	 * @param paramName
	 * @return File matching paramName
	 * @throws ConfigurationException
	 *             if file doesn't exist, or if the parameter value is missing
	 */
	public File getFile(String paramName) throws ConfigurationException;
	
	/**
	 * return the File matching paramName in directory iDir.
	 * 
	 * @param paramName
	 * @param iDir
	 * @return File matching paramName in iDir
	 * @throws ConfigurationException
	 *             if file doesn't exist, or if the parameter value is missing
	 */
	public File getFile(String paramName, File iDir) throws ConfigurationException;
	public File getDirectory(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return param value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public long getLong(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return param value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public int getInt(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return double value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public double getDouble(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return float value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public float getFloat(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return string value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public String getString(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return String array
	 * @throws ConfigurationException
	 */
	public String[] getStringArray(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return list of strings for paramName
	 * @throws ConfigurationException
	 */
	public List<String> getStringList(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return a Double array
	 * @throws ConfigurationException
	 */
	public double[] getDoubleArray(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return array of floats
	 * @throws ConfigurationException
	 */
	public float[] getFloatArray(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return array of longs
	 * @throws ConfigurationException
	 */
	public long[] getLongArray(String paramName) throws ConfigurationException;
	
	/**
	 * @param paramName
	 * @return array of ints
	 * @throws ConfigurationException
	 */
	public int[] getIntArray(String paramName) throws ConfigurationException;
	
	/**
	 * return an array of Files matching the file names mapped to the key
	 * 
	 * @param paramName
	 * @return an array of File matching the file names mapped to the key
	 * @throws ConfigurationException
	 */
	public File[] getFileArray(String paramName) throws ConfigurationException;
	
	/**
	 * return an array of Files matching the directory names mapped to the key
	 * 
	 * @param paramName
	 * @return an array of File matching the file names mapped to the key
	 * @throws ConfigurationException
	 */
	public File[] getDirArray(String paramName) throws ConfigurationException;
	
	/**
	 * Returns a set of enum values of type enumType.
	 * <BR>
	 * <B>Note: if the order is important for you, you must use
	 * {@link #getEnumList(Class, String)}</B>
	 * 
	 * @param <T>
	 * @param enumType
	 * @param key
	 * @return a set of enum values of type enumType
	 * @throws ConfigurationException
	 */
	public <T extends Enum<T>> Set<T> getEnumSet(Class<T> enumType, String key) throws ConfigurationException;
	
	
	public <T extends Enum<T>> List<T> getEnumList(Class<T> enumType, String key) throws ConfigurationException;
	
	/**
	 * return a single enum value of type enumType
	 * 
	 * @param <T>
	 * @param enumType
	 * @param key
	 * @return a single enum value of type enumType
	 * @throws ConfigurationException
	 */
	public <T extends Enum<T>> T getEnum(Class<T> enumType, String key) throws ConfigurationException;
	
	/**
	 * Returns a map of key-value strings, for parameter of form
	 * "[key1,value1],[key2,value2],[key3,value3]..."
	 * 
	 * @param key the parameter key
	 * @return the dictionary as a LinkedHashMap
	 * @throws ConfigurationException any error
	 */
	public LinkedHashMap<String, String> getDictionary(String key) throws ConfigurationException;
	public String getModuleName();
	

}
