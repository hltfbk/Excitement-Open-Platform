package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Map;
import java.util.Set;

/**
 * Reads evaluation-pairs file.
 * An evaluation-pairs file is a file that contains <B>candidates</B>
 * sentences for each hypothesis. A candidate sentence is a sentences
 * that "yes/no" entailment should be judged for.
 * <P>
 * Not all the sentences in the document ("corpus document") should be
 * annotated. Only a subset of the sentences, which are called "candidates"
 * should be judged by the systems.
 * @author Asher Stern
 *
 */
public interface EvaluationPairsReader
{
	public void setXml(String xmlFileName) throws Rte6mainIOException;
	public void read() throws Rte6mainIOException;
	public String getTopicId() throws Rte6mainIOException;
	public Map<String, Set<SentenceIdentifier>> getCandidateSentencesMap() throws Rte6mainIOException;
}

