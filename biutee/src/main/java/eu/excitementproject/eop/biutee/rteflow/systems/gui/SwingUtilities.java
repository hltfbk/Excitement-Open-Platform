package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Component;
import java.awt.Frame;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;


/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 27, 2011
 *
 */
public class SwingUtilities
{
	/**
	 * Code from http://stackoverflow.com/questions/1236231/managing-swing-ui-default-font-sizes-without-quaqua
	 * @param fontSize
	 */
	public static void changeDefaultFontSize(int fontSize)
	{
		UIDefaults defaults = UIManager.getDefaults();
		// UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		Enumeration<Object> keys = defaults.keys();
		while (keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			if ((key instanceof String) && (((String) key).endsWith(".font")))
			{
				FontUIResource font = (FontUIResource) UIManager.get(key);
				defaults.put (key, new FontUIResource(font.getFontName(), font.getStyle(), fontSize));
			}
		}
	}
	
	public static void handleError(final Component parentComponent, final Exception ex, boolean withInvokeLater)
	{
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				logger.error("Exception!",ex);
				try
				{
					String exceptionMessages = ExceptionUtil.getMessages(ex);
					JOptionPane.showMessageDialog(parentComponent, "Exception! See log (usually also printed to console)\n"+exceptionMessages,"An error occurred",JOptionPane.ERROR_MESSAGE); 
				}
				catch(Exception xx){}
			}
		};
		
		if (withInvokeLater)
		{
			invokeLater(runnable);
		}
		else
		{
			runnable.run();
		}
		
	}
	

	public static void messageBox(final Component parentComponent, final String message, final String title, boolean withInvokeLater)
	{
		messageBox(parentComponent,message,title,JOptionPane.INFORMATION_MESSAGE,withInvokeLater);
	}
	
	public static void messageBox(final Component parentComponent, final String message, final String title, final int messageType, boolean withInvokeLater)
	{
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				
				JOptionPane.showMessageDialog(parentComponent, message, title, messageType); 
			}
		};
		
		if (withInvokeLater)
		{
			invokeLater(runnable);
		}
		else
		{
			runnable.run();
		}
		
	}

	
	public static void showTextBoxDialog(final Frame parentComponent, final String message, final String title, final boolean wordWrap ,final boolean withInvokeLater)
	{
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				TextBoxDialog<TextBoxDialog.EMPTY_ENUM> dialog =
						new TextBoxDialog<TextBoxDialog.EMPTY_ENUM>(parentComponent,title,true,message);
				dialog.setWordWrap(wordWrap);
				dialog.showMe();
			}
		};
		
		if (withInvokeLater)
		{
			invokeLater(runnable);
		}
		else
		{
			runnable.run();
		}
		
	}


	private static final Logger logger = Logger.getLogger(SwingUtilities.class);
}
