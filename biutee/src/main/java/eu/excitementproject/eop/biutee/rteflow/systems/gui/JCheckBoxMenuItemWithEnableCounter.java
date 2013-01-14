package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import javax.swing.JCheckBoxMenuItem;

/**
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and should be
 * improved. Need to re-design, make it more modular, adding documentation and
 * improve code.
 * 
 * @author Asher Stern
 * @since Apr 16, 2012
 *
 */
public class JCheckBoxMenuItemWithEnableCounter extends JCheckBoxMenuItem 
{
	private static final long serialVersionUID = 5498884031183542185L;
	
	public JCheckBoxMenuItemWithEnableCounter(String text)
	{
		super(text);
	}

	public synchronized void incEnableCount()
	{
		this.enableCount++;
		setTheEnabled();
	}

	public synchronized void decEnableCount()
	{
		this.enableCount--;
		setTheEnabled();
	}
	
	private synchronized void setTheEnabled()
	{
		if (enableCount<=0)
		{
			setEnabled(true);
		}
		else
		{
			setEnabled(false);
		}
	}

	private int enableCount = 0;
}
