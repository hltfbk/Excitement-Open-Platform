package eu.excitementproject.eop.common.utilities.log4j;


import java.util.LinkedList;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A log4j appender that prints the log printouts into a {@link JTextArea}.
 * 
 * @author Asher Stern
 * @since 23 August 2012
 *
 */
public class GuiAppender extends AppenderSkeleton
{
	public static final int DEFAULT_LINES = 5;
	
	/**
	 * Constructor with the {@link JTextArea} into which the log printouts will be printed.
	 * 
	 * The number of lines to be printed is {@value DEFAULT_LINES} (older lines are removed)
	 * 
	 * @param textArea
	 */
	public GuiAppender(JTextArea textArea)
	{
		this(textArea,DEFAULT_LINES);
	}
	
	/**
	 * Constructor with the {@link JTextArea} into which the log printouts will be printed, and with number of lines
	 * to be viewed (older lines will be deleted).
	 * 
	 * @param textArea
	 * @param lines
	 */
	public GuiAppender(JTextArea textArea, int lines)
	{
		super();
		this.textArea = textArea;
		this.layout = new SimpleLayout();
		events = new LinkedList<String>();
		for (int i=0;i<lines;++i)
		{
			events.add("\n");
		}
	}

	@Override
	public void close()
	{
	}

	@Override
	public boolean requiresLayout()
	{
		return false;
	}

	@Override
	protected void append(LoggingEvent arg0)
	{
		String str = layout.format(arg0);
		events.removeFirst();
		events.add(str);
		
		textArea.setText(eventsToString());
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	private String eventsToString()
	{
		StringBuilder sb = new StringBuilder();
		for (String str : events)
		{
			sb.append(str);
		}
		return sb.toString();
	}

	private LinkedList<String> events;
	
	private JTextArea textArea;
}
