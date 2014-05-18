package eu.excitementproject.eop.common.utilities;

import java.util.*;


/**
 * Services related to Operating system
 * <P>
 * Two services are supplied:
 * <ol>
 * <li>
 * Getting the OS type (Windows, Unix, Linux, Mac)
 * </li>
 * <li>
 * Getting a program name by supplying OS independent program name
 * (The meaning is adding ".exe" for windows).
 * </li>
 * </ol>
 * 
 * @author Asher Stern
 *
 */
public class OS
{
	
	////////////////////// public part //////////////////////////
	
	
	public static boolean isWindows()
	{
		return windows;
	}
	
	public static boolean isUnix()
	{
		return unix;
	}

	public static boolean isLinux()
	{
		return linux;
	}
	
	public static boolean isMac()
	{
		return mac;
	}
	
	
	/**
	 * Returns the actual program name.
	 * The meaning is appending ".exe" for windows.
	 * @param baseProgramName the program name, OS independent form.
	 * @return the actual program name (i.e. appending ".exe" for windows). 
	 */
	public static String programName(String baseProgramName)
	{
		if (isWindows())
			return baseProgramName+WINDOWS_PROGRAM_NAME_EXTENSION;
		else return baseProgramName;
		
	}
	
	
	/**
	 * Gets a dynamic link library name, and returns it as
	 * it should exist in the operating system file system.
	 * <BR>
	 * For example: dynamic library "xx", is "xx.dll" under Windows,
	 * and it is "libxx.so" under Unix.
	 * 
	 * @param libName
	 * @return
	 * @throws OsNotSupportedException
	 */
	public static String getDynamicLinkLibraryName(String libName)
	{
		return System.mapLibraryName(libName);
	}
	
	
	
	
	
	
	//////////////////private part  ////////////////////////////
	
	// constants
	private static final String WINDOWS_PROGRAM_NAME_EXTENSION = ".exe";
	
	// fields
	private static HashSet<String> windowsStartString;
	private static HashSet<String> unixStartString;
	private static HashSet<String> linuxStartString;
	private static HashSet<String> macStartString;
	private static boolean windows = false;
	private static boolean unix = false;
	private static boolean linux = false;
	private static boolean mac = false;

	// static initialization
	static
	{
		windowsStartString = new HashSet<String>();
		windowsStartString.add("Windows");
		
		unixStartString = new HashSet<String>();
		unixStartString.add("Linux");
		unixStartString.add("Solaris");
		unixStartString.add("SunOS");
		unixStartString.add("HP-UX");
		unixStartString.add("AIX");
		unixStartString.add("FreeBSD");
		unixStartString.add("Irix");
		unixStartString.add("Digital Unix");
		
		linuxStartString = new HashSet<String>();
		linuxStartString.add("Linux");
		
		macStartString = new HashSet<String>();
		macStartString.add("Mac OS");
		
		
		
		String osName = System.getProperty("os.name");
		
		for (String name : windowsStartString)
		{
			if (osName.toLowerCase().startsWith(name.toLowerCase()))
				windows = true;
		}

		for (String name : unixStartString)
		{
			if (osName.toLowerCase().startsWith(name.toLowerCase()))
				unix = true;
		}

		for (String name : linuxStartString)
		{
			if (osName.toLowerCase().startsWith(name.toLowerCase()))
				linux = true;
		}


		for (String name : macStartString)
		{
			if (osName.toLowerCase().startsWith(name.toLowerCase()))
				mac = true;
		}

	}
	


}
