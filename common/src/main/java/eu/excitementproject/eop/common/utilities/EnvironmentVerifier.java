package eu.excitementproject.eop.common.utilities;

import java.io.File;
import java.util.HashSet;
import java.util.Set;



/**
 * Checks whether the given files / executables / dynamic link libraries
 * exist in the current OS configuration.
 * 
 * @author Asher Stern
 *
 */
public class EnvironmentVerifier
{
	////////////////////// PUBLIC PART ///////////////////////////

	//////////////// NESTED EXCEPTION CLASS ////////////////////
	public static class EnvironmentVerifierException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public EnvironmentVerifierException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public EnvironmentVerifierException(String message)
		{
			super(message);
		}
	}
	
	///////////////////////////////////////////////////////////
	
	
	////////////////// CONSTANTS ////////////////////////////
	public static final String ENVIRONMENT_VARIABLE_PATH_WINDOWS = "Path";
	public static final String ENVIRONMENT_VARIABLE_PATH_UNIX = "PATH";
	public static String ENVIRONMENT_VARIABLE_PATH = "PATH"; // default
	
	protected static final String CLEANED_UP_ERROR_MESSAGE = " Note: After one verification - an additional call to setXXX method is required.";
	
	public static final String PROPERTY_NAME_LIBRARY_PATH = "java.library.path";
	static
	{
		if (OS.isUnix())
			ENVIRONMENT_VARIABLE_PATH = ENVIRONMENT_VARIABLE_PATH_UNIX;
		else if (OS.isWindows())
			ENVIRONMENT_VARIABLE_PATH = ENVIRONMENT_VARIABLE_PATH_WINDOWS;
	}
	/////////////////////////////////////////////////////////
	
	
	
	/////////////////////// PUBLIC METHODS ////////////////////
	
	/**
	 * Call this method to set the items you would like
	 * to verify whether they exist in the system.
	 * 
	 * @param items the items to verify whether they exist or not.
	 */
	public void setItemsToVerify(Set<String> items)
	{
		this.itemsToVerify = items;
		this.dynLibsAlternatives = null;
	}
	
	public void setDynamicLibrariesAlternatives(Set<Set<String>> dynLibsAlternatives)
	{
		this.dynLibsAlternatives = dynLibsAlternatives;
		this.itemsToVerify = null;
	}
	
	/**
	 * Finds out whether the items given in {@link #setItemsToVerify(Set)}
	 * exist as executables in the system's PATH.
	 * @return true - if all the items exist.
	 * @throws EnvironmentVerifierException
	 */
	public boolean verifyExecutables() throws EnvironmentVerifierException
	{
		if (this.itemsToVerify==null)
			throw new EnvironmentVerifierException("setItemsToVerify must be called before calling this function"+cleanedUpErrorMessage);
		Set<String> actualItemsToVerify = new HashSet<String>();
		for (String item : this.itemsToVerify)
		{
			actualItemsToVerify.add(OS.programName(item));
		}
		this.itemsToVerify = actualItemsToVerify;
		String paths = System.getenv().get(ENVIRONMENT_VARIABLE_PATH);
		if (paths == null)
			throw new EnvironmentVerifierException("the environment variable: "+ENVIRONMENT_VARIABLE_PATH+" does not exist in your system!");
		return verifyInPath(paths);
		
	}
	
	/**
	 * Finds out whether the items given in {@link #setItemsToVerify(Set)}
	 * exist as dynamic link libraries in the system.
	 * @return true - if all the items exist.
	 * @throws EnvironmentVerifierException
	 */
	public boolean verifyDynamicLinkLibraries() throws EnvironmentVerifierException
	{
		if ( (this.itemsToVerify==null) && (this.dynLibsAlternatives ==null) )
			throw new EnvironmentVerifierException("setItemsToVerify or setDynamicLibrariesAlternatives must be called before calling this function"+cleanedUpErrorMessage);
		if (this.dynLibsAlternatives!=null)
			return verifyDynamicLinkLibrariesAlternatives();
		else
		{
			try
			{
				Set<String> actualItemsToVerify = new HashSet<String>();
				for (String item : this.itemsToVerify)
				{
					actualItemsToVerify.add(OS.getDynamicLinkLibraryName(item));
				}
				this.itemsToVerify = actualItemsToVerify;
			}
			catch(Exception e)
			{
				throw new EnvironmentVerifierException("Couldn't verify dynamic libraries.",e);
			}
			String paths = System.getProperty(PROPERTY_NAME_LIBRARY_PATH);
			return verifyInPath(paths);
		}
	}
	

	/**
	 * Finds out whether the items given in {@link #setItemsToVerify(Set)}
	 * exist as files in the paths given
	 * @param paths a list of directories, separated by the regular
	 * system's path separator (e.g. ";" on Windows, ":" on Unix).
	 * @return true - if all the items exist.
	 * @throws EnvironmentVerifierException
	 */
	public boolean verifyInPath(String paths) throws EnvironmentVerifierException
	{
		if ( (this.itemsToVerify==null) && (this.dynLibsAlternatives==null) )
			throw new EnvironmentVerifierException("setItemsToVerify must be called before calling this function"+cleanedUpErrorMessage);
		String[] pathsAsArray = paths.split(File.pathSeparator);
		HashSet<String> pathsAsSet = new HashSet<String>();
		for (String pathItem : pathsAsArray)
		{
			pathsAsSet.add(pathItem);
		}
		return verifyInPath(pathsAsSet);
	}

	/**
	 * Finds out whether the items given in {@link #setItemsToVerify(Set)}
	 * exist as files in the paths given
	 * @param paths a set of directories
	 * @return true - if all the items exist.
	 * @throws EnvironmentVerifierException
	 */
	public boolean verifyInPath(Set<String> paths) throws EnvironmentVerifierException
	{
		if ( (this.itemsToVerify==null) && (this.dynLibsAlternatives==null) )
			throw new EnvironmentVerifierException("setItemsToVerify must be called before calling this function"+cleanedUpErrorMessage);
		
		boolean ret = true;
		if (this.dynLibsAlternatives!=null)
			ret = verifyAlternativesInPath(paths);
		else
		{
			this.missingItems = new HashSet<String>();
			for (String filename : this.itemsToVerify)
			{
				boolean found = false;
				for (String path : paths)
				{
					File file = new File(path,filename);
					if (file.exists())
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					ret = false;
					this.missingItems.add(filename);
				}
			}
		}
		
		cleanUp();
		return ret;
	}
	
	
	/**
	 * returns <code> null </code> if any of the <code> verify </code>
	 * methods was not called yet, or if all of the files were found
	 * by the last call to one of the <code> verify </code> methods.
	 * <BR>
	 * If in the last call to <code> verify </code> method there were
	 * missing items - they are returned by this method.
	 * @return the missing items - if there were missing items.
	 */
	public Set<String> getMissingItems()
	{
		return this.missingItems;
		
	}
	
	
	
	
	//////////////////// PROTECTED & PRIVATE PART /////////////////////
	
	protected void cleanUp()
	{
		this.dynLibsAlternatives = null;
		this.itemsToVerify = null;
		cleanedUpErrorMessage = CLEANED_UP_ERROR_MESSAGE;
	}
	
	protected boolean verifyAlternativesInPath(Set<String> paths) throws EnvironmentVerifierException
	{
		if (this.dynLibsAlternatives==null) throw new EnvironmentVerifierException("bug: this.dynLibsAlternatives==null");
		boolean ret = true;
		this.missingItems = new HashSet<String>();
		
		for (Set<String> alternativeSet : this.dynLibsAlternatives)
		{
			boolean found = false;
			for (String dynLibAlternative : alternativeSet)
			{
				for (String path : paths)
				{
					File dynLibFile = new File(path,dynLibAlternative);
					if (dynLibFile.exists())
					{
						found = true;
						break;
					}
				}
				if (found)
					break;
			}
			if (!found)
			{
				ret = false;
				StringBuffer sbDynLibsNotFound = new StringBuffer();
				boolean firstIteration = true;
				for (String dynLibAlternative : alternativeSet)
				{
					if (firstIteration) firstIteration = false;
					else sbDynLibsNotFound.append(" / ");
					sbDynLibsNotFound.append(dynLibAlternative);
				}
				this.missingItems.add(sbDynLibsNotFound.toString());
			}
		}
		return ret;
	}
	
	
	protected boolean verifyDynamicLinkLibrariesAlternatives() throws EnvironmentVerifierException, EnvironmentVerifierException
	{
		if (this.dynLibsAlternatives==null)
			throw new EnvironmentVerifierException("bug");
		try
		{
			Set<Set<String>> actualDynLibsAlternatives = new HashSet<Set<String>>();
			for (Set<String> alternativeSet : this.dynLibsAlternatives)
			{
				HashSet<String> actualAlternativeSet = new HashSet<String>();
				for (String libName : alternativeSet)
				{
					actualAlternativeSet.add(OS.getDynamicLinkLibraryName(libName));
				}
				actualDynLibsAlternatives.add(actualAlternativeSet);
			}
			this.dynLibsAlternatives = actualDynLibsAlternatives;
			String paths = System.getProperty(PROPERTY_NAME_LIBRARY_PATH);
			return verifyInPath(paths);
		}
		catch(Exception e)
		{
			throw new EnvironmentVerifierException("Couldn't verify libraries due to nested exception ",e);
		}
	}


	
	
	protected Set<String> itemsToVerify = null;
	protected Set<Set<String>> dynLibsAlternatives;
	protected HashSet<String> missingItems;
	protected String cleanedUpErrorMessage = "";

}
