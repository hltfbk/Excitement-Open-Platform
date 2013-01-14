package eu.excitementproject.eop.common.utilities.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.datastructures.KeyCaseInsensitiveHashTable;
import eu.excitementproject.eop.common.utilities.DictionaryRegExp;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.DictionaryRegExp.DictionaryRegExpException;
import eu.excitementproject.eop.common.utilities.file.FileUtils;


/**
 * <B>This class has to be removed, since Excitement uses another type of
 * configuration files.</B>
 * 
 * ConfigurationParams is an elaborated Hash table of Strings (keys are case
 * insensitive) with extra checks, used to represent one xml file
 * module/submodule, mapping parameter names to their values
 * <p>
 * The classes {@link ConfigurationFile} and {@link ConfigurationParams} use the
 * JaxB jars {@link https://jaxb.dev.java.net/} to provide a comprehensive and
 * more user friendly Java interface to XML files. They depend on the JaxB jar
 * files in JARS
 * 
 * <p>
 * See examples in Demo.xml, Demo.java
 * </p>
 * 
 * <p>
 * A good xml file for these classes would look like this:
 * <p>
 * {@code
 *  	<?xml version="1.0"?>
	
		<!DOCTYPE page [
		<!ENTITY jars "\\nlp-srv\jars\">		<!-- SOME COMMENT -->
		<!ENTITY stopwords "\\nlp-srv\Data\RESOURCES\stopwords-Eyal.txt">
		]>
		
		<configuration>
		
		<module name="logging">
			<param name="main-output-dir">\\nlp-srv\amnon\temp</param>
			<param name="experiment-name">rank dev</param>
			<param name="__COMMENTED-OUT-MODULE">mailer</param>
			<param name="log-file-max-size">500MB</param>
		</module>
		
		<!-- main module for LexicalGraph-->
		<module name="lexical inference">
			<param name="num of expansions">2</param>		<!-- number of steps when building the graph -->
			<param name="senses to use">1</param>			<!-- wn senses of seeds will be set to those sense numbers -->  
		</module>
		
		</configuration>
	}
 * <p>
 * Here's an example client code snippet:
 * <p>
 * The {@link ConfigurationParams} has also the ability to expand environment
 * variables, i.e. for each value, substitute the value of environment variable
 * to its actual value, as it is set by the OS. Environment variables are
 * recognized by either %ENVIRONMENT_VARIABLE_NAME% or
 * $ENVIRONMENT_VARIABLE_NAME or ${ENVIRONMENT_VARIABLE_NAME}. <BR>
 * Note that by default environment variables are <B>not</B> expanded. The user
 * has to explicitly set the configuration-param to expand environment
 * variables, by calling {@link #setExpandingEnvironmentVariables(boolean)}.
 * <p>
 * 
 * Here is an example code: <code>
 * <pre>	
 * ConfigurationFile conf = new ConfigurationFile(new File(fileName), true);
 * ConfigurationParams params = conf.getModuleConfiguration("data set");
 * File topDir = params.getFile("top-dir");
 * File gsFile = params.getFile("gold-standard-file");
 * List<String> topics = null;
 * if(params.containsKey("topics")){
 * 	topics = params.getStringList("topics");
 * }
 * </pre>
 * <code>
 * 
 * @see ConfigurationFile
 * @author BIU NLP legacy-code
 */
public class ConfigurationParams extends KeyCaseInsensitiveHashTable<String>
{

	// /////////////////////////////////////////////////////////////// public
	// section ////////////////////////////////////////////////////////

	/**
	 * Constructor with empty ConfigurationFile
	 */
	public ConfigurationParams()
	{
		super();
		m_ref = null;
	}

	/**
	 * Constructor
	 * 
	 * @param iRef
	 *            ConfigurationFile
	 */
	public ConfigurationParams(ConfigurationFile iRef)
	{
		this();
		m_ref = iRef;
	}

	/**
	 * Constructor
	 * 
	 * @param iRef
	 *            ConfigurationFile
	 * @param iModuleName
	 */
	public ConfigurationParams(ConfigurationFile iRef, String iModuleName)
	{
		this(iRef);
		m_moduleName = iModuleName;
	}
	
	
	/**
	 * Create ConfigurationParams for a module, based on a file-name and a module-name, with expanding environment variables.
	 * @throws ConfigurationException 
	 * @throws ConfigurationFileDuplicateKeyException 
	 */
	public static ConfigurationParams create(String fileName, String moduleName) throws ConfigurationFileDuplicateKeyException, ConfigurationException {
		ConfigurationFile file = new ConfigurationFile(fileName);
		file.setExpandingEnvironmentVariables(true);
		return file.getModuleConfiguration(moduleName);
	}

