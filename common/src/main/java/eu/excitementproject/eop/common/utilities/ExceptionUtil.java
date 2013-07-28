package eu.excitementproject.eop.common.utilities;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;


/**
 * Collections of utilities (public static methods) for
 * handling exceptions.
 * 
 * @author Asher Stern
 *
 */
public class ExceptionUtil
{
	/**
	 * Returns the exception's message plug all nested
	 * exceptions messages.
	 * @param throwable an Exception (or Error)
	 * 
	 * @return The exception's message plug all nested
	 * exceptions messages.
	 */
	public static String getMessages (Throwable throwable)
	{
		StringBuffer buffer = new StringBuffer();
		while (throwable != null)
		{
			if (throwable.getMessage()!=null)
			{
				buffer.append(throwable.getClass().getSimpleName() + ": " + throwable.getMessage()+"\n");
			}
			throwable = throwable.getCause();
		}
		return buffer.toString();
	}
	
	/**
	 * Prints a string that describes the given exception
	 * into the given PrintStream
	 * 
	 * @param throwable
	 * @param printStream
	 */
	public static void outputException(Throwable throwable, PrintStream printStream)
	{
		throwable.printStackTrace(printStream);
		printStream.println();
		printStream.println(ExceptionUtil.getMessages(throwable));
	}
	
	/**
	 * Prints a string that describes the given exception
	 * into the given PrintWriter
	 * 
	 * @param throwable
	 * @param printWriter
	 */
	public static void outputException(Throwable throwable, PrintWriter printWriter)
	{
		throwable.printStackTrace(printWriter);
		printWriter.println();
		printWriter.println(ExceptionUtil.getMessages(throwable));
	}
	
	public static void logException(Throwable throwable, Logger logger)
	{
		logger.error("Exception/Error:\n",throwable);
		logger.error(ExceptionUtil.getMessages(throwable));
	}
	
	public static String getStackTrace(Throwable t)
	{
		StringWriter stringWriter = new StringWriter();
		t.printStackTrace(new PrintWriter(stringWriter));
		String ret = stringWriter.toString();
		try{stringWriter.close();}catch(IOException e){}
		return ret;
	}
	

}
