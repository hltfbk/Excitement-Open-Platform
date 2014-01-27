package eu.excitementproject.eop.common.utilities.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.utilities.DictionaryRegExp;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.DictionaryRegExp.DictionaryRegExpException;
//import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams.NumberClass;
import eu.excitementproject.eop.common.utilities.file.FileUtils;


/**
 * Implementation of {@link ConfigurationParams} which implements most of the methods, while
 * leaving the actual method for retrieving value for a given key not-implemented.
 * 
 * @author Asher Stern
 * @since Dec 18, 2013
 *
 */
public abstract class AbstractConfigurationParams implements ConfigurationParams
{
	public String get(String paramName) throws ConfigurationException
	{
		// check for null value
		if (paramName == null)
			throw new ConfigurationException("Got null paramName");

		String str = rawGet(paramName);

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
	
	
	public boolean isExpandingEnvironmentVariables()
	{
		return expandingEnvironmentVariables;
	}

	public void setExpandingEnvironmentVariables(boolean expandingEnvironmentVariables)
	{
		this.expandingEnvironmentVariables = expandingEnvironmentVariables;
	}
	
	
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

	public File getFile(String paramName) throws ConfigurationException
	{
		return getFileOrDirByParam(paramName, null, true);
	}

	public File getFile(String paramName, File iDir)
			throws ConfigurationException
	{
		return getFileOrDirByParam(paramName, iDir, true);
	}

	public File getDirectory(String paramName) throws ConfigurationException
	{
		return getFileOrDirByParam(paramName, null, false);
	}

	public long getLong(String paramName) throws ConfigurationException
	{
		return (Long) checkNumericValue(paramName, NumberClass.LONG);
	}

	
	public int getInt(String paramName) throws ConfigurationException
	{
		return (Integer) checkNumericValue(paramName, NumberClass.INTEGER);
	}

	
	public Double getDouble(String paramName) throws ConfigurationException
	{
		return (Double) checkNumericValue(paramName, NumberClass.DOUBLE);
	}

	public float getFloat(String paramName) throws ConfigurationException
	{
		return (Float) checkNumericValue(paramName, NumberClass.FLOAT);
	}

	public String getString(String paramName) throws ConfigurationException
	{
		return get(paramName);
	}

	
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
				throw new ConfigurationException("bad argument. Found in " + whoAmI_forException(), e);
			} catch (NullPointerException e)
			{
				throw new ConfigurationException(
						"One of the arguments was illegal. Found in " + whoAmI_forException() + ". See nested exception", e);
			}
		}
		else
		{
			ret = new ArrayList<T>(0);
		}

		return ret;
	}
	
	
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
	
	public Integer getInteger(String name) throws ConfigurationException
	{
		return getInt(name);
	}
	

	
	protected abstract String rawGet (String paramName) throws ConfigurationException;
	
	protected abstract String whoAmI_forException();
	
	
	
	
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
					+ " is a directory, not a file. Found in " + whoAmI_forException());
		if (!getFileAndNotDir && fileOrDir.isFile())
			throw new ConfigurationException(fileOrDir.toString()
					+ " is a file, not a directory. Found in " + whoAmI_forException());

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
									+ retClass + "Found in " + whoAmI_forException());
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
	
	protected boolean expandingEnvironmentVariables = false;
}
