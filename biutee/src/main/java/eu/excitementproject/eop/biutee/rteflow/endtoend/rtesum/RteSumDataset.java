package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.CandidateIdentifier;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 21, 2013
 *
 */
public class RteSumDataset extends Dataset<RteSumInstance>
{
	private static final long serialVersionUID = -7881274000124305949L;
	
	public RteSumDataset(RteSumDatasetContents datasetContents, List<CandidateIdentifier> candidates) throws BiuteeException
	{
		super();
		this.datasetContents = datasetContents;
		this.candidates = candidates;
		
		createInstances();
	}
	
	@Override
	public List<RteSumInstance> getListOfInstances() throws BiuteeException
	{
		return instances;
	}
	
	private synchronized void createInstances() throws BiuteeException
	{
		instances = new ArrayList<>(candidates.size());
		for (CandidateIdentifier candidate : candidates)
		{
			instances.add(new RteSumInstance(candidate,datasetContents));
		}
	}
	
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		try
		{
			createInstances();
		}
		catch (BiuteeException e)
		{
			// I can't throw a checked exception here.
			throw new RuntimeException("Deserialization failed. See nested exception.",e);
		}
	}


	private final RteSumDatasetContents datasetContents;
	private final List<CandidateIdentifier> candidates;
	private transient List<RteSumInstance> instances = null;
}