	/**
	 * Indicates whether the values are processed to expand environment
	 * variables. default: <tt>false</tt>
	 * 
	 * @see #setExpandingEnvironmentVariables(boolean)
	 * @return
	 */
	public boolean isExpandingEnvironmentVariables()
	{
		return expandingEnvironmentVariables;
	}

	/**
	 * Set to <tt>true</tt> if you want that each value in the configuration
	 * file will first be processed such that environment variables are
	 * expanded, then processed according to the appropriate method getXXX. <BR>
	 * <B>Default: <tt>false</tt></B>
	 * 
	 * @see StringUtil#expandEnvironmentVariables(String)
	 * @param expandingEnvironmentVariables
	 */
	public void setExpandingEnvironmentVariables(
			boolean expandingEnvironmentVariables)
	{
		this.expandingEnvironmentVariables = expandingEnvironmentVariables;
	}

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
	public boolean getBoolean(String paramName) throws ConfigurationException
	{
		String val = get(paramName);

		if (val == null)
		{
			throw new ConfigurationException("Missing boolean parameter: "
					+ getModuleName() + " : " + paramName);
		}
		if (!val.equalsIgnoreCase("true") && !val.equalsIgnoreCase("false"))
		{
			throw new ConfigurationException("Invalid value: '" + val
					+ "' for boolean parameter: '" + paramName
					+ "' in module '" + getModuleName() + "'");
		}

		return Boolean.parseBoolean(val);
	}

	/**
	 * return File matching paramName.
	 * 
	 * @param paramName
	 * @return File matching paramName
	 * @throws ConfigurationException
	 *             if file doesn't exist, or if the parameter value is missing
	 */
	public File getFile(String paramName) throws ConfigurationException
	{
		return getFileOrDirByParam(paramName, null, true);
	}

	/**
	 * return the File matching paramName in directory iDir.
	 * 
	 * @param paramName
	 * @param iDir
	 * @return File matching paramName in iDir
	 * @throws ConfigurationException
	 *             if file doesn't exist, or if the parameter value is missing
	 */
	public File getFile(String paramName, File iDir)
			throws ConfigurationException
	{
		return getFileOrDirByParam(paramName, iDir, true);
	}

	public File getDirectory(String paramName) throws ConfigurationException
	{
		return getFileOrDirByParam(paramName, null, false);
	}

	/**
	 * @param paramName
	 * @return param value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public long getLong(String paramName) throws ConfigurationException
	{
		return (Long) checkNumericValue(paramName, NumberClass.LONG);
	}

	/**
	 * @param paramName
	 * @return param value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public int getInt(String paramName) throws ConfigurationException
	{
		return (Integer) checkNumericValue(paramName, NumberClass.INTEGER);
	}

	/**
	 * @param paramName
	 * @return double value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public double getDouble(String paramName) throws ConfigurationException
	{
		return (Double) checkNumericValue(paramName, NumberClass.DOUBLE);
	}

	/**
	 * @param paramName
	 * @return float value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public float getFloat(String paramName) throws ConfigurationException
	{
		return (Float) checkNumericValue(paramName, NumberClass.FLOAT);
	}

	/**
	 * @param paramName
	 * @return string value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	public String getString(String paramName) throws ConfigurationException
	{
		return get(paramName);
	}

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
	public String get(String paramName) throws ConfigurationException
	{
		// check for null value
		if (paramName == null)
			throw new ConfigurationException("Got null paramName");

		String str = super.get(paramName);

		// check for null value
		if (str == null)
			throw new ConfigurationException("Missing string value for '"
					+ paramName + "': '" + str + "' in module '"
					+ getModuleName() + "'");

		if (expandingEnvironmentVariables)
		{
			str = StringUtil.expandEnvironmentVariables(str);
		}

		return str;
	}
	

	/**
	 * @param paramName
	 * @return String array
	 * @throws ConfigurationException
	 */
	public String[] getStringArray(String paramName)
			throws ConfigurationException
	{
		String[] ret = null;

		String stringsList = get(paramName);
		if (stringsList.length() == 0)
		{
			ret = new String[0];
		}
		else
		{
			ret = stringsList.split(COMMA);
			String[] newRet = new String[ret.length];
			for (int index=0;index<ret.length;++index)
			{
				newRet[index]=ret[index].trim();
			}
			ret = newRet;
		}

		return ret;
	}

