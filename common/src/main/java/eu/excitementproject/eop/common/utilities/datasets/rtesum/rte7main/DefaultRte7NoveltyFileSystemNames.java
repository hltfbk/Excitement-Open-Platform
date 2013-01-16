package eu.excitementproject.eop.common.utilities.datasets.rtesum.rte7main;

import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6FileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6MainConstants;

public class DefaultRte7NoveltyFileSystemNames extends DefaultRte7MainFileSystemNames implements Rte6FileSystemNames
{
	@Override
	public String getGoldStandardFileName()
	{
		return Rte7MainConstants.NOVELTY_GOLD_STANDARD_FILE_NAME;
	}
	
	@Override
	public String getTaskDirectoryName()
	{
		return Rte6MainConstants.NOVELTY_TASK_DIRECTORY_NAME;
	}
	
	@Override
	public String getHypothesisFileName()
	{
		return Rte6MainConstants.NOVELTY_TASK_HYPOTHESIS_FILE_NAME;
	}
	
	@Override
	public String getEvaluationPairsFileName()
	{
		return Rte6MainConstants.NOVELTY_TASK_EVALUATION_PAIRS_FILE_NAME;
	}
}
