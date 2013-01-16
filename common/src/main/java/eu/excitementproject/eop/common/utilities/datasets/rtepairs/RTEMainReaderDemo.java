package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.File;
import java.util.Map;


/**
 * 
 * @author Asher Stern
 *
 */
public class RTEMainReaderDemo
{

	public static void main(String[] args)
	{
		try
		{
			
			if (args.length<1)
				throw new Exception ("First argument should be XML path, which is RTE dataset xml file.");
			
			RTEMainReader reader = new DefaultRTEMainReader();
			reader.setXmlFile(new File(args[0]));
			reader.read();
			Map<Integer, TextHypothesisPair> map = reader.getMapIdToPair();
			System.out.println(map.keySet().size());
			for (Integer id : map.keySet())
			{
				TextHypothesisPair pair = map.get(id);
				System.out.println(pair.getId());
				System.out.println(pair.getText());
				System.out.println(pair.getHypothesis());
				System.out.println(pair.getClassificationType().toString());
				System.out.println(pair.getAdditionalInfo());
				System.out.println("-----------------------------------------");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
