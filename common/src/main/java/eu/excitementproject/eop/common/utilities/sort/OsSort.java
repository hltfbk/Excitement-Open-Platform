/**
 * 
 */
package eu.excitementproject.eop.common.utilities.sort;

import java.io.File;
import java.io.IOException;

import eu.excitementproject.eop.common.utilities.OS;


/**
 * Uses the builtin windows/unix/linux sort command line utility to sort files, depending on your platform.
 * <p>
 * setSortExe() does nothing.
 * 
 * @author Amnon Lotan
 * @since Jan 10, 2011
 * 
 */
public class OsSort implements DiskSort 
{
	/**
	 * 
	 */
	public OsSort()	{	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.sort.DiskSort#sort(java.io.File, java.io.File)
	 */
	public void sort(File inFile, File outFile) throws DiskSortIOException 
	{
		sort(inFile, outFile, false);
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @param deleteInFile if true, the in file is deleted
	 * @throws DiskSortIOException
	 */
	public void sort(File inFile, File outFile, boolean deleteInFile) throws DiskSortIOException
	{
		sortStatic(inFile, outFile, deleteInFile);
	}

	/**
	 * Same as sort(), only static
	 * @param inFile
	 * @param outFile
	 * @throws DiskSortIOException
	 */
	public static void sortStatic(File inFile, File outFile) throws DiskSortIOException 
	{
		sortStatic(inFile, outFile, false);
	} 
	
	/**
	 * Same as sort(), only static
	 * @param inFile
	 * @param outFile
	 * @param deleteInFile if true, the in file is deleted
	 * @throws DiskSortIOException
	 */
	public static void sortStatic(File inFile, File outFile, boolean deleteInFile) throws DiskSortIOException
	{
		sortStatic(inFile, outFile,deleteInFile,false);
	}

	/**
	 * Same as sort(), only static
	 * @param inFile
	 * @param outFile
	 * @param deleteInFile if true, the in file is deleted
	 * @param bNumeric indicates whether a numeric sort is required (true)
	 * @throws DiskSortIOException
	 */
	public static void sortStatic(File inFile, File outFile, boolean deleteInFile, boolean bNumeric) throws DiskSortIOException
	{
		if (inFile == null)
			throw new DiskSortIOException("no in file!");
		if (!inFile.exists())
			throw new DiskSortIOException(inFile + " doesn't exist!");
		
		if (bNumeric && !OS.isLinux() && !OS.isUnix())
			throw new DiskSortIOException("Numeric sort is not supported in ");

		Process process;
		try {
			process = Runtime.getRuntime().exec( new String[]{
					SORT_CMD, 
					inFile.getAbsolutePath(), 
					OS.isWindows() ? WIN_OUTPUT_SWITCH : UNIX_OUTPUT_SWITCH, 
					outFile.getAbsolutePath(),
					(bNumeric ? UNIX_NUMERICSORT_SWITCH : "")});
			
		} catch (IOException e1) {
			throw new DiskSortIOException("Error in windows-sorting " + inFile, e1);
		}
		try
		{
			process.waitFor();
		} 
		catch (InterruptedException e) { }	
		
		// delete inFile
		if (deleteInFile)
			inFile.delete();
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.sort.DiskSort#setSortExe(java.lang.String)
	 */
	public void setSortExe(String nsortExeName) throws DiskSortIOException {}

	private static final String SORT_CMD = "sort";
	private static final String WIN_OUTPUT_SWITCH = "/O";
	private static final String UNIX_OUTPUT_SWITCH = "-o";
	private static final String UNIX_NUMERICSORT_SWITCH = "-n";
}