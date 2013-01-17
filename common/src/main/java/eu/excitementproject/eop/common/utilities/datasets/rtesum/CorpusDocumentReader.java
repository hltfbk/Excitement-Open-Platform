package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.LinkedHashMap;

/**
 * Reads a corpus document file, with is an XML file that contains a
 * text.
 * <BR>
 * Each topic has 10 text files ("corpus document files"). The whole
 * data set contains 10 topics.
 * <BR>
 * The corpus, or text, file is an XML file that contains list of sentences
 * indexed by numbers 0,1,2,..., and contains also head-line and date-line.
 * 
 * @author Asher Stern
 *
 */
public interface CorpusDocumentReader
{
	public void setXml(String xmlFileName) throws Rte6mainIOException;
	public void read() throws Rte6mainIOException;
	public String getDocId() throws Rte6mainIOException;
	public String getType() throws Rte6mainIOException;
	public String getHeadline() throws Rte6mainIOException;
	public String getDateline() throws Rte6mainIOException;
	public LinkedHashMap<Integer, String> getMapSentences() throws Rte6mainIOException;
}
