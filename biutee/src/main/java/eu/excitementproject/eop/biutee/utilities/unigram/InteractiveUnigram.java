package eu.excitementproject.eop.biutee.utilities.unigram;

import eu.excitementproject.eop.transformations.utilities.MLELidstonSmoothedUnigramProbabilityEstimation;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author Asher Stern
 * @since May 13, 2013
 *
 */
public class InteractiveUnigram
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			new InteractiveUnigram().go(args);
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}
	
	public void go(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException
	{
		UnigramProbabilityEstimation estimation = MLELidstonSmoothedUnigramProbabilityEstimation.fromSerializedFile(new File(args[0]));
		System.out.println("Enter word. Type exit to exit.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = reader.readLine().trim();
		boolean stop = false;
		while (!stop)
		{
			double est = estimation.getEstimationFor(line);
			System.out.printf("%-4.12f\n",est);
			line = reader.readLine().trim();
			if ("exit".equalsIgnoreCase(line))
				stop=true;
		}
	}

}
