package eu.excitementproject.eop.lap.biu.en.sentencesplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.DefaultSentenceSplitter;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;


/**
 * Uses $JARS/morphadorner
 * 
 * @author Asher Stern
 * @since Apr 16, 2012
 *
 */
public class MorphAdornerSentenceSplitter implements SentenceSplitter
{
	@Override
	public void setDocument(File textFile) throws SentenceSplitterException
	{
		this.sentences = null;
		try
		{
			this.text = FileUtils.loadFileToString(textFile);
		}
		catch(IOException e)
		{
			throw new SentenceSplitterException("Failed to read the file: "+textFile.getPath(),e);
		}
	}

	@Override
	public void setDocument(String documentContents)
			throws SentenceSplitterException
	{
		this.sentences = null;
		this.text = documentContents;
	}

	@Override
	public void setDocument(InputStream documentStream) throws SentenceSplitterException
	{
		this.sentences = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(documentStream));
		try
		{
			StringBuffer sb = new StringBuffer();
			String line = reader.readLine();
			while (line != null)
			{
				sb.append(line).append(" ");
				line = reader.readLine();
			}
			this.text = sb.toString();
		}
		catch (IOException e)
		{
			throw new SentenceSplitterException("Failed to read the input stream.",e);
		}
		finally
		{
			try{reader.close();}catch(IOException e){}
		}
	}

	@Override
	public void split() throws SentenceSplitterException
	{
		if (null==text) throw new SentenceSplitterException("null text");
		DefaultSentenceSplitter realSplitter = new DefaultSentenceSplitter();
		List<List<String>> listOfListOfTokens = realSplitter.extractSentences(text);
		sentences = new ArrayList<String>(listOfListOfTokens.size());
		int[] offsets = realSplitter.findSentenceOffsets(text, listOfListOfTokens);
		for (int index=0;index<(offsets.length-1);++index)
		{
			sentences.add(text.substring(offsets[index], offsets[index+1]).trim());
		}
		
		this.text = null;
	}

	@Override
	public List<String> getSentences()
	{
		return sentences;
	}
	
	private String text = null;
	private List<String> sentences = null;

}
