package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.File;
import java.util.Map;


/**
 * Reads the RTE data set XML file.
 * 
 * @author Asher Stern
 *
 */
public interface RTEMainReader
{
	
	/**
	 * Set the RTE data-set file.
	 * @param xmlFile
	 */
	public void setXmlFile(File xmlFile);
	
	/**
	 * Classification means the "answer": the text entails
	 * the hypothesis or not.
	 * In the training data, the classification is specified,
	 * while in the test data (until the answers are published)
	 * the classification is not specified.
	 * <P>
	 * By calling this method, if the XML contains a pair
	 * without its classification - an exception will be
	 * thrown by the {@link #read()} method.
	 */
	public void setHasClassification();
	
	/**
	 * Read the file specified by {@link #setXmlFile(File)}.
	 * @throws RTEMainReaderException
	 */
	public void read() throws RTEMainReaderException;
	
	/**
	 * After reading the file, by calling {@link #read()} method,
	 * get the data by this method.
	 * @return
	 * @throws RTEMainReaderException
	 */
	public Map<Integer,TextHypothesisPair> getMapIdToPair() throws RTEMainReaderException;
}
