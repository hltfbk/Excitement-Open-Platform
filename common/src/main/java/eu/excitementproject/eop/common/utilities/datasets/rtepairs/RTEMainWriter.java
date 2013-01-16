package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.File;
import java.util.List;

/**
 * Used to create an XML file from a list of Text-Hypothesis pairs.
 * @author Asher Stern
 *
 */
public abstract class RTEMainWriter
{
	public RTEMainWriter(File output,List<TextHypothesisPair> pairs) throws RTEMainWriterException
	{
		if (output==null)
			throw new RTEMainWriterException("output file is null");
		if (pairs==null)
			throw new RTEMainWriterException("pairs is null");

		this.output = output;
		this.pairs = pairs;
		
	}
	
	public abstract void write() throws RTEMainWriterException;
	
	protected File output;
	protected List<TextHypothesisPair> pairs;
}
