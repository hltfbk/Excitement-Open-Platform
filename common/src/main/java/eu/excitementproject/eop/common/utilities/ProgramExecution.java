package eu.excitementproject.eop.common.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;


/**
 * Executes an external program.
 * The class gets the input for the program, reads the output of
 * the program, such that the user can get the output by calling
 * the method {@link #getOutput()}.
 * 
 * If the user does not want to provide input for the program, all
 * needed is to give <code>null</code> as the <code>Reader</code> given
 * to the constructor.
 * 
 * @author Asher Stern
 *
 */
public class ProgramExecution
{
	//////////////// public static constants /////////////////////////////
	public static final String emptyArgument = (OS.isWindows()?"\"\"":"");
	

	////////////////////// PUBLIC PART /////////////////////////
	
	/**
	 * Constructs the {@linkplain ProgramExecution} object with the
	 * program to be executed and the command line parameters, and with
	 * the input to be redirected into the standard input of the program.
	 * 
	 * @param command The program name (path), and command line arguments.
	 * The first element in the list is the program name, and the rest are
	 * command line arguments.
	 * @param reader <code>null</code> is legal here.
	 * The <code>Reader</code> given here is the input to the program,
	 * to be redirected to the program's standard input.
	 */
	public ProgramExecution(List<String> command,Reader reader) throws ProgramExecutionException
	{
		this.command = command;
		if (command==null)
			throw new ProgramExecutionException("empty command line supplied");
		this.reader = reader;
		StringBuffer sbCommand = new StringBuffer();
		boolean firstIteration = true;
		for (String token: command)
		{
			if (firstIteration)firstIteration = false;
			else sbCommand.append(" ");
			sbCommand.append(token);
		}
		this.commandStringRepresentation = sbCommand.toString();
		
	}

	/**
	 * Constructs the {@linkplain ProgramExecution} object with the
	 * program to be executed and the command line parameters, and with
	 * the input to be redirected into the standard input of the program.
	 * 
	 * @param command The program name (path), and command line arguments.
	 * The first element in the list is the program name, and the rest are
	 * command line arguments.
	 * @param reader <code>null</code> is legal here.
	 * The <code>Reader</code> given here is the input to the program,
	 * to be redirected to the program's standard input.
	 * @param charset character set to be used for filling the process'
	 * standard input.
	 * @throws ProgramExecutionException
	 */
	public ProgramExecution(List<String> command,Reader reader,Charset charset) throws ProgramExecutionException
	{
		this(command,reader);
		this.charset = charset;
	}
	
	/**
	 * Set whether the standard error output (<code>stderr</code>)
	 * of the process will be redirected to the standard output
	 * (<code>stdout</code>).
	 * <BR>
	 * The default is <code>false</code>.
	 * <BR>
	 * If the standard error is not redirected, then the error stream
	 * is closed immediately when the process starts. 
	 * 
	 * @param redirectError true if the standard error should be
	 * redirected to the standard output. 
	 */
	public void setRedirectError(boolean redirectError)
	{
		this.redirectError = redirectError;
	}
	
	
	/**
	 * Executes the program. Fills the output, such that it later
	 * can be observed by the user by calling {@link #getOutput()} method.
	 * <P>
	 * Note: The method is blocking, and will not return until the end
	 * of the program's execution.
	 * 
	 * @throws ProgramExecutionException any error.
	 */
	public void execute() throws ProgramExecutionException
	{
		try
		{
			executeImplementation();
		}
		catch(IOException e)
		{
			throw new ProgramExecutionException("Failed to run: "+commandStringRepresentation,e);
		}
		catch(InterruptedException e)
		{
			throw new ProgramExecutionException("IO threads failure during execution of: "+commandStringRepresentation,e);
		}
	}
	
	
	/**
	 * Returns the output of the program. That output is the
	 * text that was printed to the standard output by the program
	 * (i.e. if the program had been executed from command line,
	 * that output would have been printed to the screen or console).
	 * 
	 * @return The output as list of string. Each element in the list is
	 * a line.
	 */
	public LinkedList<String> getOutput()
	{
		return this.output;
	}

	/**
	 * Return the exitValue of the process. 
	 * <p>
	 * <b>NOTE</b> that this method causes you to <i>wait</i> for the process to terminate. It's necessary 'cos checking the exit value of a
	 * running process raises an IllegalThreadStateException.
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	public int getExitValue() throws InterruptedException
	{
		process.waitFor();
		return process.exitValue();
	}

	///////////////////////// PROTECTED & PRIVATE PART /////////////////////////
	
	// protected static constants
	protected static final int BLOCK_SIZE = 512;
	
	
	
	///////////////////// nested classes ///////////////////////////
	
	private class Filler implements Runnable
	{
		public void run()
		{
			try
			{
				char[] buffer = new char[BLOCK_SIZE];
				int readResult = 0;
				OutputStreamWriter processOutputStreamWriter = null;
				if (null==charset)
					processOutputStreamWriter = new OutputStreamWriter(process.getOutputStream());
				else
					processOutputStreamWriter = new OutputStreamWriter(process.getOutputStream(),charset);
					
					
				BufferedReader bufferedReader = new BufferedReader(reader);
				for (readResult = bufferedReader.read(buffer);readResult>=0;readResult = bufferedReader.read(buffer))
				{
					processOutputStreamWriter.write(buffer, 0, readResult);
				}
				processOutputStreamWriter.close();
			}
			catch(Exception e)
			{
				exceptionThrownByIOThreads = e;
			}
		}
	} // end of nested class Filler
	
	private class Fetcher implements Runnable
	{
		public void run()
		{
			try
			{
				BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = processReader.readLine();
				while (line != null)
				{
					output.add(line);
					line = processReader.readLine();
				}
				processReader.close();
			}
			catch(Exception e)
			{
				exceptionThrownByIOThreads = e;
			}
		}
	} // end of nested class Fetcher
	
	
	
	/////////////////////// protected methods /////////////////////
	
	protected void executeImplementation() throws IOException, ProgramExecutionException, InterruptedException
	{
		this.output = new LinkedList<String>();
		
		exceptionThrownByIOThreads = null;
		ProcessBuilder builder = new ProcessBuilder(command);
		if (this.redirectError)
			builder.redirectErrorStream(true);
		process = builder.start();
		if (!this.redirectError)
			if (OS.isWindows())
				process.getErrorStream().close();
		Thread threadInputFiller = null;
		if (this.reader != null)
		{
			threadInputFiller = new Thread(new Filler());
		}
		
		Thread threadOutputGetter = new Thread(new Fetcher());

		if (this.reader != null)
		{
			threadInputFiller.start();
		}
		else
		{
			process.getOutputStream().close();
		}
		threadOutputGetter.start();
		if (this.reader != null)
			threadInputFiller.join();
		threadOutputGetter.join();
		if (exceptionThrownByIOThreads!=null)
		{
			throw new ProgramExecutionException("IO redirection thread has thrown an exception. The command was: "+commandStringRepresentation,exceptionThrownByIOThreads);
		}
	}
	
	///////////////////////// protected fields //////////////////////////////
	
	protected List<String> command;
	protected String commandStringRepresentation;
	protected Reader reader;
	protected LinkedList<String> output;
	protected Exception exceptionThrownByIOThreads = null;
	protected Process process;
	protected Charset charset = null;
	protected boolean redirectError = false;

}
