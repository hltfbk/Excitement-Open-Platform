package eu.excitementproject.eop.biutee.rteflow.macro;
import java.io.Serializable;

/**
 * Information about text-hypothesis pair that is not driven
 * from the proof (of the text from the hypothesis).
 * For example, the task-name ("IR", "QA", etc.) from which
 * that pair was created.
 * 
 * @author Asher Stern
 * @since Aug 6, 2011
 *
 */
public class GlobalPairInformation implements Serializable
{
	private static final long serialVersionUID = 6257998479230358656L;
	
	public GlobalPairInformation(String taskName, String datasetName)
	{
		super();
		this.taskName = taskName;
		this.datasetName = datasetName;
	}

	public GlobalPairInformation(String taskName)
	{
		this(taskName,null);
	}
	

	public String getTaskName()
	{
		return taskName;
	}
	
	public String getDatasetName()
	{
		return datasetName;
	}
	
	@Override
	public String toString()
	{
		return "GlobalPairInformation: taskName = "+getTaskName()+", datasetName = "+getDatasetName();
	}



	protected final String taskName;
	protected final String datasetName;
}
