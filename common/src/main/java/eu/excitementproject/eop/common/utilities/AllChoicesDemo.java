package eu.excitementproject.eop.common.utilities;

import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.AllChoices.AllChoicesException;


/**
 * Demo for {@link AllChoices}.
 * @author Asher Stern
 *
 */
public class AllChoicesDemo
{
	
	@SuppressWarnings("unchecked")
	public void f() throws AllChoicesException
	{
		List<String> l1 = new LinkedList<String>();
		List<String> l2 = new LinkedList<String>();
		List<String> l3 = new LinkedList<String>();
		
		l1.add("a"); l1.add("b"); l1.add("c");
		l2.add("1"); l2.add("2"); l2.add("3");
		l3.add("!"); l3.add("@"); l3.add("#");
		
		Iterable<String>[] iterablesArray = new Iterable[3];
		iterablesArray[0] = l1;
		iterablesArray[1] = l2;
		iterablesArray[2] = l3;
		
		ChoiceHandler<String> handler = new ChoiceHandler<String>()
		{
			public void handleChoice(List<String> choice)
			{
				for (String s : choice)
				{
					System.out.print(s+" ");
				}
				System.out.println();
				
			}
		};
		
		AllChoices<String> ac = new AllChoices<String>(iterablesArray, handler);
		ac.run();
		
		
		
	}

	public static void main(String[] args)
	{
		try
		{
			AllChoicesDemo demo = new AllChoicesDemo();
			demo.f();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}

}
