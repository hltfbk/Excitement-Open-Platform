package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.util.List;

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public interface AnswersFileWriter
{
	public void setXml(String xmlFileName) throws RteKbpIOException;
	
	public void setRootElementName(String rootElementName) throws RteKbpIOException;
	
	public void setPairsAnswers(List<PairAnswer> answers) throws RteKbpIOException;
	
	public void write() throws RteKbpIOException;
}
