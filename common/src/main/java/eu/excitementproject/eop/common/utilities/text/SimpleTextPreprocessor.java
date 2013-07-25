package eu.excitementproject.eop.common.utilities.text;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleTextPreprocessor implements TextPreprocessor
{
	protected final static Map<String, String> REPLACE_MAP;
	static
	{
		REPLACE_MAP = new LinkedHashMap<String, String>();
		REPLACE_MAP.put("gonna","going to");
		REPLACE_MAP.put("wonna","want to");
		REPLACE_MAP.put("don\'t","do not");
		REPLACE_MAP.put("dont","do not");
		REPLACE_MAP.put("doesn\'t","does not");
		REPLACE_MAP.put("doesnt","does not");
		REPLACE_MAP.put("didn\'t","did not");
		REPLACE_MAP.put("didnt","did not");
		REPLACE_MAP.put("Don\'t","Do not");
		REPLACE_MAP.put("Dont","Do not");
		REPLACE_MAP.put("Doesn\'t","Does not");
		REPLACE_MAP.put("Doesnt","Does not");
		REPLACE_MAP.put("Didn\'t","Did not");
		REPLACE_MAP.put("Didnt","Did not");
		REPLACE_MAP.put("I\'m","I am");
		REPLACE_MAP.put("I\'ll","I will");
		REPLACE_MAP.put("mom","mother");
		REPLACE_MAP.put("pic","picture");
		REPLACE_MAP.put("can\'t","cannot");
		REPLACE_MAP.put("cant","cannot");
		REPLACE_MAP.put("It\'s","It is");
		REPLACE_MAP.put("Its","It is");
		REPLACE_MAP.put("it\'s","it is");
		REPLACE_MAP.put("its","it is");
		
		
		
	}

	public void setText(String text)
	{
		this.originalText = text;
		this.preprocessedText = null;
		preprocessDone = false;
	}

	public void preprocess()
	{
		if (originalText!=null)
		{
			preprocessedText = originalText;
			for (String key : REPLACE_MAP.keySet())
			{
				String value = REPLACE_MAP.get(key);
				preprocessedText = preprocessedText.replaceAll(key, value);
			}
		}
		preprocessDone = true;
	}

	
	public String getPreprocessedText() throws TextPreprocessorException
	{
		if (preprocessDone)
			return this.preprocessedText;
		else
			throw new TextPreprocessorException("preprocess() method was not called.");
		
	}
	
	protected String originalText;
	protected String preprocessedText = null;
	protected boolean preprocessDone = false;
	
	
}