	/**
	 * @param paramName
	 * @return list of strings for paramName
	 * @throws ConfigurationException
	 */
	public List<String> getStringList(String paramName)
			throws ConfigurationException
	{
		Vector<String> toReturn = null;
		String[] ret = getStringArray(paramName);
		if (ret != null)
		{
			toReturn = new Vector<String>();
			for (String s : ret)
			{
				toReturn.add(s);
			}
		}
		return toReturn;
	}

	/**
	 * @param paramName
	 * @return a Double array
	 * @throws ConfigurationException
	 */
	public double[] getDoubleArray(String paramName)
			throws ConfigurationException
	{
		double[] doubleArr;
		String[] strArrOfDoubles = getArrayValues(paramName);

		if (strArrOfDoubles == null)
			doubleArr = null;
		else
		{
			doubleArr = new double[strArrOfDoubles.length];
			for (int i = 0; i < strArrOfDoubles.length; i++)
				doubleArr[i] = Double.parseDouble(strArrOfDoubles[i]);
		}

		return doubleArr;
	}

	/**
	 * @param paramName
	 * @return array of floats
	 * @throws ConfigurationException
	 */
	public float[] getFloatArray(String paramName)
			throws ConfigurationException
	{
		float[] arrFloats;
		String[] strArrOfFloats = getArrayValues(paramName);

		if (strArrOfFloats == null)
			arrFloats = null;
		else
		{
			arrFloats = new float[strArrOfFloats.length];
			for (int i = 0; i < strArrOfFloats.length; i++)
				arrFloats[i] = Float.parseFloat(strArrOfFloats[i]);
		}

		return arrFloats;
	}

	/**
	 * @param paramName
	 * @return array of longs
	 * @throws ConfigurationException
	 */
	public long[] getLongArray(String paramName) throws ConfigurationException
	{
		long[] arrLongs;
		String[] strArrOfLongs = getArrayValues(paramName);

		if (strArrOfLongs == null)
			arrLongs = null;
		else
		{
			arrLongs = new long[strArrOfLongs.length];
			for (int i = 0; i < strArrOfLongs.length; i++)
				arrLongs[i] = Long.parseLong(strArrOfLongs[i]);
		}

		return arrLongs;
	}

	/**
	 * @param paramName
	 * @return array of ints
	 * @throws ConfigurationException
	 */
	public int[] getIntArray(String paramName) throws ConfigurationException
	{
		int[] arrInts;
		String[] strArrOfInts = getArrayValues(paramName);

		if (strArrOfInts == null)
			arrInts = null;
		else
		{
			arrInts = new int[strArrOfInts.length];
			for (int i = 0; i < strArrOfInts.length; i++)
				arrInts[i] = Integer.parseInt(strArrOfInts[i]);
		}

		return arrInts;
	}

	/**
	 * return an array of Files matching the file names mapped to the key
	 * 
	 * @param paramName
	 * @return an array of File matching the file names mapped to the key
	 * @throws ConfigurationException
	 */
	public File[] getFileArray(String paramName) throws ConfigurationException
	{
		File[] fileArr;
		String names = this.get(paramName).trim();

		// return null if the name is null
		if (names.length() == 0)
			fileArr = null;
		else
		{
			String[] nameArr = names.split(COMMA);
			fileArr = new File[nameArr.length];
			for (int i = 0; i < nameArr.length; i++)
				fileArr[i] = getFileOrDirByName(nameArr[i], null, true, paramName);
		}

		return fileArr;
	}

