/**
 * 
 */
package ac.biu.nlp.nlp.datasets.rte6main;

import java.io.File;

import ac.biu.nlp.nlp.datasets.rte7main.DefaultRte7MainFileSystemNames;
import ac.biu.nlp.nlp.datasets.rte7main.DefaultRte7NoveltyFileSystemNames;
import ac.biu.nlp.nlp.datasets.rte7main.TestsetRte7MainFileSystemNames;
import ac.biu.nlp.nlp.datasets.rte7main.TestsetRte7NoveltyFileSystemNames;

/**
 * @author amnon
 * @since 5 áñôè 2011
 * 
 */
public class FileSystemNamesFactory {

	/**
	 * Use default Main Task flag
	 * @param datasetDir
	 * @return
	 */
	public static FilteredCandidatesRte6FileSystemNames chooseFilteredFileSystemNames(File datasetDir)
	{
		return chooseFilteredFileSystemNames(datasetDir, false);	
	}
	
	/**
	 * Use default Main Task flag
	 * @param datasetDir
	 * @param isNoveltyTask
	 * @return
	 */
	public static Rte6FileSystemNames chooseUnfilteredFileSystemNames(File datasetDir)
	{
		return chooseUnfilteredFileSystemNames(datasetDir, false);
	}
	
	
	/**
	 * 
	 * @param datasetDir
	 * @param isNoveltyTask
	 * @return
	 */
	public static Rte6FileSystemNames chooseUnfilteredFileSystemNames(File datasetDir, boolean isNoveltyTask)
	{
		boolean itIsRte7 = DefaultRte7MainFileSystemNames.isRte7(datasetDir.getAbsolutePath());
		boolean isDevSet = DefaultRte7MainFileSystemNames.isDevSet(datasetDir);
		Rte6FileSystemNames fileSystemNames = null;
		if (isNoveltyTask)
		{
//			logger.info("Working on Novelty Task");
			if (itIsRte7)
			{
//				logger.info("Working on RTE-7");
				if (isDevSet)
				{
//					logger.info("Working on Training folders");
					fileSystemNames = new DefaultRte7NoveltyFileSystemNames(); 
				}
				else
				{
//					logger.info("Working on Test folders");
					fileSystemNames = new TestsetRte7NoveltyFileSystemNames();
				}
			}
			else
			{
//				logger.info("Working on RTE-6");
				if (isDevSet)
				{
//					logger.info("Working on Training folders");
					fileSystemNames = new DefaultRte6NoveltyFileSystemNames(); 
				}
				else
				{
//					logger.info("Working on Test folders");
					fileSystemNames = new TestsetRte6NoveltyFileSystemNames();
				}
			}
		}
		else
		{
//			logger.info("Working on Main Task");
			if (itIsRte7)
			{
//				logger.info("Working on RTE-7");
				if (isDevSet)
				{
//					logger.info("Working on Training folders");
					fileSystemNames = new DefaultRte7MainFileSystemNames(); 
				}
				else
				{
//					logger.info("Working on Test folders");
					fileSystemNames = new TestsetRte7MainFileSystemNames();
				}
			}
			else
			{
//				logger.info("Working on RTE-6");
				if (isDevSet)
				{
//					logger.info("Working on Training folders");
					fileSystemNames = new DefaultRte6MainFileSystemNames(); 
				}
				else
				{
//					logger.info("Working on Test folders");
					fileSystemNames = new TestsetRte6MainFileSystemNames();
				}
			}
		}
		
		return fileSystemNames;
	}
	
	/**
	 * 
	 * @param datasetDir
	 * @param isNoveltyTask
	 * @return
	 */
	public static FilteredCandidatesRte6FileSystemNames chooseFilteredFileSystemNames(File datasetDir, boolean isNoveltyTask)
	{
		return new FilteredCandidatesRte6FileSystemNames(chooseUnfilteredFileSystemNames(datasetDir, isNoveltyTask));	// filtered candidates files
	}
}
