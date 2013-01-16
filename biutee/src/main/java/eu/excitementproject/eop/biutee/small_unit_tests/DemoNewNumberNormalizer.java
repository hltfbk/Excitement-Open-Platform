package eu.excitementproject.eop.biutee.small_unit_tests;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_MODULE_NAME;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import eu.excitementproject.eop.biutee.utilities.preprocess.NewNormalizerBasedTextPreProcessor;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

public class DemoNewNumberNormalizer
{
	public static void f(String[] args) throws Exception
	{
		ConfigurationFile configurationFile = new ConfigurationFile(args[0]);
		ConfigurationParams params =  configurationFile.getModuleConfiguration(RTE_PAIRS_PREPROCESS_MODULE_NAME);
		
		NewNormalizerBasedTextPreProcessor textPreprocessor = new NewNormalizerBasedTextPreProcessor(params);
		
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean stop = false;
		do
		{
			stop = true;
		
			System.out.println("Enter a string:");
			line = reader.readLine();
			if (line!=null)
			{
				if (!line.equalsIgnoreCase("bye"))
				{
					stop = false;
					textPreprocessor.setText(line);
					textPreprocessor.preprocess();
					String normalizedLine = textPreprocessor.getPreprocessedText();
					System.out.println(normalizedLine);
				}
			}
		}while(!stop);
		
		System.out.println("Bye Bye! Hope you enjoyed.");
		
		
		
		
		
		
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
