package eu.excitementproject.eop.biutee.utilities;

/**
 * Used by GUI to print status in status-bar.
 * 
 * Within GUI ("tracing tool") it is used to write messages to the status bar.
 * An implementation for non-GUI application is {@link DoNothingShortMessageFire}, which
 * actually does nothing (the message is not fired, not printed, etc.).
 * 
 * @author Asher Stern
 * @since Dec 7, 2011
 *
 */
public interface ShortMessageFire
{
	public void fire(String message);

}
