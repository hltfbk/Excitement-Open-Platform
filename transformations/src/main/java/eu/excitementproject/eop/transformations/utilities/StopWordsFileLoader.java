package eu.excitementproject.eop.transformations.utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
//import eu.excitementproject.eop.transformations.rteflow.systems.SystemInitialization;

/**
 * Loads a file of stop-words, and provides them as an {@link ImmutableSet}.
 * <P>
 * The stop-words file is just a text file, such that each line is exactly one word.
 * 
 * @see SystemInitialization
 * 
 * @author Asher Stern
 * @since Jun 13, 2012
 *
 */
public class StopWordsFileLoader
{
	public StopWordsFileLoader(String fileName)
	{
		super();
		this.fileName = fileName;
	}
	
	public void load() throws IOException
	{
		Set<String> setStopWords = new LinkedHashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		try
		{
			for (String line = reader.readLine();line != null;line = reader.readLine())
			{
				line = line.trim();
				if (line.length()>0)
				{
					setStopWords.add(line);
				}
			}
		}
		finally
		{
			reader.close();
		}
		this.stopWords = new ImmutableSetWrapper<String>(setStopWords);
	}
	
	

	public ImmutableSet<String> getStopWords()
	{
		return stopWords;
	}



	private String fileName;
	
	private ImmutableSet<String> stopWords;
}
