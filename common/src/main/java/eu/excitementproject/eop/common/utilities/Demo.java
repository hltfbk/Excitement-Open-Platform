package eu.excitementproject.eop.common.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Demo
{

	public static void main(String[] args)
	{
		System.out.println("hello world");
		for (int iteration=0;iteration<2;++iteration)
		{
			int[] rp = Utils.randomPermutation(100);
			for (int index=0;index<rp.length;index++)
			{
				System.out.print(String.format("%3d", rp[index])+" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main1(String[] args)
	{
		try
		{
			Map<String,Integer> map = new HashMap<String, Integer>();
			map.put("a", 10);
			map.put("b", 20);
			map.put("c", 15);
			map.put("d", 30);
			map.put("e", 12);
			
			List<String> list = Utils.getSortedByValue(map);
			for (String str : list)
			{
				System.out.println(str);
			}
			System.out.println();
			ListIterator<String> listIterator = list.listIterator(list.size());
			while (listIterator.hasPrevious())
			{
				String str = listIterator.previous();
				System.out.println(str);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
