package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import eu.excitementproject.eop.biutee.utilities.preprocess.HandleAllCapitalTextPreprocessor;


public class DemoHandleAllCapital
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			HandleAllCapitalTextPreprocessor tp = new HandleAllCapitalTextPreprocessor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String line = reader.readLine();
			while (!line.equalsIgnoreCase("exit"))
			{
				System.out.println("line before: "+line);
				tp.setText(line);
				tp.preprocess();
				System.out.println("line after: "+tp.getPreprocessedText());
				line = reader.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			
		}
		
	}

}
