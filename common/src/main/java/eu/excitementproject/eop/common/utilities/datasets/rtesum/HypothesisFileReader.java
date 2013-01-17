package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Map;

/**
 * Reads an hypothesis file. Hypothesis file contains a set of hypotheses,
 * such that each hypothesis has:
 * <OL>
 * <LI>an ID</LI>
 * <LI>the hypothesis sentence itself.</LI>
 * <LI>Unnecessary additional information, which is a sentence from
 * "cluster B" (unnecessary corpus) that that hypothesis was created from.
 * That information is unnecessary, but exist, and is represented
 * by the class {@link HypothesisRef}</LI>
 * </OL>
 * @author Asher Stern
 *
 */
public interface HypothesisFileReader
{
	public void setXml(String xmlFileName) throws Rte6mainIOException;
	public void read() throws Rte6mainIOException;
	public Map<String,String> getHypothesisTextMap() throws Rte6mainIOException;
	public Map<String,HypothesisRef> getHypothesisRefMap() throws Rte6mainIOException;
}
