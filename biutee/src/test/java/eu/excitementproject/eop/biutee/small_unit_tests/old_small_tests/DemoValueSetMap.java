package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;

public class DemoValueSetMap
{
	public static void f(String[] args)
	{
		ValueSetMap<Integer, Integer> vsm1 = new SimpleValueSetMap<Integer, Integer>();
		vsm1.put(1, 1);
		vsm1.put(1, 2);
		vsm1.put(1, 3);
		vsm1.put(2, 10);
		vsm1.put(2, 11);
		vsm1.put(41, 50);
		vsm1.put(1111, 110);
		
		ValueSetMap<Integer, Integer> vsm2 = new SimpleValueSetMap<Integer, Integer>();
		vsm2.put(1, 100);
		vsm2.put(1, 200);
		vsm2.put(1, 300);
		vsm2.put(10, 400);
		vsm2.put(3, 500);
		vsm2.put(3, 600);
		vsm2.put(50, 1);
		vsm2.put(1, 1);
		vsm2.put(1111, 1);
		
		ValueSetMap<Integer, Integer> vsm3 = DsUtils.compose(vsm1, vsm2);
		for (Integer key : vsm3.keySet())
		{
			for (Integer value : vsm3.get(key))
			{
				System.out.println("mapping: "+key+" to "+value);
			}
		}
		
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			f(args);
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}

}
