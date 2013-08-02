package eu.excitementproject.eop.transformations.utilities;

import java.util.LinkedHashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.ExperimentManager;

/**
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 */
public class GlobalMessages
{
	public static final Level DEFAULT_LEVEL = Level.WARN;
	public static final int MAXIMUM_ACCUMULATED_MESSAGE_LENGTH = 5000;
	
	public static final GlobalMessages getInstance()
	{
		return instance;
	}
	
	public void log(Level level, String message)
	{
		if (!maximumAccumulatedLengthExceeded)
		{
			synchronized(this)
			{
				if ((accumulatedLength+message.length()) > MAXIMUM_ACCUMULATED_MESSAGE_LENGTH)
				{
					messages.add(new Message(Level.ERROR, "The maximum accumulated length of global messages has been exceeded."));
					maximumAccumulatedLengthExceeded=true;
				}
				else
				{
					messages.add(new Message(level,message));
					accumulatedLength+=message.length();
				}
			}
		}
	}

	public void error(String message)
	{
		log(Level.ERROR,message);
	}

	public void warn(String message)
	{
		log(Level.WARN,message);
	}

	public void info(String message)
	{
		log(Level.INFO,message);
	}
	
	public void addToLogAndExperimentManager(Logger logger)
	{
		if (messagesLogged())
		{
			String messages = getMessages();
			logger.log(DEFAULT_LEVEL, messages);
			ExperimentManager.getInstance().addMessage(messages);
		}
	}
	
	
	public boolean messagesLoggedAtAll()
	{
		return (messages.size()>0);
	}

	public boolean messagesLogged()
	{
		return messagesLogged(DEFAULT_LEVEL);
	}

	public boolean messagesLogged(Level level)
	{
		if (!messagesLoggedAtAll()) return false;
		
		boolean ret = false;
		for (Message message : messages)
		{
			if (message.getLevel().isGreaterOrEqual(level))
			{
				ret = true;
			}
		}
		return ret;
	}
	
	public String getMessages()
	{
		return getMessages(DEFAULT_LEVEL);
	}
	
	public String getMessages(Level level)
	{
		StringBuilder sb = new StringBuilder();
		LinkedHashSet<Message> theMessages = new LinkedHashSet<Message>();
		theMessages.addAll(messages);
		sb.append("Global messages of level ").append(level.toString()).append(" and above:\n");
		for (Message message : theMessages)
		{
			if (level.isGreaterOrEqual(message.getLevel()))
			{
				sb.append(message.getLevel().toString()).append(" ");
				sb.append(message.getMessage()).append("\n");
			}
		}
		return sb.toString();
	}
	

	
	private GlobalMessages(){}
	
	
	private LinkedHashSet<Message> messages = new LinkedHashSet<>();
	private int accumulatedLength = 0;
	private boolean maximumAccumulatedLengthExceeded=false;
	
	
	private static final GlobalMessages instance = new GlobalMessages();

	private static final class Message
	{
		public Message(Level level, String message)
		{
			super();
			if (null==level)level=Level.ALL;
			if (null==message)message="";
			
			this.level = level;
			this.message = message;
			
		}
		
		
		
		public Level getLevel()
		{
			return level;
		}
		public String getMessage()
		{
			return message;
		}
		
		



		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((message == null) ? 0 : message.hashCode());
			return result;
		}



		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Message other = (Message) obj;
			if (message == null)
			{
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			return true;
		}





		private final Level level;
		private final String message;
	}
}
