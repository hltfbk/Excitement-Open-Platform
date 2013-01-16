package eu.excitementproject.eop.common.utilities.datasets.rtekbp;


/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class DefaultRteKbpFileSystemNames implements RteKbpFileSystemNames
{

	public String getMainDataDir()
	{
		return "data";
	}

	public String[] getAlternativesForPairFileName()
	{
		String[] ret = new String[1+1];
		int index=0;
		ret[index] = "RTE6_KBP_DEVSET.xml";
		index++;
		ret[index] = "RTE6_KBP_TESTSET.xml";
		
		return ret;
	}

	public String getSourceDataDir()
	{
		return "source_data";
	}

	public String getDocumentFilesExtension()
	{
		return ".sgm";
	}

}
