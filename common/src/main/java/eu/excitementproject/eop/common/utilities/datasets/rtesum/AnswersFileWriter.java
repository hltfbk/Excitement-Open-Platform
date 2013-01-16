package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Map;
import java.util.Set;

/**
 * Writes an answer file for the RTE6 main task.
 * @author asher
 *
 */
public interface AnswersFileWriter
{
	public void setXml(String xmlFileName) throws Rte6mainIOException;
	
	/**
	 * Sets the answers to be written to the answers file.
	 * The argument is a map from topic id to its answer. Each answer for a topic id is a mapping
	 * from hypothesis id to a set of sentences that entail that hypothesis.
	 * 
	 * @param answers the answers (see above).
	 * @throws Rte6mainIOException
	 */
	public void setAnswers(Map<String,Map<String,Set<SentenceIdentifier>>> answers) throws Rte6mainIOException;
	
	public void setWriteTheEvaluationAttribute(boolean writeEvaluation);
	
	public void write() throws Rte6mainIOException;
}
