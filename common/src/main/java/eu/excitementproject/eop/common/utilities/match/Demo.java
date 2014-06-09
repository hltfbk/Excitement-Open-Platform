package eu.excitementproject.eop.common.utilities.match;

import java.util.LinkedList;


/**
 * Demo for {@link Matcher}
 * @author Asher Stern
 *
 */
public class Demo
{

	public static void main(String[] args)
	{
		LinkedList<Integer> l1 = new LinkedList<Integer>();
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		
		l1.add(1);
		//l1.add(2);
		l1.add(3);
		l1.add(2);
		l1.add(4);
		l1.add(100);
		l1.add(101);
		l1.add(103);
		l1.add(102);
		
		l2.add(2);
		l2.add(15);
		l2.add(4);
		l2.add(6);
		l2.add(8);
		l2.add(200);
		l2.add(202);
		l2.add(204);
		
		
		/*
		SimpleDuplicateableIterator<Integer> di = new SimpleDuplicateableIterator<Integer>(l1.iterator());
		DuplicateableIterator<Integer> di2 = di.duplicate();
		System.out.println(di.next());
		System.out.println(di.next());
		System.out.println(di2.next());
		System.out.println(di2.next());
		System.out.println(di2.next());
		System.out.println(di.next());
		System.out.println(di.next());
		System.out.println(di.hasNext());
		System.out.println(di2.hasNext());
		
		
		System.out.println("****");
		DuplicateableIterator<Integer> di3 = di2.duplicate();
		System.out.println(di3.next());
		System.out.println(di2.hasNext());
		System.out.println(di3.hasNext());
		System.out.println(di2.next());
		*/
		
		
		MatchFinder<Integer,Integer> finder = new MatchFinder<Integer, Integer>()
		{

			public boolean areMatch()
			{
				return rhs.intValue()==(2*lhs.intValue());
			}

			public void set(Integer lhs, Integer rhs)
			{
				this.lhs = lhs;
				this.rhs = rhs;
				
			}
			
			private Integer lhs;
			private Integer rhs;
			
		};
		
		Operator<Integer,Integer> operator = new Operator<Integer, Integer>()
		{

			public void makeOperation()
			{
				System.out.println(lhs.intValue()+" "+rhs.intValue());
			}

			public void set(Integer lhs, Integer rhs)
			{
				this.lhs = lhs;
				this.rhs = rhs;
				
			}
			
			private Integer lhs;
			private Integer rhs;
			
		};
		
		Matcher<Integer, Integer> matcher = new Matcher<Integer, Integer>(l1.iterator(), l2.iterator(), finder, operator);
		matcher.makeMatchOperation();
		
		
		

	}

}

