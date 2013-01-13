package ac.biu.nlp.nlp.engineml.rteflow.macro.search;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TextTreesProcessor;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;

/**
 * Extends the interface {@link TextTreesProcessor} with methods about
 * run-time statistics.
 * 
 * @author Asher Stern
 * 
 *
 */
public interface WithStatisticsTextTreesProcessor extends TextTreesProcessor
{
	/**
	 * Returns number of elements that were expanded (i.e. the system
	 * generated children for them)
	 * 
	 * @return
	 * @throws TeEngineMlException
	 */
	public long getNumberOfExpandedElements() throws TeEngineMlException;
	
	/**
	 * Returns number of elements that were generated (created) by the
	 * system.
	 * 
	 * @return
	 * @throws TeEngineMlException
	 */
	public long getNumberOfGeneratedElements() throws TeEngineMlException;

}
