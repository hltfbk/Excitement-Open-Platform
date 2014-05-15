package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;

public class DemoInverseSigmoid
{
	public static void f(String[] args) throws IOException
	{
		System.out.println("DemoInverseSigmoid");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		double d=0;
		String str;
		System.out.println("Enter non-numerical string for exit");
		do
		{
			System.out.println("Enter number:");
			str = reader.readLine();
			try
			{
				d = Double.parseDouble(str);
				System.out.println(String.format("%-3.6f", ClassifierUtils.inverseSigmoid(d)));
			}
			catch(NumberFormatException e)
			{
				System.out.println("Quit.");
				str=null;
			}
		}
		while(str!=null);
	}
	
	public static void main(String[] args)
	{
		try
		{
			f(args);
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}
}
