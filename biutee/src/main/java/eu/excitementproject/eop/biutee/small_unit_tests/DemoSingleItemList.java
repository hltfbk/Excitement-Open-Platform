package eu.excitementproject.eop.biutee.small_unit_tests;
import java.util.ListIterator;

import eu.excitementproject.eop.transformations.datastructures.SingleItemList;


public class DemoSingleItemList
{
	public static void main(String[] args)
	{
		try
		{
			SingleItemList<Integer> list = new SingleItemList<Integer>(1);
			for (Integer i : list)
			{
				System.out.println(i);
			}
			if (list.contains(2))
				System.out.println("bad");
			else
				System.out.println("good");
			
			ListIterator<Integer> lit = list.listIterator();
			System.out.println("good");
			lit.next();
			System.out.println("good");
			try{lit.next(); System.out.println("bad");}catch(Exception e){System.out.println("good");}
			
			lit = list.listIterator();
			if (lit.hasNext())
				System.out.println("good");
			else
				System.out.println("bad");
			if (lit.hasPrevious())
				System.out.println("bad");
			else
				System.out.println("good");

			System.out.println(lit.next());
			if (lit.hasNext())
				System.out.println("bad");
			else
				System.out.println("good");
			if (lit.hasPrevious())
				System.out.println("good");
			else
				System.out.println("bad");
				
			System.out.println(lit.previous());
			if (lit.hasNext())
				System.out.println("good");
			else
				System.out.println("bad");
			if (lit.hasPrevious())
				System.out.println("bad");
			else
				System.out.println("good");
			
			System.out.println(list.isEmpty());
			System.out.println(list.size());

			
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
