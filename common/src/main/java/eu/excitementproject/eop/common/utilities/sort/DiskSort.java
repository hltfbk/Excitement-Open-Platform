package eu.excitementproject.eop.common.utilities.sort;

import java.io.File;


/**
 * An interface that sorts files
 * @author NLP legacy code
 *
 */
public interface DiskSort 
{
	/**
	 * Sort a file, output written to iOutFile
	 * @param iInFile
	 * @param iOutFile
	 * @throws DiskSortException
	 */
	public void sort(File inFile, File outFile) throws DiskSortIOException;

	/**
	 * @param nsortExeName
	 * @throws DiskSortIOException
	 */
	public void setSortExe(String nsortExeName) throws DiskSortIOException;
}
