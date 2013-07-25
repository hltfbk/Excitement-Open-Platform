package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class RtePairsDataset extends Dataset<THPairInstance>
{
	private static final long serialVersionUID = -3596691497099928859L;
	
	public RtePairsDataset(List<ExtendedPairData> pairsData)
	{
		super();
		this.pairsData = pairsData;
		createInstances();
	}
	
	@Override
	public List<THPairInstance> getListOfInstances() throws BiuteeException
	{
		return this.instances;
	}
	
	
	
	private synchronized void createInstances()
	{
		instances = new ArrayList<>(pairsData.size());
		for (ExtendedPairData pairData : pairsData)
		{
			instances.add(new THPairInstance(pairData));
		}
	}


	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		createInstances();
	}

	private final List<ExtendedPairData> pairsData;
	private transient List<THPairInstance> instances;
}
