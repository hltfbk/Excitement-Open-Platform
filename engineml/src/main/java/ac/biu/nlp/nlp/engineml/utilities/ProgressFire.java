package ac.biu.nlp.nlp.engineml.utilities;
import ac.biu.nlp.nlp.engineml.rteflow.macro.AbstractTextTreesProcessor;

/**
 * Used by GUI as a mechanism for sending signals about
 * a progress of some mechanism.
 * 
 * @see AbstractTextTreesProcessor#setProgressFire(ProgressFire)
 * @author Asher Stern
 * @since May 13, 2012
 *
 */
public interface ProgressFire
{
	/**
	 * must be in [0,1]
	 * @param percentage
	 */
	public void fire(double percentage);
}
