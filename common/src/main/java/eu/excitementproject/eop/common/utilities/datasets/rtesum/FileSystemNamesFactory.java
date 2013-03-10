/**
 * 
 */
package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;

import eu.excitementproject.eop.common.utilities.datasets.rtesum.rte7main.DefaultRte7MainFileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.rte7main.DefaultRte7NoveltyFileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.rte7main.TestsetRte7MainFileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.rte7main.TestsetRte7NoveltyFileSystemNames;


/**
 * @author Amnon Lotan. Modifier by Asher Stern.
 * @since 2011
 * 
 */
public class FileSystemNamesFactory {
	
	public static final String RTE6_FLAG = "RTE6";
	public static final String RTE7_FLAG = "RTE7";
	public static final String DEV_FLAG = "DEV";
	public static final String TEST_FLAG = "TEST";
	
	

	/**
	 * Use default Main Task flag
	 * @param datasetDir
	 * @param annualFlag either {@value #RTE6_FLAG} or {@value #RTE7_FLAG}
	 * @param devTestFlag either {@value #DEV_FLAG} or {@value #TEST_FLAG}
	 * @return
	 */
	public static FilteredCandidatesRte6FileSystemNames chooseFilteredFileSystemNames(String annualFlag, String devTestFlag, File datasetDir)
	{
		return chooseFilteredFileSystemNames(annualFlag, devTestFlag, datasetDir, false);	
	}
	
	/**
	 * Use default Main Task flag
	 * @param datasetDir
	 * @param annualFlag either {@value #RTE6_FLAG} or {@value #RTE7_FLAG}
	 * @param devTestFlag either {@value #DEV_FLAG} or {@value #TEST_FLAG}
	 * @param isNoveltyTask
	 * @return
	 */
	public static Rte6FileSystemNames chooseUnfilteredFileSystemNames(String annualFlag, String devTestFlag, File datasetDir)
	{
		return chooseUnfilteredFileSystemNames(annualFlag, devTestFlag, datasetDir, false);
	}
	
	
	/**
	 * 
	 * @param datasetDir
	 * @param isNoveltyTask
	 * @param annualFlag either {@value #RTE6_FLAG} or {@value #RTE7_FLAG}
	 * @param devTestFlag either {@value #DEV_FLAG} or {@value #TEST_FLAG}
	 * @return
	 */
	public static Rte6FileSystemNames chooseUnfilteredFileSystemNames(String annualFlag, String devTestFlag, File datasetDir, boolean isNoveltyTask)
	{
		// boolean itIsRte7 = DefaultRte7MainFileSystemNames.isRte7(datasetDir.getAbsolutePath());
		// boolean isDevSet = DefaultRte7MainFileSystemNames.isDevSet(datasetDir);
		Boolean itIsRte7 = null;
		if (annualFlag.equals(RTE6_FLAG))
		{
			itIsRte7=false;
		}
		else if (annualFlag.equals(RTE7_FLAG))
		{
			itIsRte7=true;
		}
		else
		{
			itIsRte7=null;
		}
		Boolean isDevSet = null;
		if (devTestFlag.equals(DEV_FLAG))
		{
			isDevSet=true;
		}
		else if (devTestFlag.equals(TEST_FLAG))
		{
			isDevSet=false;
		}
		else
		{
			isDevSet=null;
		}
		Rte6FileSystemNames fileSystemNames = null;
		if ( (itIsRte7!=null) && (isDevSet!=null) )
		{
			if (isNoveltyTask)
			{
				if (itIsRte7)
				{
					if (isDevSet)
					{
						fileSystemNames = new DefaultRte7NoveltyFileSystemNames(); 
					}
					else // test set
					{
						fileSystemNames = new TestsetRte7NoveltyFileSystemNames();
					}
				}
				else // rte-6
				{
					if (isDevSet)
					{
						fileSystemNames = new DefaultRte6NoveltyFileSystemNames(); 
					}
					else // test set
					{
						fileSystemNames = new TestsetRte6NoveltyFileSystemNames();
					}
				}
			}
			else // main task
			{
				if (itIsRte7)
				{
					if (isDevSet)
					{
						fileSystemNames = new DefaultRte7MainFileSystemNames(); 
					}
					else
					{
						fileSystemNames = new TestsetRte7MainFileSystemNames();
					}
				}
				else // rte-6
				{
					if (isDevSet)
					{
						fileSystemNames = new DefaultRte6MainFileSystemNames(); 
					}
					else
					{
						fileSystemNames = new TestsetRte6MainFileSystemNames();
					}
				}
			}
		}

		return fileSystemNames;
	}
	
	/**
	 * 
	 * @param datasetDir
	 * @param isNoveltyTask
	 * @param annualFlag either {@value #RTE6_FLAG} or {@value #RTE7_FLAG}
	 * @param devTestFlag either {@value #DEV_FLAG} or {@value #TEST_FLAG}
	 * @return
	 */
	public static FilteredCandidatesRte6FileSystemNames chooseFilteredFileSystemNames(String annualFlag, String devTestFlag, File datasetDir, boolean isNoveltyTask)
	{
		Rte6FileSystemNames unfiltered = chooseUnfilteredFileSystemNames(annualFlag, devTestFlag, datasetDir, isNoveltyTask);
		if (unfiltered!=null)
		{
			return new FilteredCandidatesRte6FileSystemNames(unfiltered);	// filtered candidates files
		}
		else
		{
			return null;
		}
	}
}
