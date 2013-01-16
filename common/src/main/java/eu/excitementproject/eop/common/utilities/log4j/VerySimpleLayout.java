package eu.excitementproject.eop.common.utilities.log4j;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;


/**
 * 
 * @author Asher Stern
 *
 */
public class VerySimpleLayout extends SimpleLayout
{
	public String format(LoggingEvent event)
	{
		stringBuffer.setLength(0);
		stringBuffer.append(event.getLevel().toString()).append(" - ");
		stringBuffer.append(event.getRenderedMessage());
		stringBuffer.append(LINE_SEP);
		return stringBuffer.toString();
	}


	private static final int STRING_BUFFER_INITIAL_LENGTH = 1024;
	private StringBuffer stringBuffer = new StringBuffer(STRING_BUFFER_INITIAL_LENGTH); 

}
