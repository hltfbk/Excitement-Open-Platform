package eu.excitementproject.eop.common.utilities.strings.distance;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Given a string ("original") and a set of strings, and a "number" -
 * this class computes and returns a subset with the most similar strings
 * to the original one. The subset is of size "number".
 * 
 * @author Asher Stern
 *
 */
public class MinimumLevenshteinDistanceOfSetComputer
{
	///////////////////// PUBLIC PART //////////////////////////
	
	
	/**
	 * Construct with the original string and the set of strings
	 * @param originalString
	 * @param set
	 */
	public MinimumLevenshteinDistanceOfSetComputer(String originalString,
			Set<String> set)
	{
		super();
		this.originalString = originalString;
		this.set = set;
	}
	
	
	/**
	 * compute the most similar strings in the set (given in the constructor).
	 * Returns a set of size <code>number</code> that contains those most similar
	 * strings
	 * 
	 * @param number
	 * @return
	 * @throws StringsDistanceException
	 */
	public Set<String> compute(int number) throws StringsDistanceException
	{
		Set<String> ret = new LinkedHashSet<String>();

		if (set!=null)
		{
			StringAndInteger[] array = new StringAndInteger[set.size()]; 
			LevenshteinDistance distanceComputer = new LevenshteinDistance();
			int index=0;
			for (String str : set)
			{
				distanceComputer.setFirstString(str);
				distanceComputer.setSecondString(originalString);
				distanceComputer.setCaseSensitive(false);

				long distance = distanceComputer.computeDistance();
				StringAndInteger stringAndInteger = new StringAndInteger(str, distance);
				array[index] = stringAndInteger;
				index++;
			}
			Arrays.sort(array, new StringAndIntegerComparator());
			
			for (int arrayIndex=0;((arrayIndex<number)&&(arrayIndex<array.length));++arrayIndex)
			{
				ret.add(array[arrayIndex].getString());
			}
		}

		
		return ret;
	}
	
	
	/////////////////////// PRIVATE PART /////////////////////////////
	
	
	// nested classes
	
	private static class StringAndInteger
	{
		public StringAndInteger(String string, long integer)
		{
			super();
			this.string = string;
			this.integer = integer;
		}
		
		public String getString()
		{
			return string;
		}
		public long getInteger()
		{
			return integer;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (integer ^ (integer >>> 32));
			result = prime * result
					+ ((string == null) ? 0 : string.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StringAndInteger other = (StringAndInteger) obj;
			if (integer != other.integer)
				return false;
			if (string == null)
			{
				if (other.string != null)
					return false;
			} else if (!string.equals(other.string))
				return false;
			return true;
		}



		private final String string;
		private final long integer;
	}
	
	private static class StringAndIntegerComparator implements Comparator<StringAndInteger>
	{
		public int compare(StringAndInteger o1, StringAndInteger o2)
		{
			if (o1.getInteger()<o2.getInteger())
				return -1;
			else if (o1.getInteger()==o2.getInteger())
				return 0;
			else
				return 1;
		}
	}
	
	
	
	
	// member fields
	
	private String originalString;
	private Set<String> set;
	

}
