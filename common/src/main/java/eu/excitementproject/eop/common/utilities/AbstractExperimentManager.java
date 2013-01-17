package eu.excitementproject.eop.common.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.Vector;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.file.FileUtils.CopyDirectoryException;


/**
 * Copies files of an experiment to a given destination.
 * Typically, used by using the class {@link ExperimentManager}.
 * <BR>
 * Usage:
 * <OL>
 * <LI>Subsequently call {@link #register(File)} for each file and directory you want to copy</LI>
 * <LI>Eventually, call {@link #save()}</LI>
 * </OL>
 * The files are copied only if the destination directory can be resolved.
 * If not - it is <B>not</B> an error. It's OK. It only means that the {@link #save()} will
 * have no effect.
 * <P>
 * Destination directory is resolved as follows:
 * First, if a configuration file was specified, and it contains the module
 * {@value #CONFIGURATION_MODULE_NAME}, and this module contains
 * the parameter {@value #CONFIGURATION_PARAMETER_NAME_BASE_DIRECTORY}, then its value
 * is used as the base-directory for the experiment directory.
 * If the base directory was not retrieved from configuration file, then an environment
 * variable {@value #EXPERIMENTS_ENVIRONMENT_VARIABLE_NAME} is checked for existence. If it
 * exists, then its value is used as the base directory.
 * <BR>
 * Now, if the base directory was resolved, then the experiment directory is
 * base_directory/i, where "i" is a number starting from 1, such that "base_directory/i" does
 * not exist.
 * <P>
 * In addition to copying files and directories, a read-me file is created. It contains the
 * time in which the experiment ended. Optionally, you can call the methods {@link #start()},
 * {@link #addMessage(String)}, and {@link #setConfigurationFile(ConfigurationFile)}, to
 * add more information to the read-me file.
 * <P>
 * Note that public methods do not and should not throw any exception.
 * This is due to the concept that experiment-manager is an optional component, nice to have,
 * but should not affect the system.
 * If the user does want to know if saving the experiment has succeeded, the user has to
 * check the return value of the {@link #save()} method. To get the exception itself (if
 * {@link #save()} indeed failed), the user has to call {@link #getException()}.
 * 
 * 
 * @author Asher Stern
 * @since Dec 10, 2011
 * 
 * @see ExperimentManager
 *
 */
public abstract class AbstractExperimentManager
{
	public static final String EXPERIMENTS_ENVIRONMENT_VARIABLE_NAME = "BIU_EXPERIMENTS";
	public static final String CONFIGURATION_MODULE_NAME = "experiment";
	public static final String CONFIGURATION_PARAMETER_NAME_BASE_DIRECTORY = "experiments_base_directory";
	public static final String README_FILE_NAME = "readme.txt";
	public static final String OWNERSHIP_FILE_NAME = "__EXPERIMENT_MANAGER_OWNERSHIP__";
	public static final int LIMIT_OWNERSHIP_ATTEMPTS = 10;
	
	/**
	 * Add file or directory to copy when {@link #save()} is called.
	 * @param file
	 */
	public void register(File file)
	{
		if (file!=null)
		{
			registeredFiles.add(file);
		}
	}
	
	/**
	 * Add a string message to the read-me file
	 * @param message
	 */
	public void addMessage(String message)
	{
		if (null==message) message="(null)";
		messages.add(message);
	}
	
	
	public void setConfigurationFile(String configurationFileName)
	{
		try
		{
			setConfigurationFile(new File(configurationFileName));
		}
		catch(Exception e)
		{
			this.exception = e;
		}
	}
	
	public void setConfigurationFile(File configurationFile)
	{
		try
		{
			setConfigurationFile(new ConfigurationFile(configurationFile));
		}
		catch(Exception e)
		{
			this.exception = e;
		}
	}
	
