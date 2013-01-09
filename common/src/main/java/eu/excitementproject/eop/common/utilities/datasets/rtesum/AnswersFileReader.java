package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Map;
import java.util.Set;

/**
 * Reads an answers file which is an XML file.
 * Answers file is either gold standard file, or regular answers file, which
 * is a file in the format that should be submitted for the RTE6 main
 * and novelty task.
 * 
 * @author Asher Stern
 *
 */
public interface AnswersFileReader
{
	public void setXml(String xmlFileName) throws Rte6mainIOException;
	public void read() throws Rte6mainIOException;
	public void setGoldStandard(boolean goldStandard) throws Rte6mainIOException;
	
	/**
	 * Returns a map from topic-id to a map from hypothesis id to text sentences
	 * @return
	 * @throws Rte6mainIOException
	 */
	public Map<String, Map<String,HypothesisAnswer>> getGoldStandard() throws Rte6mainIOException;
	
	/**
	 * Returns a map from topic-id to a map from hypothesis-id to text sentences
	 * @return
	 * @throws Rte6mainIOException
	 */
	public Map<String,Map<String,Set<SentenceIdentifier>>> getAnswers() throws Rte6mainIOException;
}
