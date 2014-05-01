package eu.excitementproject.eop.common.utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Parses the command line arguments.
 * For example:
 * -e workdir -r 1.xml 2.xml -s 3.xml abc
 * will be parsed as follows:
 * in the flagsValues map:
 * <pre>
 * +---------+----------+
 * |"-e"     |"workdir" |
 * +---------+----------+
 * |"-r"     |"1.xml"   |
 * +---------+----------+
 * |"-s"     |"3.xml"   |
 * +---------+----------+
 * </pre>
 * and in the freeArguments list:
 * <pre>
 * {"2.xml","abc"}
 * </pre>
 * <P>
 * This class has been copied from Utils project, at org.BIU.utils.environment package.
 * </P> 
 * 
 * 
 * 
 * @author Asher Stern, July 2009
 *
 */
public class CommandLineArguments
{
	//////////////////////// PUBLIC PART ////////////////////////////////
	
	public static final String FLAG_START = "-";
	
	
	/**
	 * Constructor - parses the command line arguments.
	 * @param args the command line arguments.
	 */
	public CommandLineArguments(String[] args)
	{
		init();
		fillMap(args);
	}
	
	// getters
	
	public Map<String, String> getFlagsValues()
	{
		return flagsValues;
	}
	
	public List<String> getFreeArguments()
	{
		return freeArguments;
	}
	
	
	///////////////// PROTECTED & PRIVATE PART ////////////////////////
	
	protected void init()
	{
		flagsValues = new HashMap<String,String>();
		freeArguments = new LinkedList<String>();
		
	}
	
	protected void fillMap(String[] args)
	{
		String currentFlag = null;
		for (String arg : args)
		{
			if (arg.startsWith(FLAG_START))
			{
				currentFlag = arg;
				flagsValues.put(arg,EMPTY_STRING); // default value for that flag is the empty string.
			}
			else
			{
				if (currentFlag != null)
				{
					flagsValues.put(currentFlag, arg);
				}
				else
				{
					freeArguments.add(arg);
				}
				
				currentFlag = null;
				
			}
			
		} // end of for
		
	}


	protected Map<String,String> flagsValues;
	protected List<String> freeArguments;
	
	private static final String EMPTY_STRING = "";
}
