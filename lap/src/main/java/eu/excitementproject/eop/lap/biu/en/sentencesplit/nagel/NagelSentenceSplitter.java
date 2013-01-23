package eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.ProgramExecution;
import eu.excitementproject.eop.common.utilities.ProgramExecutionException;
import eu.excitementproject.eop.lap.biu.sentencesplit.AbstractSentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;


/**
 * Implements {@link SentenceSplitter} by invoking Sebastian Nagel's software.
 * It can be found at http://www.cis.uni-muenchen.de/~wastl/misc/tokenizer.tgz
 * <P>
 * Make sure the "tokenizer" program exists in your path (PATH environment variable).
 * @author Asher Stern
 */
public class NagelSentenceSplitter extends AbstractSentenceSplitter
{
	protected final static String TOKENIZER_PROGRAM_NAME = "tokenizer";
	protected static final String endOfSentenceMarker = ProgramExecution.emptyArgument;
	protected final int MAX_EXEC_PARAM_SZ = 62000;
	protected static String[] tokenizerArgumentsIORedirected = new String[]{
			"-L", "en", "-E", endOfSentenceMarker, "-S", "-P", "-n"
	};
	protected static LinkedList<String> command = null;
	protected static String commandStringRepresentation = null;
	
	static
	{
		command = new LinkedList<String>();
		command.add(OS.programName(TOKENIZER_PROGRAM_NAME));
		for (String argumnet : tokenizerArgumentsIORedirected)
		{
			command.add(argumnet);
		}
		
		 
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (String cmdToken : command)
		{
			if (firstIteration)
				firstIteration = false;
			else
				sb.append(" ");
			sb.append(cmdToken);
		}
		commandStringRepresentation = sb.toString();
		
		
	}
	
	
	protected boolean usingFileForInput = false;
	protected boolean usingFileForOutput = false;
	private LinkedList<Reader> readers = new LinkedList<Reader>();
	
	protected boolean documentSet = false;
	protected boolean splitWasCalled = false;
	protected LinkedList<String> sentences; 
	
	protected void clean()
	{
		this.documentSet = false;
		this.sentences = null;
	}
	 
	
	public boolean isUsingFileForInput()
	{
		return usingFileForInput;
	}

	public void setUsingFileForInput(boolean usingFileForInput)
	{
		this.usingFileForInput = usingFileForInput;
	}
	
	public boolean isUsingFileForOutput()
	{
		return usingFileForOutput;
	}

	public void setUsingFileForOutput(boolean usingFileForOutput)
	{
		this.usingFileForOutput = usingFileForOutput;
	}

	
	
	
	
	
	protected void setDocumentImpl(File textFile) throws SentenceSplitterException
	{
		clean();
		try
		{
			this.readers = new LinkedList<Reader>();
			this.readers.add( new FileReader(textFile));
		}
		catch (FileNotFoundException e)
		{
			throw new SentenceSplitterException("invalid input File:"+textFile.toString(),e);
		}
		documentSet = true;
	}

	protected void setDocumentImpl(String documentContents) throws SentenceSplitterException
	{
		clean();
		this.readers = new LinkedList<Reader>();
		
		// Amnon 8.9.10: fragment documentContents into digestible chunks
		int length = documentContents.length();
		int right = 0;
		int left = 0;
		while (right < length)
		{
			right = Math.min(length, right + MAX_EXEC_PARAM_SZ);
			this.readers.add(new StringReader(documentContents.substring(left, right)));
			left = right;
		}
		documentSet = true;
	}

	protected void setDocumentImpl(InputStream documentStream) throws SentenceSplitterException
	{
		clean();
		this.readers = new LinkedList<Reader>();
		this.readers.add(new InputStreamReader(documentStream));
		documentSet = true;
	}

	protected void splitImpl() throws SentenceSplitterException
	{
		if (!documentSet)
			throw new SentenceSplitterException("Invalid call to split() method. Document not set yet.");
		try
		{
			// TODO Make the option of using files, instead of IO redirection.
			
			/*
			 *  Amnon 13.09.10 feed the sentence splitter with the fragmented readers, one by one,
			 *  and then sentenceSplit the text around the arbitrary demarcations between fragments 
			 */
			this.sentences = new LinkedList<String>();
			
			for (Reader reader : readers)
			{
				LinkedList<String> ret = executeCommandWithReader(command, reader);

				// sentenceSplit the concatenation of ret's first sentence with the previous sentence - 'cos the two were arbitrarily demarcated 
				if (!this.sentences.isEmpty() & !ret.isEmpty())
				{
					Reader demarcationReader = new StringReader(this.sentences.removeLast().concat(ret.removeFirst()));
					ret.addAll(0, executeCommandWithReader(command, demarcationReader));
				}
				this.sentences.addAll(ret);
			} 
			splitWasCalled = true;
		}
		catch (ProgramExecutionException e)
		{
			throw new SentenceSplitterException("Failed to run: " + commandStringRepresentation,e);
		} catch (InterruptedException e) {
			throw new SentenceSplitterException("Failed to run: " + commandStringRepresentation,e);
		}
	}
	
	/**
	 * execute the sentence-splitting command
	 * 
	 * @param command
	 * @param reader
	 * @return
	 * @throws ProgramExecutionException
	 * @throws InterruptedException
	 */
	private LinkedList<String> executeCommandWithReader( LinkedList<String> command, Reader reader) throws ProgramExecutionException, InterruptedException 
	{
		ProgramExecution execution = new ProgramExecution(command, reader);
		execution.execute();
		LinkedList<String> out = execution.getOutput();
		
		// if the return value is't 0, there's probably a runtime error
		int ex = 1;
		ex = execution.getExitValue();
		if (0 != ex)
			throw new ProgramExecutionException("tokenizer returned an irregular exit value " + ex + ". Make sure cygwin is installed.");
		
		return out;
	}
	
	protected List<String> getSentencesImpl()
	{
		if (!splitWasCalled)
			throw new RuntimeException(new SentenceSplitterException("Illegal call to getSentences(). Must call split before."));
		return sentences;
	}


}
