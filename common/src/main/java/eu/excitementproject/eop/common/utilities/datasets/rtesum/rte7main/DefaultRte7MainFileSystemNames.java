package eu.excitementproject.eop.common.utilities.datasets.rtesum.rte7main;

import java.io.File;

import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultRte6MainFileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6FileSystemNames;


/**
 * 
 * @author Amnon Lotan & Asher Stern
 * @since 2011
 *
 */
public class DefaultRte7MainFileSystemNames extends DefaultRte6MainFileSystemNames implements Rte6FileSystemNames
{
	protected static final String RTE7_LABEL = "7_";
	protected static final CharSequence DEV_LABEL = "DEV";

	public String getGoldStandardFileName()
	{
		return Rte7MainConstants.GOLD_STANDARD_FILE_NAME;
	}

	/**
	 * It's important to have the full path of the dataset dir, 'cos the bastards distribute the rte7test set as:
	 * <code>...\RTE7_TESTSET\data\</code> so the name "data" is not helpfull at all.
	 * @param datasetDirAbsolutePath
	 * @return
	 */
	public static boolean isRte7(String datasetDirAbsolutePath)
	{
		File datasetDirAsFile = new File(datasetDirAbsolutePath);
		boolean ret = false;
		if (datasetDirAsFile.getName().contains(DefaultRte7MainFileSystemNames.RTE7_LABEL))
		{
			ret = true;
		}
		else
		{
			File parent = datasetDirAsFile.getParentFile();
			if (parent!=null)
			{
				if (parent.getName().contains(DefaultRte7MainFileSystemNames.RTE7_LABEL))
					ret = true;
			}
		}
		return ret;
	}

	/**
	 * @param datasetDir
	 * @return
	 */
	public static boolean isDevSet(File datasetDir) {
		return datasetDir.getName().toUpperCase().contains(DEV_LABEL);
	}
	
}
