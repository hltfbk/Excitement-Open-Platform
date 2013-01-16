package eu.excitementproject.eop.common.utilities.datasets.rtesum;

public class DefaultRte6MainFileSystemNames implements Rte6FileSystemNames
{

	public String getTopicDirectoryNamePrefix()
	{
		return Rte6MainConstants.TOPIC_DIRECTORY_NAME_PREFIX;
	}

	public String getCorpusDirectoryName()
	{
		return Rte6MainConstants.CORPUS_DIRECTORY_NAME;
	}

	public String getCorpusFileNamePostfix()
	{
		return Rte6MainConstants.CORPUS_XML_FILE_NAME_POSTFIX;
	}

	public String getTaskDirectoryName()
	{
		return Rte6MainConstants.MAIN_TASK_DIRECTORY_NAME;
	}

	public String getHypothesisFileName()
	{
		return Rte6MainConstants.MAIN_TASK_HYPOTHESIS_FILE_NAME;
	}

	public String getEvaluationPairsFileName()
	{
		return Rte6MainConstants.MAIN_TASK_EVALUATION_PAIRS_FILE_NAME;
	}

	public String getAnswersFileExtension()
	{
		return Rte6MainConstants.ANSWER_FILE_EXTENSION;
	}

	public String getGoldStandardFileName()
	{
		return Rte6MainConstants.GOLD_STANDARD_FILE_NAME;
	}

}
