package eu.excitementproject.eop.common.utilities.sort;

import java.io.File;
import java.io.IOException;

/**
 * This class wraps Nsort to sort large files
 * Nsort is a sort/merge program that can quickly sort large amounts of data, using large numbers of processors and disks in parallel.
 * see http://www.ordinal.com/
 * <p>
 * Note that NSort.exe can only be used on hosts with a special NSort license installed
 *  
 * <p>
 * Nsort accepts sort specifications in Windows, POSIX and Nsort modes.
	Windows sort usage: nsort /L[OCALE] C [/R] [/+n] [/M kilobytes]
  [/RE recordbytes] [[drive1:][path1]filename1]
  [/T [drive2:][path2]] [/O [drive3:][path3]filename3]
 flags:
  /L[OCALE] C                 This "option" is currently required for Nsort.
                              That is, the C locale is the only locale option
                              and must be explicitly specified in order to
                              specify a key with the Windows sort notation.
                              By default, the sort is case insensitive.
  /+n                         Indicates the character number, n, of the
                              beginning of the sort key in each record.
                              For instance, /+2 specifies that comparisons
                              should start on the 2nd character in each
                              line.  Comparisons start on first character
                              of each line by default.
  /M[EMORY] n                 Indicates the number of kilobytes of main
                              memory to use.  The minimum memory size is
                              8000 kilobytes.
  /REC[ORD_MAXIMUM] n         Indicates the maximum number of characters
                              per input line (default 4096, maximum 65535).
  /R[EVERSE]                  Reverses the sort order; i.e. Z to A and
                              9 to 0.
  [drive1:][path1]filename1   Specifies an input file to be sorted. There
                              can be multiple input files.  If none are
                              specified, the standard input is read.
  /T[EMPORARY]
    [drive2:][path2]          Indicates the path of a directory to hold the
                              Nsort temporary file.  For better temporary
                              file performance, multiple temporary file
                              paths can be specified on separate physical
                              disks.  The system temporary directory is
                              used if no temporary directory is specified.
  /O[UTPUT]
    [drive3:][path3]filename3 Specifies the file where the sorted input is
                              to be stored.  If not specified, the data is
                              written to the standard output.   Specifying
                              the output file is faster than redirecting
                              standard output to the same file.
 *
 * @author nlp legacy code
 */
public class Nsort implements DiskSort 
{
	/**
	 * Constructor
	 * @param nsortExeName
	 * @throws DiskSortIOException 
	 */
	public Nsort(String nsortExeName) throws DiskSortIOException
	{
		// allow for empty exec file, cos we have a default value
		if (nsortExeName != null)
			m_nsortExe = new File(nsortExeName);

		if (!m_nsortExe.exists())
			throw new DiskSortIOException("Bad given/default nsort executable: " + m_nsortExe);
	}	
	
	/**
	 * Constructor
	 * @throws DiskSortIOException 
	 */
	public Nsort() throws DiskSortIOException
	{
		this(null);
	}
	
	/**
	 * @param nsortExeName
	 * @throws DiskSortIOException
	 */
	public void setSortExe(String nsortExeName) throws DiskSortIOException
	{
		if (nsortExeName == null)
			throw new DiskSortIOException("Got null arg");
		
		m_nsortExe = new File(nsortExeName);

		if (!m_nsortExe.exists())
			throw new DiskSortIOException("Bad given nsort executable: " + nsortExeName);
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.sort.DiskSort#sort(java.io.File, java.io.File)
	 */
	public void sort(File iInFile, File iOutFile) throws DiskSortIOException
	{
		sort(iInFile, iOutFile, "");
	}
	
	/**
	 * Sort a file, output written to iOutFile
	 * @param iInFile
	 * @param iOutFile
	 * @param cmdParameters Any other NSort command line parameters. Note that this String goes unchecked to the execution
	 * @throws DiskSortIOException
	 */
	public void sort(File iInFile, File iOutFile, String cmdParameters) throws DiskSortIOException
	{
		if (cmdParameters == null) 
			cmdParameters = "";
		
		try
		{ 		
			// prepare the Program And Arguments
			String command[] = { 
					m_nsortExe.getAbsolutePath(), 
					iInFile.getCanonicalPath(),
					cmdParameters,
					LBL_OUT_FILE_SWITCH,
					iOutFile.getCanonicalPath()
				};

			// execute
			Process process = Runtime.getRuntime().exec(command, null, null);
			try
			{
				process.waitFor();
			} 
			catch (InterruptedException e) { }			
		} 
		catch (IOException e)
		{
			throw new DiskSortIOException("Error occured when executing the command for the files:\n" + iInFile + ", " + iOutFile + "\nand with parameters: " + cmdParameters + "\nSee nested.", e);		
		}
	}
	
	
	private File m_nsortExe = new File(DEFAULT_NSORT_FILE);

	private static final String LBL_OUT_FILE_SWITCH = "-o";
	
	private static final String DEFAULT_NSORT_FILE = "C:\\Program Files\\Nsort\\nsort.exe";
}
