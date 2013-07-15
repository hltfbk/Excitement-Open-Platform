package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

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
	private static final long serialVersionUID = 71830345251905296L;


	public RtePairsDataset(List<ExtendedPairData> pairsData)
	{
		super();
		
		instances = new ArrayList<>(pairsData.size());
		for (ExtendedPairData pairData : pairsData)
		{
			instances.add(new THPairInstance(pairData));
		}
		
	}
	@Override
	public List<THPairInstance> getListOfInstances() throws BiuteeException
	{
		return this.instances;
	}

	
	private final List<THPairInstance> instances;
}