	/**
	 * return an array of Files matching the directory names mapped to the key
	 * 
	 * @param paramName
	 * @return an array of File matching the file names mapped to the key
	 * @throws ConfigurationException
	 */
	public File[] getDirArray(String paramName) throws ConfigurationException
	{
		File[] dirArr;
		String names = this.get(paramName).trim();

		// return null if the name is null
		if (names.length() == 0)
			dirArr = null;
		else
		{
			String[] nameArr = names.split(COMMA);
			dirArr = new File[nameArr.length];
			for (int i = 0; i < nameArr.length; i++)
				dirArr[i] = getFileOrDirByName(nameArr[i], null, false, paramName);
		}

		return dirArr;
	}

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
	public <T extends Enum<T>> Set<T> getEnumSet(Class<T> enumType, String key) throws ConfigurationException
	{
		Set<T> ret = new LinkedHashSet<T>();
		ret.addAll(getEnumList(enumType,key));
		return ret;
	}

	
	public <T extends Enum<T>> List<T> getEnumList(Class<T> enumType, String key) throws ConfigurationException
	{
		List<T> ret;
		String value = get(key);
		if (value.trim().length()>0)
		{
			String[] values = value.split(COMMA);
			ret = new ArrayList<T>(values.length);
			try
			{
				for (String val : values)
				{
					val = val.trim();
					ret.add(Enum.valueOf(enumType, val));
				}
			} catch (IllegalArgumentException e)
			{
				throw new ConfigurationException("bad argument. Found in file " + this.m_ref.getConfFile().getName() + " in module " + m_moduleName, e);
			} catch (NullPointerException e)
			{
				throw new ConfigurationException(
						"One of the arguments was illegal. Found in file " + this.m_ref.getConfFile().getName() + " in module " + m_moduleName + ". See nested exception", e);
			}
		}
		else
		{
			ret = new ArrayList<T>(0);
		}

		return ret;
	}

	/**
	 * return a single enum value of type enumType
	 * 
	 * @param <T>
	 * @param enumType
	 * @param key
	 * @return a single enum value of type enumType
	 * @throws ConfigurationException
	 */
	public <T extends Enum<T>> T getEnum(Class<T> enumType, String key)
			throws ConfigurationException
	{
		T ret = null;
		String value = get(key);
		try
		{
			ret = Enum.valueOf(enumType, value);
		} catch (IllegalArgumentException e)
		{
			throw new ConfigurationException("bad argument", e);
		} catch (NullPointerException e)
		{
			throw new ConfigurationException(
					"One of the arguments was illegal. See nested exception", e);
		}

		return ret;
	}
	
	
	/**
	 * Returns a map of key-value strings, for parameter of form
	 * "[key1,value1],[key2,value2],[key3,value3]..."
	 * 
	 * @param key the parameter key
	 * @return the dictionary as a LinkedHashMap
	 * @throws ConfigurationException any error
	 */
	public LinkedHashMap<String, String> getDictionary(String key) throws ConfigurationException
	{
		String value = get(key);
		try
		{
			DictionaryRegExp dictionaryRegExp = new DictionaryRegExp(value);
			dictionaryRegExp.extractDictionary();
			return dictionaryRegExp.getDictionary();
		}
		catch (DictionaryRegExpException e)
		{
			throw new ConfigurationException("Failed to extract the dictionary for parameter: "+key);
		}
	}
	

	/**
	 * @return the ConfigurationFile
	 */
	public ConfigurationFile getConfigurationFile()
	{
		return m_ref;
	}

	/**
	 * @param iModuleName
	 *            the requested module's name
	 * @return the ConfigurationParams for the given module in this object's
	 *         ConfigurationFile
	 * @throws ConfigurationException
	 *             if iModuleName doesn't exist in this map
	 */
	public ConfigurationParams getSisterModuleConfiguration(String iModuleName)
			throws ConfigurationException
	{
		return m_ref.getModuleConfiguration(iModuleName);
	}

	/**
	 * @return name of current module/submodule
	 */
	public String getModuleName()
	{
		return m_moduleName;
	}

	// ////////////////////////////////////////////////////////////// protected // section ///////////////////////////////////////////////////////

	/**
	 * return the file or dir matching the paramName, found in parentDir, and make sure it's a file/dir as expected
	 * 
	 * @param paramName
	 * @param parentDir
	 * @param getFileAndNotDir
	 * @return
	 * @throws ConfigurationException
	 */
	protected File getFileOrDirByParam(String paramName, File parentDir, boolean getFileAndNotDir) throws ConfigurationException
	{
		String fileName = get(paramName);
		if (fileName.trim().length()==0)
		{
			throw new ConfigurationException("Empty value to parameter "+paramName+", illegal for files or directories.");
		}
		
		// Asher 24-February-2011
		fileName = FileUtils.normalizeCygwinPathToWindowsPath(fileName);
		
		
		return getFileOrDirByName(fileName, parentDir, getFileAndNotDir, paramName);
	}

