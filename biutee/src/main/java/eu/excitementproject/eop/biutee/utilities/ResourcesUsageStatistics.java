package eu.excitementproject.eop.biutee.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultProof;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;

/**
 * 
 * @author Asher Stern
 * @since Jan 12, 2014
 *
 * @param <I>
 * @param <P>
 */
public class ResourcesUsageStatistics<I extends Instance, P extends DefaultProof>
{

	public static void main(String[] args)
	{
		try
		{
			File serFile = new File(args[0]);
			try(ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(serFile)))
			{
				@SuppressWarnings("unchecked")
				Results<? extends Instance, ? extends DefaultProof> results = (Results<? extends Instance, ? extends DefaultProof>)serStream.readObject();
				ResourcesUsageStatistics<? extends Instance, ? extends DefaultProof> rus = new ResourcesUsageStatistics<>(results.getProofs());
				Map<String, Integer> map = rus.usageMapAllProofs();
				for (String ruleBaseName : map.keySet())
				{
					System.out.println(ruleBaseName+": "+map.get(ruleBaseName));
				}
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}
	
	
	public ResourcesUsageStatistics(ImmutableList<InstanceAndProof<I, P>> proofs)
	{
		super();
		this.proofs = proofs;
	}
	
	


	public Map<String, Integer> usageMapAllProofs()
	{
		Map<String, Integer> ret = new LinkedHashMap<>();
		for (InstanceAndProof<I, P> proof : proofs)
		{
			Map<String, Integer> usageMap = usageMapInSingleProof(proof.getProof().getHistory());
			for (String ruleBaseName : usageMap.keySet())
			{
				Integer current = ret.get(ruleBaseName);
				int intCurrent = 0;
				if (current!=null){intCurrent = current.intValue();}
				ret.put(ruleBaseName,intCurrent+usageMap.get(ruleBaseName));
			}
		}
		return ret;
	}
	
	private Map<String, Integer> usageMapInSingleProof(TreeHistory history)
	{
		Map<String, Integer> ret = new LinkedHashMap<>();
		for (Specification specification : history.getSpecifications())
		{
			String ruleBaseName = null;
			if (specification instanceof RuleSpecification)
			{
				ruleBaseName = ((RuleSpecification)specification).getRuleBaseName();
			}
			else if (specification instanceof RuleSubstituteNodeSpecification<?>)
			{
				ruleBaseName = ((RuleSubstituteNodeSpecification<?>)specification).getRuleBaseName();
			}
			if (ruleBaseName!=null)
			{
				Integer current = ret.get(ruleBaseName);
				int intCurrent = 0;
				if (current!=null){intCurrent=current.intValue();}
				ret.put(ruleBaseName, intCurrent+1);
			}
		}
		
		return ret;
	}


	private final ImmutableList<InstanceAndProof<I, P>> proofs;

}
