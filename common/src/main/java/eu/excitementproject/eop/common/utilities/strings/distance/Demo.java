package eu.excitementproject.eop.common.utilities.strings.distance;

import java.util.HashSet;
import java.util.Set;


/**
 * Demo for {@link LevenshteinDistance}.
 * 
 * @author Asher Stern
 *
 */
public class Demo {

	public static void main(String[] args)
	{
		try
		{
			Set<String> set = new HashSet<String>();
			set.add("mystring");
			set.add("MYSTRING");
			set.add("string");
			set.add("integer");
			set.add("strinteger");
			MinimumLevenshteinDistanceOfSetComputer mldosc = new MinimumLevenshteinDistanceOfSetComputer("mystring", set);
			Set<String> newSet = mldosc.compute(4);
			for (String s : newSet)
			{
				System.out.println(s);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * @param args
	 */
	public static void mainold(String[] args)
	{
		try
		{
			LevenshteinDistance distance = new LevenshteinDistance();
			distance.setFirstString("abA");
			distance.setSecondString("bba");
			distance.setCaseSensitive(false);
			System.out.println(distance.computeDistance());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		


	}

}