	/**
	 * return the file or dir matching the <b>fileName</b>, found in parentDir, and make sure it's a file/dir as expected
	 * @param fileName
	 * @param parentDir
	 * @param getFileAndNotDir
	 * @return
	 * @throws ConfigurationException 
	 */
	protected File getFileOrDirByName(String fileName, File parentDir, boolean getFileAndNotDir, String paramName) throws ConfigurationException
	{
		// instantiate file
		File fileOrDir = (new File(parentDir, fileName)).getAbsoluteFile();
		
		// Asher 16 Feb, 2011 - normalization for Windows / Unix
		fileOrDir = FileUtils.normalizeFileNameByOS(fileOrDir);

		// and check for bad file path
		checkFileExists(fileOrDir, paramName, parentDir);

		if (getFileAndNotDir && fileOrDir.isDirectory())
			throw new ConfigurationException(fileOrDir.toString()
					+ " is a directory, not a file. Found in file " + this.m_ref.getConfFile().getName() + " in module " + m_moduleName);
		if (!getFileAndNotDir && fileOrDir.isFile())
			throw new ConfigurationException(fileOrDir.toString()
					+ " is a file, not a directory. Found in file " + this.m_ref.getConfFile().getName() + " in module " + m_moduleName);

		return fileOrDir;
	}

	/**
	 * throw a ConfigurationException if the file doesn't exist
	 * 
	 * @param file
	 * @param paramName
	 * @param iDir
	 * @throws ConfigurationException
	 *             if file doesn't exist
	 */
	protected void checkFileExists(File file, String paramName, File iDir)
			throws ConfigurationException
	{
		if (!file.exists())
			throw new ConfigurationException(
					"File doesn't exist, for parameter "
							+ paramName
							+ ": '"
							+ file.getAbsolutePath()
							+ "'"
							+ ((iDir != null) ? " in directory '" + iDir + "'"
									: "") + " in module '" + getModuleName()
							+ "'");
	}

	/**
	 * Retrieve the parameter's hash value, distinguish the value's type, and
	 * check for correctness
	 * 
	 * @param paramName
	 * @param retClass
	 *            the type/class of the hashed value
	 * @return the key's hash value
	 * @throws ConfigurationException
	 *             if the parameter value is invalid
	 */
	protected Number checkNumericValue(String paramName, NumberClass retClass)
			throws ConfigurationException
	{
		Number ret;
		String strValue = get(paramName);
		if (strValue == null)
			// return null
			ret = null;
		else
			try
			{
				switch (retClass)
				{
				case INTEGER:
					ret = Integer.parseInt(strValue);
					break;
				case LONG:
					ret = Long.parseLong(strValue);
					break;
				case FLOAT:
					ret = Float.parseFloat(strValue);
					break;
				case DOUBLE:
					ret = Double.parseDouble(strValue);
					break;
				default:
					throw new ConfigurationException(
							"ConfigurationParams.checkNumericValue(): got an invalid parameter, retClass == "
									+ retClass + "Found in file " + this.m_ref.getConfFile().getName() + " in module " + m_moduleName);
				}
			} catch (NumberFormatException e)
			{
				throw new NumberFormatException("Illegal value: '" + strValue
						+ "' for " + retClass.toString().toLowerCase()
						+ " parameter '" + paramName + "' in module '"
						+ getModuleName() + "'");
			}

		return ret;
	}

	/**
	 * @param paramName
	 * @return a String Array of the values of paramName
	 * @throws ConfigurationException
	 */
	protected String[] getArrayValues(String paramName)
			throws ConfigurationException
	{

		String strList = get(paramName);

		return strList.split(COMMA);
	}

	/**
	 * A list of the numeric classes used here
	 */
	protected enum NumberClass
	{
		INTEGER, LONG, FLOAT, DOUBLE
	}

	protected static final String COMMA = ",";
	private static final long serialVersionUID = 7224479183627234556L;

	/**
	 * the ConfigurationFile
	 */
	protected ConfigurationFile m_ref = null;
	/**
	 * name of current module
	 */
	protected String m_moduleName = null;

	protected boolean expandingEnvironmentVariables = false;
}
