package eu.excitementproject.eop.common.utilities;

/**
 * The typical usage of {@link AbstractExperimentManager}, for a process that makes a single
 * experiment.
 * <P>
 * This class is a singleton - i.e., only one instance of it exists in the
 * process lifetime. This is the usual way to use {@link AbstractExperimentManager}.
 *  
 * @author Asher Stern
 * @since Dec 10, 2011
 *
 */
public class ExperimentManager extends AbstractExperimentManager
{
	public static ExperimentManager getInstance()
	{
		return instance;
	}
	
	protected ExperimentManager(){super();}
	private static final ExperimentManager instance = new ExperimentManager();
}
