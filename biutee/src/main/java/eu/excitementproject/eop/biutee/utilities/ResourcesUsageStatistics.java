package eu.excitementproject.eop.biutee.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultProof;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;

/**
 * A small stand-alone application to calculate how frequently knowledge-resources were used in training data.
 * <BR>
 * This class uses RTTI. It is not a recommended coding-practice. However, since this is a stand alone application
 * I do not change the code to eliminate this RTTI.
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
				ImmutableList<? extends InstanceAndProof<? extends Instance, ? extends DefaultProof>> proofs =
				(ImmutableList<InstanceAndProof<? extends Instance, ? extends DefaultProof>>)serStream.readObject();
//				Results<? extends Instance, ? extends DefaultProof> results = (Results<? extends Instance, ? extends DefaultProof>)serStream.readObject();
				ResourcesUsageStatistics<? extends Instance, ? extends DefaultProof> rus = new ResourcesUsageStatistics<>(proofs);
				rus.go();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}
	
	
	
	public ResourcesUsageStatistics(ImmutableList<? extends InstanceAndProof<? extends I, ? extends P>> proofs)
	{
		super();
		this.proofs = proofs;
	}
	
	


	public void go() throws BiuteeException
	{
		final Boolean TRUE = true;
		final Boolean FALSE = false;
		Map<String, Integer> all = new LinkedHashMap<>();
		Map<String, Integer> positives = new LinkedHashMap<>();
		Map<String, Integer> negatives = new LinkedHashMap<>();
		boolean thereIsNullLabel = false;
		for (InstanceAndProof<? extends I, ? extends P> proof : proofs)
		{
			Map<String, Integer> singlePairMap = usageMapInSingleProof(proof.getProof().getHistory());
			updateUsageMapBySingleProof(all,singlePairMap);
			Boolean label = proof.getInstance().getBinaryLabel();
			if (label.equals(TRUE))
			{
				updateUsageMapBySingleProof(positives,singlePairMap);
			}
			else if (label.equals(FALSE))
			{
				updateUsageMapBySingleProof(negatives,singlePairMap);
			}
			else
			{
				thereIsNullLabel = true;
			}
		}
		
		if (thereIsNullLabel)
		{
			System.out.println("There is a null label.");
		}
		
		System.out.println("Usage in all pairs");
		printMap(all);
		System.out.println("Usage in positive pairs");
		printMap(positives);
		System.out.println("Usage in negative pairs");
		printMap(negatives);
	}
	
	private void printMap(Map<String, Integer> map)
	{
		for (String ruleBaseName : map.keySet())
		{
			System.out.println(ruleBaseName+": "+map.get(ruleBaseName));
		}
	}
	
	private void updateUsageMapBySingleProof(Map<String, Integer> map, Map<String, Integer> singlePairMap)
	{
		for (String ruleBaseName : singlePairMap.keySet())
		{
			Integer current = map.get(ruleBaseName);
			int intCurrent = 0;
			if (current!=null){intCurrent = current.intValue();}
			map.put(ruleBaseName,intCurrent+singlePairMap.get(ruleBaseName));
		}
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


	private final ImmutableList<? extends InstanceAndProof<? extends I, ? extends P>> proofs;

}
