package eu.excitementproject.eop.biutee.rteflow.macro.search;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
