package eu.excitementproject.eop.biutee.small_unit_tests;
import java.util.HashMap;
import java.util.Map;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.transformations.datastructures.FastIntegerKeyMap;
import eu.excitementproject.eop.transformations.datastructures.FastIntegerKeyMap.FastIntegerKeyMapException;

public class DemoFastIntegerKeyMap
{

	public static void f(String[] args) throws FastIntegerKeyMapException
	{
		Map<Integer, Double> otherMap = new HashMap<Integer, Double>();
		otherMap.put(3, 1.0);
		otherMap.put(4, 1.0);
		otherMap.put(1, 0.0);
		otherMap.put(66, -10.0);
		otherMap.put(6, -10.0);
		FastIntegerKeyMap<Double> map = new FastIntegerKeyMap<Double>(otherMap, Double.class);
		for (Integer key : map.keySet())
		{
			System.out.println(key.toString()+":"+map.get(key));
		}
		System.out.println(map.get(10));
		System.out.println(map.containsKey(10));
		System.out.println(map.containsKey(1));
		System.out.println(map.containsKey(66));
		System.out.println(map.containsKey(166));
		System.out.println(map.containsKey(-2));
		System.out.println("--------------------");
		System.out.println(map.containsValue(new Object()));
		System.out.println(map.containsValue(10.0));
		System.out.println(map.containsValue(-10.0));
		System.out.println(map.containsValue(66));
		System.out.println("--------------------");
		System.out.println(map.isEmpty());
		System.out.println(map.size());
		System.out.println("--------------------");
		for (Double d : map.values())
		{
			System.out.println(d);
		}
		
		otherMap.put(33,33.0);
		FastIntegerKeyMap<Double> map2 = new FastIntegerKeyMap<Double>(otherMap, Double.class);
		System.out.println(map2.equals(map));
		System.out.println("--------------------");
		for (Map.Entry<Integer, Double> entry : map.entrySet())
		{
			System.out.println("key: "+entry.getKey()+" -> "+entry.getValue());
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
