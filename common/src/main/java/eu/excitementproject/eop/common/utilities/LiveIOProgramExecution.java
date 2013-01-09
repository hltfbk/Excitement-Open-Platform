package eu.excitementproject.eop.common.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Executes a program. The program stays "alive", i.e. reading from its <code>stdin</code>.
 * You can add text into the program's <code>stdin</code> by calling {@link #putLine(String)},
 * and get the program's output (lines that were printed to its <code>stdout</code>) by calling
 * {@link #getLine()}.
 * <P>
 * <B>Usage</B>
 * <ol>
 * <li>after creating an instance by <code> new </code> statement</li>
 * <li>Call {@link #start()} method </li>
 * <li>Call the methods {@link #putLine(String)} and {@link #getLine()} on a regular basis </li>
 * <li> when done: call {@link #endIO()} </li>
 * </ol>
 * <P>
 * <B>Note 1: </B> Even though you flush your output to the program (which is output stream from your point
 * of view, and stdin from the program's point of view), <B>it does not mean that the program flushes its
 * own output!</B>
 * <BR>
 * The meaning of that headache is that a native program, which uses "printf" command - will still "keep"
 * the output in its own buffers, and you will get it only later, in the best case...
 * <BR>
 * To solve the problem: For any C / C++ program, add <code>setbuf(stdout,0);</code> as the first
 * statement in its <code>void main(int argc, char** argv)</code> function.
 * (and if you don't have the native program's the source code, I don't know
 * how to help.
 * <P>
 * <B> Note 2: </B> Sometimes (especially if you work against Cygwin natives), it is recommended
 * to specify the character encoding of the process output stream (which is the stdin of the process).
 * Use the constructor {@link #LiveIOProgramExecution(List, Charset, boolean, boolean, boolean)} for that purpose.
 *  
 * @author Asher Stern
 *
 */
public class LiveIOProgramExecution
{
	////////////////////////////// PUBLIC PART ////////////////////////////////////

	// CONSTANTS:
	public static final String NULL_STRING_REPLACEMENT = "(null)";

	
	//////////////////// PUBLIC CONSTRUCTORS AND METHODS //////////////////////////
	
	public LiveIOProgramExecution(List<String> programAndArguments) throws LiveIOProgramExecutionException
	{
		this(programAndArguments,null,false,false,true);

	}
	
	public LiveIOProgramExecution(List<String> programAndArguments, Charset charset, boolean useStream, boolean redirectError, boolean treatNullAsError) throws LiveIOProgramExecutionException
	{
		if (programAndArguments==null)
			throw new LiveIOProgramExecutionException("LiveIOProgramExecution was constructed with null command");
		if (programAndArguments.size() == 0)
			throw new LiveIOProgramExecutionException("LiveIOProgramExecution was constructed with empty command");
		try
		{
			this.command = programAndArguments.toArray(new String[0]);
		}
		catch(Exception e)
		{
			throw new LiveIOProgramExecutionException("bad command.",e);
		}
		try
		{
			StringBuffer sbCommand = new StringBuffer();
			boolean firstIteration = true;
			for (String str : this.command)
			{
				if (firstIteration) firstIteration = false;
				else
					sbCommand.append(" ");
				sbCommand.append(str);
			}
			commandAsStringForDebugPurposes = sbCommand.toString();
		}
		catch(Exception e)
		{
			throw new LiveIOProgramExecutionException("bad command.",e);
		}
		this.charset = charset;
		this.useStream = useStream;
		this.redirectError = redirectError;
		this.treatNullAsError = treatNullAsError;
	}
	

	
	public void start() throws LiveIOProgramExecutionException
	{
		try
		{
			ArrayList<String> listCommand = Utils.arrayToCollection(this.command, new ArrayList<String>(this.command.length));
			ProcessBuilder builder = new ProcessBuilder(listCommand);
			if (this.redirectError)
				builder.redirectErrorStream(true);
			this.process = builder.start();
			if (!this.redirectError)
			{
				if (OS.isWindows())
					this.process.getErrorStream().close();
			}
			//this.process = Runtime.getRuntime().exec(this.command);
		}
		catch(IOException e)
		{
			throw new LiveIOProgramExecutionException("command execution failed for: "+this.commandAsStringForDebugPurposes);
		}
		this.programReader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
		if (!this.useStream)
		{
			if (null==this.charset)
				this.programWriter = new PrintWriter(this.process.getOutputStream());
			else
				this.programWriter = new PrintWriter(new OutputStreamWriter(this.process.getOutputStream(),this.charset));
		}
		else
		{
			if (null==this.charset)
				this.programPrintStream = new PrintStream(this.process.getOutputStream(),true);
			else
			{
				try
				{
					this.programPrintStream = new PrintStream(this.process.getOutputStream(),true,this.charset.name());
				}
				catch(UnsupportedEncodingException e)
				{
					throw new LiveIOProgramExecutionException("bad encoding name. See nested exception.",e);

				}
			}
		}
	}
	
	
	/**
	 * Call this method to get the next line from the program's
	 * <code> stdout </code>.
	 * <P>
	 * <B>This method is better, since it has more stability</B><BR>
	 * If the program is "stuck" due to some unknown reason, then <B> your program
	 * will not be "stuck" </B>, since it is guaranteed that after <code> timeout
	 * </code> milliseconds the method will return (or throw an exception).
	 * 
	 * 
	 * @param timeout time out, in milliseconds.
	 * @return the next line printed by the program to its standard output.
	 * @throws TimeOutLiveIOProgramExecutionException if timeout reached and
	 * the program did not yet print the next line to its standard output.
	 * 
	 * @throws LiveIOProgramExecutionException Any other error.
	 */
	public String getLine(long timeout) throws TimeOutLiveIOProgramExecutionException, LiveIOProgramExecutionException
	{
		if (synchException!=null)
			throw new LiveIOProgramExecutionException("An older exception prevents continuing. See nested exception.",synchException);
		if (getLineThreadException!=null)
			throw new LiveIOProgramExecutionException("An older exception prevents continuing. See nested exception.",getLineThreadException);
		
		asyncGetLineResult = null;
		
		if (null==asynchGetLineThread)
			createAndStartAsyncThread();
		
		
			
		synchronized(synchCall)
		{
			synchCall.setValue(false);
			synchCall.notify();
		}
		
		
		try
		{
			synchronized(synchReturn)
			{
				if (synchReturn.booleanValue()==true)
					synchReturn.wait(timeout);
				synchReturn.setValue(true);
			}
		}
		catch(Exception e)
		{
			synchException = e;
		}
		
		
		if (synchException!=null)
			throw new LiveIOProgramExecutionException("Synchronization exception.",synchException);
		if (getLineThreadException!=null)
			throw new LiveIOProgramExecutionException("An exception while trying to get the line.",getLineThreadException);
		//if (null==asyncGetLineResult)
		if (timeOut)
		{
			TimeOutLiveIOProgramExecutionException timeOutException = new TimeOutLiveIOProgramExecutionException("TimeOut");
			
			getLineThreadException = timeOutException; 
				// Later - no further call is legal.
			
			throw timeOutException;
		}
		if (this.treatNullAsError){ if (null==asyncGetLineResult){
			throw new LiveIOProgramExecutionException("null line returned");
		}}
		
		return asyncGetLineResult;
	}
	

	/**
	 * Prints a line to the program's standard input.
	 * @param line a line to print.
	 */
	public void putLine(String line)
	{
		if (!this.useStream)
		{
			this.programWriter.println(line);
			this.programWriter.flush();
		}
		else
		{
			this.programPrintStream.println(line);
			this.programPrintStream.flush();
		}
	}
	
	
	
	/**
	 * Less stable method to get the next line from the program.
	 * <P>
	 * It is preferred to use {@linkplain #getLine(long)}, such that
	 * even if the program (and you know how it is with native programs) will
	 * not return the next line - an exception will be thrown, such that your
	 * program will not be "stuck" on waiting the next line to be printed.
	 * 
	 * @return The next line printed by the program to its standard output.
	 * 
	 * @throws LiveIOProgramExecutionException Any error.
	 */
	public String getLine() throws LiveIOProgramExecutionException
	{
		String ret = getLineImpl();
		if (this.treatNullAsError){ if (null==ret){
			throw new LiveIOProgramExecutionException("null line returned");
		}}
		return ret;
	}
	
	public void endIO() throws LiveIOProgramExecutionException
	{
		try
		{
			this.programWriter.close();
			// this.programReader.close(); done by the async thread. see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4859836
			if (asynchGetLineThread!=null)
			{
				endAsyncThread();
			}
		}
		catch(Exception e)
		{
			// nothing throws an exception here above, nevertheless...
			throw new LiveIOProgramExecutionException("Failed to finalize LiveIOPrgoramExecution of: "+this.commandAsStringForDebugPurposes,e);
		}
	}

	
	/**
	 * Destroys the process.
	 * If an exception was thrown by the
	 * {@link LiveIOProgramExecution} object, then it is recommended
	 * to kill the process.
	 */
	public void destroyProcess()
	{
		try
		{
			process.exitValue();
		}
		catch(IllegalThreadStateException e)
		{
			// if we are here, then the process is alive
			try{process.destroy();}catch(Exception x){}
		}

	}
	
	/**
	 * Though an exception was thrown by {@link #getLine(long)} method,
	 * The {@link LiveIOProgramExecution} object will still be able
	 * to have new lines put in (i.e. call {@link #putLine(String)}).
	 * 
	 * <B>Your risk!</B>
	 */
	public void revive()
	{
		getLineThreadException = null;
		
	}
	
	//////////////////// PROTECTED AND PRIVATE PART ///////////////////////////
	
	
	
	private static class MutableBoolean
	{
		public MutableBoolean(boolean b)
		{
			this.b = b;
		}
		public boolean booleanValue(){return b;}
		public void setValue(boolean b){this.b = b;}
		private boolean b;
	}
	
	
	private class AsyncGetLineRunnable implements Runnable
	{
		public void run()
		{
			try
			{
				while(!end)
				{
					synchronized(synchCall)
					{
						if (synchCall.booleanValue()==true)
							synchCall.wait();
						synchCall.setValue(true);
					} // end of synchronized(synchCall)

					if (!end)
					{
						try
						{
							timeOut = true;
							asyncGetLineResult = getLineImpl();
							timeOut = false;
						}
						catch(LiveIOProgramExecutionException e)
						{
							getLineThreadException = e;
						}
					}

					synchronized(synchReturn)
					{
						synchReturn.setValue(false);
						synchReturn.notify();
					}
				}
				closeProgramReader();
			}
			catch(Exception e)
			{
				synchException = e;
			}
			
		}
	} // end of private class AsyncGetLineRunnable
	
	
	protected String getLineImpl() throws LiveIOProgramExecutionException
	{
		try
		{
			return this.programReader.readLine();
		}
		catch(IOException e)
		{
			throw new LiveIOProgramExecutionException("getLine failed to programm: "+this.commandAsStringForDebugPurposes,e);
		}
	}
	
	protected void closeProgramReader() throws IOException
	{
		this.programReader.close();
	}
	
	private void createAndStartAsyncThread()
	{
		synchCall = new MutableBoolean(true);
		synchReturn = new MutableBoolean(true);
		
		
		asynchGetLineThread = new Thread(new AsyncGetLineRunnable());
		asynchGetLineThread.start();
		
	}
	
	private void endAsyncThread()
	{
		end = true;
		synchronized(synchCall)
		{
			synchCall.setValue(false);
			synchCall.notify();
		}
	}

	
	////////////////////// PROTECTED AND PRIVATE FIELDS ///////////////////////
	
	protected String[] command;
	protected String commandAsStringForDebugPurposes;
	protected Process process;
	
	protected BufferedReader programReader;
	protected PrintWriter programWriter;
	protected PrintStream programPrintStream;
	protected boolean useStream = false;
	protected Charset charset = null;
	protected boolean treatNullAsError = false;
	protected boolean redirectError = true;
	
	private Thread asynchGetLineThread = null;
	private String asyncGetLineResult = null;
	
	private MutableBoolean synchCall = null;
	private MutableBoolean synchReturn = null;
	
	private Exception getLineThreadException = null;
	private Exception synchException = null;
	private boolean timeOut = false;
	
	
	private boolean end = false;

}
