package eu.excitementproject.eop.lexicalminer.definition.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import javax.naming.ConfigurationException;


/**
 * This singleton class should be use to check whether a given word is a stopword
 * based on the supplied stopwords list given by the file from the configuration.
 * NOTE: init must be call before first use
 * 
 * @author Alon Halfon
 * @since 6/5/2012
 *
 */
public class StopwordsDictionary {
	
	private HashSet<String> stopwords;
	private static boolean initialized=false;
	private static StopwordsDictionary instance = null;

	

	protected StopwordsDictionary(String stopwordsFilePath) throws IOException {
	
		stopwords=new HashSet<String>();
		// load the list of stopwords

		File file = new File(stopwordsFilePath);
		if (!file.exists())
			throw new FileNotFoundException(String.format("Missing stopwords list file in path:%s",stopwordsFilePath));
		
		@SuppressWarnings("resource")
		BufferedReader input =  new BufferedReader(new FileReader(file));
		
		String line;

		while ((line=input.readLine())!=null)
			stopwords.add(line.trim());

		
	}
	
	
	
	public static void init(String stopwordsFilePath) throws IOException
	{
		instance = new StopwordsDictionary(stopwordsFilePath);
		
		
		initialized=true;
	}
	
	public static StopwordsDictionary getInstance() throws ConfigurationException {
		if (!initialized)
			throw new ConfigurationException("You must initialize the StopwordsDictionary class before first use by using the init function");
				
		return instance;
	}
		
	/*
	 * Return true if the given word is a stop word according to the stop word list.
	 * uses the toLower so it's unnecessary to call toLower before using the function. 
	 */
	public boolean isStopWord(String word)
	{
		if (stopwords.contains(word.toLowerCase()))
			return true;
		return false;
	}
	
	
	
	   
	   
}
