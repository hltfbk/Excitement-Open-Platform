package eu.excitementproject.eop.common.utilities.datasets.rtesum;

public class DefaultRte6NoveltyFileSystemNames extends DefaultRte6MainFileSystemNames implements Rte6FileSystemNames
{
	@Override
	public String getGoldStandardFileName()
	{
		return Rte6MainConstants.NOVELTY_GOLD_STANDARD_FILE_NAME;
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