	/**
	 * Sets configuration file. It will be used to retrieve the experiment base directory
	 * (but if it does not specify a base directory, then the value
	 * of {@value #EXPERIMENTS_ENVIRONMENT_VARIABLE_NAME} will be used).
	 * It is also used to add messages to the read-me file - the added messages are
	 * the contents of the module {@value #CONFIGURATION_MODULE_NAME}.
	 * @param configurationFile
	 */
	public void setConfigurationFile(ConfigurationFile configurationFile)
	{
		this.configurationFile = configurationFile;
	}
	
	/**
	 * Optionally call this method to add start-time to the read-me file.
	 */
	public void start()
	{
		startDate = new Date();
	}
	
	/**
	 * Creates the experiment directory, copies the registered files and directories into it,
	 * and writes the read-me file.
	 * If the experiment base directory was not defined - then it is OK, the function will
	 * have no effect, and will return true.
	 * <P>
	 * <B>THIS METHOD SHOULD NOT THROW ANY EXCEPTION</B>
	 *  
	 * @return <tt>true</tt> if succeeded, <tt>false</tt> if failed.
	 */
	public boolean save()
	{
		boolean succeeded = true;
		if (this.exception!=null)
			succeeded = false;
		else
		{
			try
			{
				if (saveWasCalled)
				{
					throw new Exception("save() was already called.");
				}
				saveWasCalled = true;

				File destinationBaseDirectory = getDestinationBaseDirectory();
				if (destinationBaseDirectory!=null)
				{
					File experimentDirectory = getExperimentDirectory(destinationBaseDirectory);


					String readmeFileContents = getReadmeFileContents();
					if (this.configurationFile!=null)
					{
						register(this.configurationFile.getConfFile());
					}
					copyRegisteredFiles(experimentDirectory);
					writeReadmeFile(experimentDirectory, readmeFileContents);

					try{new File(experimentDirectory,OWNERSHIP_FILE_NAME).delete();}
					catch(Exception e){}

					this.saved = true;
				}
			}
			catch(Exception e)
			{
				succeeded = false;
				this.exception = e;
			}
		}
		return succeeded;
	}
	
	/**
	 * If {@link #save()} returned <tt>false</tt>, then the exception caused that failure will
	 * be returned. Otherwise null will be returned.
	 * 
	 * @return the exception caused {@link #save()} to fail, or null if no failure occurred.
	 */
	public Exception getException()
	{
		return exception;
	}
	
	/**
	 * Returns <tt>true</tt> if the {@link #save()} succeeded and indeed copied the files and the
	 * directories, and created the read-me file.
	 * @return
	 */
	public boolean isSaved()
	{
		return saved;
	}

	///////////////////// PROTECTED AND PRIVATE ////////////////////////////
	
	protected AbstractExperimentManager(){}
	
	protected String getReadmeFileContents() throws ConfigurationException
	{
		StringBuffer readmeBuffer = new StringBuffer();
		if (startDate!=null)
		{
			readmeBuffer.append("Started: ").append(startDate.toString()).append("\n");
		}
		readmeBuffer.append("Ended: ").append(new Date().toString()).append("\n");
		for (String message : messages)
		{
			readmeBuffer.append(message).append("\n");
		}
		ConfigurationParams params = getParams();
		if (params != null)
		{
			for (String key : params.keySet())
			{
				readmeBuffer.append(key).append(": ").append(params.get(key)).append("\n");
			}
		}
		
		return readmeBuffer.toString();
	}
	
	protected void writeReadmeFile(File experimentDirectory, String readmeFileContents) throws FileNotFoundException
	{
		File readmeFile = new File(experimentDirectory,README_FILE_NAME);
		if (readmeFile.exists())
		{
			readmeFile = new File(experimentDirectory,README_FILE_NAME+"_"+UUID.randomUUID().toString());
		}
		PrintWriter writer = new PrintWriter(readmeFile);
		try
		{
			writer.println(readmeFileContents);
		}
		finally
		{
			writer.close();
		}
		
	}
	
