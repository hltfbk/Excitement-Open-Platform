package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.util.List;

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public interface PairsFileReader
{
	public void setXml(String xmlFileName) throws RteKbpIOException;
	
	public void setRootElementName(String rootElementName) throws RteKbpIOException;
	
	public void setGoldStandard(boolean isGoldStandard) throws RteKbpIOException;
	
	public void read() throws RteKbpIOException;
	
	public List<PairInformation> getPairs() throws RteKbpIOException;
}
