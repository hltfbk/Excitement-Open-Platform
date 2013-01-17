package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * A demo of {@link RTEMainWriter}.
 * Writes 1/12 of the original file into a new file.
 * @author Asher Stern
 *
 */
public class RTEMainWriterDemo
{
	public final static int DEFAULT_SKIP = 6;
	
	public RTEMainWriterDemo(String xmlFileName, int skip)
	{
		super();
		this.xmlFileName = xmlFileName;
		this.skip = skip;
	}

	public void f() throws Exception
	{
		File xmlFile = new File(this.xmlFileName);
		RTEMainReader reader = new DefaultRTEMainReader();
		reader.setXmlFile(xmlFile);
		reader.setHasClassification();
		reader.read();
		Map<Integer,TextHypothesisPair> result = reader.getMapIdToPair();
		List<TextHypothesisPair> list = new LinkedList<TextHypothesisPair>();
		int noCounter = 0;
		int yesCounter = 0;
		for (Integer id : result.keySet())
		{
			TextHypothesisPair pair = result.get(id);
			boolean entailment = pair.getBooleanClassificationType().booleanValue();
			if (entailment)
			{
				if (yesCounter==0)
				{
					list.add(pair);
				}
				yesCounter++;
				if (yesCounter==skip) yesCounter = 0;
			}
			else
			{
				if (noCounter==0)
				{
					list.add(pair);
				}
				noCounter++;
				if (noCounter==skip) noCounter = 0;
			}
		}
		
		
		File xmlTarget = new File(this.xmlFileName+".my.xml");
		RTEMainWriter writer = new DefaultRTEMainWriter(xmlTarget, list);
		writer.write();
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1) throw new Exception("args");
			String filename = args[0];
			RTEMainWriterDemo demo = new RTEMainWriterDemo(filename, DEFAULT_SKIP);
			demo.f();
		
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		
		

	}
	
	private String xmlFileName;
	private int skip = DEFAULT_SKIP;

}
