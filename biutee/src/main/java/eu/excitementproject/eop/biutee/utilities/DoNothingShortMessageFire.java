package eu.excitementproject.eop.biutee.utilities;

/**
 * A {@link ShortMessageFire} which does nothing with the message to print. I.e.,
 * calling {@link #fire(String)} does nothing.
 * This {@linkplain ShortMessageFire} is used when a class uses {@link ShortMessageFire}
 * for GUI, but it is not necessary for the normal flow (i.e., non-GUI).
 * 
 * @author Asher Stern
 * @since Dec 7, 2011
 *
 */
public class DoNothingShortMessageFire implements ShortMessageFire
{
	public void fire(String message)
	{}
}
