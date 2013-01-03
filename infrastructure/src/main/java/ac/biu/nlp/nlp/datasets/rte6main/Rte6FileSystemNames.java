package ac.biu.nlp.nlp.datasets.rte6main;


/**
 * Gives the names of directories and files of the data-set.
 * 
 * @author Asher Stern
 * @since Aug 16, 2010
 * 
 * @see Rte6MainConstants
 *
 */
public interface Rte6FileSystemNames
{
	public String getTopicDirectoryNamePrefix();
	public String getCorpusDirectoryName();
	public String getCorpusFileNamePostfix();
	public String getTaskDirectoryName();
	public String getHypothesisFileName();
	public String getEvaluationPairsFileName();
	public String getAnswersFileExtension();
	public String getGoldStandardFileName();
}
