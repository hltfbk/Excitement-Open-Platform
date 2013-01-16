package eu.excitementproject.eop.common.utilities;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds a dictionary - a map of key-value strings - from a given string.
 * The given string should be [key1,value1],[key2,value2]... string.
 * 
 * @author Asher Stern
 * @since Mar 25, 2012
 *
 */
public class DictionaryRegExp
{
	@SuppressWarnings("serial")
	public static class DictionaryRegExpException extends Exception
	{public DictionaryRegExpException(String message){super(message);}
	public DictionaryRegExpException(String message, Throwable t){super(message,t);}}

	
	
	public DictionaryRegExp(String inputString)
	{
		super();
		this.inputString = inputString;
	}
	
	public DictionaryRegExp(String inputString, boolean trim)
	{
		super();
		this.inputString = inputString;
		this.trim = trim;
	}


	public void extractDictionary() throws DictionaryRegExpException
	{
		dictionary = new LinkedHashMap<String, String>();
		Matcher dictionaryMatcher = dictionaryPattern.matcher(inputString);
		boolean matchFound = dictionaryMatcher.find();
		while (matchFound)
		{
			try
			{
				String entityString = dictionaryMatcher.group(1);
				if (entityString!=null)
				{
					Matcher entityMatcher = entityPattern.matcher(entityString);
					boolean entityMatchFound = entityMatcher.matches();
					if (entityMatchFound)
					{
						String key = entityMatcher.group(1);
						if (trim)key=key.trim();
						String value = entityMatcher.group(2);
						if (trim) value=value.trim();
						if (dictionary.containsKey(key)) throw new DictionaryRegExpException("duplicate key : "+key);
						dictionary.put(key, value);
					}
				}
			}
			catch(IllegalStateException e){throw new DictionaryRegExpException("match failed for input string: "+inputString,e);}
			catch(IndexOutOfBoundsException e){throw new DictionaryRegExpException("match failed for input string: "+inputString,e);}

			matchFound = dictionaryMatcher.find();
		}
	}
	
	
	
	public LinkedHashMap<String, String> getDictionary() throws DictionaryRegExpException
	{
		if (null==dictionary) throw new DictionaryRegExpException("Not extracted");
		return dictionary;
	}



	private String inputString;
	boolean trim=true;
	
	private Pattern dictionaryPattern = Pattern.compile(",*(\\[[^\\[\\]]*\\])");
	private Pattern entityPattern = Pattern.compile("\\[([^,\\[\\]]*),([^,\\[\\]]*)\\]");
	
	private LinkedHashMap<String,String> dictionary;
}
