package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Map;
import java.util.Set;

public interface EvaluationPairsWriter
{
	public void setXml(String xmlFileName) throws Rte6mainIOException;
	public void setTopicId(String topicId)  throws Rte6mainIOException;
	public void setCandidates(Map<String, Set<SentenceIdentifier>> candidates) throws Rte6mainIOException;
	public void write() throws Rte6mainIOException;
}
