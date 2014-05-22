package eu.excitementproject.eop.common.utilities.text;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;

import eu.excitementproject.eop.common.utilities.ProgramExecution;
import eu.excitementproject.eop.common.utilities.ProgramExecutionException;
import eu.excitementproject.eop.common.utilities.Utils;



/**
 * Uses "uni2ascii" program to convert Unicode string
 * to an ASCII string.
 * This implementation may be still unstable. Further programming
 * effort should be done to make sure it works.
 * @author Asher Stern
 *
 */
public class DefaultUnicodeToAsciiConverter extends UnicodeToAsciiConverter
{
	public static final Charset INPUT_CHARSET = Charset.forName("UTF-8");
	public static final String CONVERTER_PROGRAM = "uni2ascii";
	public static final String[] CONVERTER_ARGUMENTS = new String[]{
		"-B",
		"-w",
		"-q"
	};
	protected static ArrayList<String> command = null;
	static
	{
		command = new ArrayList<String>(CONVERTER_ARGUMENTS.length+1);
		command.add(CONVERTER_PROGRAM);
		command.addAll(Utils.arrayToCollection(CONVERTER_ARGUMENTS, new ArrayList<String>(CONVERTER_ARGUMENTS.length)));
	}

	@Override
	protected void doConvert() throws UnicodeToAsciiConverterException
	{
		try
		{
			StringReader reader = new StringReader(this.text);
			ProgramExecution execution = new ProgramExecution(command, reader, INPUT_CHARSET);
			execution.execute();
			LinkedList<String> output = execution.getOutput();
			if (null==output)
				throw new UnicodeToAsciiConverterException("The converter program returned null.");
			StringBuffer sbResult = new StringBuffer();
			for (String line : output)
			{
				sbResult.append(line+"\n");
			}
			this.text = sbResult.toString();
		}
		catch(ProgramExecutionException e)
		{
			throw new UnicodeToAsciiConverterException("Failed to convert due to the nested exception.",e);
		}
		


	}
	
	
	

}
