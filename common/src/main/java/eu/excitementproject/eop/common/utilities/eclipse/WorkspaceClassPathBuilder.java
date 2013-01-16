package eu.excitementproject.eop.common.utilities.eclipse;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.OS;


/**
 * An executable java class that prints a string that represents the class-path
 * for a given Eclipse workspace.
 * 
 * The string printed by this program is list of paths required
 * to compile and run the projects in this work-space, with variable "JARS" left-appended when
 * required.
 * The list's elements are separated by either ":" or ";" (or any other character) depending on
 * the OS in which it runs.
 * 
 * 
 * @author Asher Stern
 * 
 *
 */
public class WorkspaceClassPathBuilder
{
	public static final String CLASSPATH_FILE_NAME = ".classpath";
	public static final String DEFAULT_VARIABLE_NAME = "JARS";
	

	/**
	 * 
	 * @param args 1. the workspace path. [2. optional: variable name (e.g. JARS)] [3. replacement e.g. %JARS%]
	 */
	public static void main(String[] args)
	{
		try
		{
			int index=0;
			if (args.length<(index+1)) throw new ClassPathReaderException("args");
			String workspaceDirName = args[index];
			index++;
			
			String variableName = DEFAULT_VARIABLE_NAME;
			if (args.length>=(index+1))
			{
				variableName = args[index];
				index++;
			}
			String variableReplacement = null;
			if (args.length>=(index+1))
			{
				variableReplacement=args[index];
				index++;
			}
			else
			{
				if (OS.isWindows())
				{
					variableReplacement = "%"+variableName+"%";
				}
				else if (OS.isUnix())
				{
					variableReplacement = "\\$"+variableName;
				}
				else
					throw new ClassPathReaderException("don\'t know value for variableReplacement");
			}
			
			WorkspaceClassPathBuilder builder = new WorkspaceClassPathBuilder(workspaceDirName, variableName, variableReplacement,System.out);
			builder.read();
			String classPathString = builder.getClassPathString();
			System.out.println(classPathString);
			
			
			
			
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}
	
	
	

	
	
	public WorkspaceClassPathBuilder(String workspaceDirName,
			String variableName, String replaceVariableName)
	{
		this(workspaceDirName,variableName,replaceVariableName,null);
	}

	public WorkspaceClassPathBuilder(String workspaceDirName,
			String variableName, String replaceVariableName, PrintStream warningsStream)
	{
		super();
		this.workspaceDirName = workspaceDirName;
		this.variableName = variableName;
		this.replaceVariableName = replaceVariableName;
		this.warningsStream = warningsStream;
	}





	public void read() throws ClassPathReaderException
	{
		classPathEntries = new LinkedHashSet<String>();
		File workSpaceDir = new File(workspaceDirName);
		if (!workSpaceDir.exists()) throw new ClassPathReaderException("dir not exist: "+workSpaceDir.getPath());
		if (!workSpaceDir.isDirectory()) throw new ClassPathReaderException("dir not exist: "+workSpaceDir.getPath());
		
		File[] projectsDirs = workSpaceDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isDirectory()&&(!pathname.getName().equals(".metadata"));
			}
		});
		
		for (File projectDir : projectsDirs)
		{
			File classpathFile = new File(projectDir,CLASSPATH_FILE_NAME);
			if (!classpathFile.exists())
			{
				if (null==warningsStream)
					throw new ClassPathReaderException("could not locate classpath file: "+classpathFile.getPath());
				else
				{
					this.warningsStream.println("Directory "+projectDir+" has no class-path file");
				}
			}
			ClassPathReader reader = new ClassPathReader(classpathFile, variableName, replaceVariableName);
			reader.read();
			this.classPathEntries.addAll(reader.getClassPathEntries());
		}
	}
	
	public String getClassPathString() throws ClassPathReaderException
	{
		String separator = System.getProperty("path.separator");
		if (null==separator) throw new ClassPathReaderException("could not find path separator.");
		StringBuffer buffer = new StringBuffer();
		boolean firstIteration = true;
		for (String classPathEntry : classPathEntries)
		{
			if (firstIteration)firstIteration=false;
			else
				buffer.append(separator);
			if (OS.isWindows()) buffer.append("\"");
			buffer.append(classPathEntry);
			if (OS.isWindows()) buffer.append("\"");
		}
		return buffer.toString();
	}
	
	
	
	
	private String workspaceDirName;
	private Set<String> classPathEntries = null;
	private String variableName;
	private String replaceVariableName;
	private PrintStream warningsStream = null;
	
	


}
