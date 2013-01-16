package eu.excitementproject.eop.common.utilities.datasets.rtesum;

public class FilteredCandidatesRte6FileSystemNames implements
		Rte6FileSystemNames
{
	public static final String FILTERED_CANDIDATES_FILE_PREFIX = "filtered_";

	public FilteredCandidatesRte6FileSystemNames(Rte6FileSystemNames original)
	{
		this.original = original;
	}

	public String getTopicDirectoryNamePrefix()
	{
		return original.getTopicDirectoryNamePrefix();
	}

	public String getCorpusDirectoryName()
	{
		return original.getCorpusDirectoryName();
	}

	public String getCorpusFileNamePostfix()
	{
		return original.getCorpusFileNamePostfix();
	}

	public String getTaskDirectoryName()
	{
		return original.getTaskDirectoryName();
	}

	public String getHypothesisFileName()
	{
		return original.getHypothesisFileName();
	}

	public String getEvaluationPairsFileName()
	{
		return FILTERED_CANDIDATES_FILE_PREFIX+original.getEvaluationPairsFileName();
	}

	public String getAnswersFileExtension()
	{
		return original.getAnswersFileExtension();
	}

	public String getGoldStandardFileName()
	{
		return original.getGoldStandardFileName();
	}

	protected Rte6FileSystemNames original;
}