	protected void copyRegisteredFiles(File experimentDirectory) throws IOException, InterruptedException, CopyDirectoryException
	{
		for (File registeredFile : registeredFiles)
		{
			if (registeredFile.exists())
			{
				if (registeredFile.isFile())
				{
					FileUtils.copyFile(registeredFile, new File(experimentDirectory,registeredFile.getName()));
				}
				else if (registeredFile.isDirectory())
				{
					File registeredFileDestination = new File(experimentDirectory,registeredFile.getName());
					if (!registeredFileDestination.mkdir()) throw new IOException("Could not create directory: "+registeredFileDestination.getPath()+" (reason unknown).");
					FileUtils.copyDirectory(registeredFile, registeredFileDestination, false);
				}
			}
		}
	}
	
	protected File getExperimentDirectory(File baseDirectory) throws IOException
	{
		synchronized(AbstractExperimentManager.class)
		{
			int index = 0;
			File experimentDirectory = null;
			int numberOfAttempts = 0;
			boolean owner = false;
			do
			{
				do
				{
					index++;
					experimentDirectory = new File(baseDirectory,Integer.toString(index));
				}while (experimentDirectory.exists());

				owner = takeOwnership(experimentDirectory);
				numberOfAttempts++;

			}while((numberOfAttempts<LIMIT_OWNERSHIP_ATTEMPTS) && (!owner));

			if (!owner) throw new IOException("Could not take ownership");
			return experimentDirectory;
		}
	}
	
	protected boolean takeOwnership(File directory)
	{
		try
		{
			directory.mkdir();
			if ( (!directory.exists()) || (!directory.isDirectory()) ) throw new IOException("Directory \""+directory.getPath()+"\" could not be created.");
			File ownershipFile = new File(directory,OWNERSHIP_FILE_NAME);
			String uniqueString = UUID.randomUUID().toString();
			PrintStream uniqueFileStream = new PrintStream(new FileOutputStream(ownershipFile, true));
			try
			{
				uniqueFileStream.println(uniqueString);
			}
			finally
			{
				uniqueFileStream.close();
			}
			String retrievedLine = null;
			BufferedReader uniqeFileReader = new BufferedReader(new FileReader(ownershipFile));
			try
			{
				retrievedLine = uniqeFileReader.readLine();
			}
			finally
			{
				uniqeFileReader.close();
			}
			boolean owner = uniqueString.equals(retrievedLine);

			return owner;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	
	
	protected ConfigurationParams getParams() throws ConfigurationException
	{
		ConfigurationParams params = null;
		if (configurationFile!=null)
		{
			if (configurationFile.isModuleExist(CONFIGURATION_MODULE_NAME))
			{
				params = configurationFile.getModuleConfiguration(CONFIGURATION_MODULE_NAME);
				params.setExpandingEnvironmentVariables(true);
			}
		}
		return params;
	}
	
	protected File getDestinationBaseDirectory() throws ConfigurationException
	{
		File destinationDirectory = null;
		ConfigurationParams params = getParams();
		if (params != null)
		{
			if (params.containsKey(CONFIGURATION_PARAMETER_NAME_BASE_DIRECTORY))
			{
				destinationDirectory = params.getFile(CONFIGURATION_PARAMETER_NAME_BASE_DIRECTORY);
			}
		}
				
		if (null == destinationDirectory)
		{
			String experimentsVarValue = System.getenv(EXPERIMENTS_ENVIRONMENT_VARIABLE_NAME);
			if (experimentsVarValue!=null)
			{
				destinationDirectory = new File(experimentsVarValue);
			}
		}
		return destinationDirectory;
	}
	
	
	
	
	private LinkedHashSet<File> registeredFiles = new LinkedHashSet<File>();
	private Vector<String> messages = new Vector<String>();
	private Date startDate = null;
	private ConfigurationFile configurationFile = null;
	private boolean saveWasCalled = false;
	private boolean saved = false;
	private Exception exception = null;
}
