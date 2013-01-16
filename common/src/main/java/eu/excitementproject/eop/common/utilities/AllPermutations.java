package eu.excitementproject.eop.common.utilities;


/**
 * Please run {@link AllPermutationsDemo} to understand this class.
 * @author Asher Stern
 *
 */
public final class AllPermutations
{
	/////////////////// PUBLIC PART //////////////////////////////
	
	/////////////// PUBLIC NESTED EXCEPTION CLASS ////////////////
	@SuppressWarnings("serial")
	public static class AllPermutationsException extends Exception
	{
		public AllPermutationsException(String message)
		{
			super(message);
		}
	}
	//////////////////////////////////////////////////////////////
	
	
	/////////// PUBLIC CONSTRUCTORS AND METHODS ///////////////////
	
	public AllPermutations(int fromLength, int toLength) throws AllPermutationsException
	{
		if (toLength<1) throw new AllPermutationsException("toLength<1");
		if (fromLength<1) throw new AllPermutationsException("fromLength<1");
		if (toLength<fromLength) throw new AllPermutationsException("toLength<fromLength");
		this.fromLength = fromLength;
		this.toLength = toLength;
		used = new boolean[toLength];
		init();
	}
	
	public int[] getResult()
	{
		return result;
	}
	
	public boolean next()
	{
		int index = fromLength-1;
		used[result[index]] = false;
		while (result[index]==maxNotUsed())
		{
			index--;
			if (index>=0)
				used[result[index]] = false;
			else
				break;
		}
		if (index==(-1))
			return false;
		
		
		int nextVal = minimumNotUsed(result[index]+1);
		used[result[index]] = false;
		used[nextVal] = true;
		result[index] = nextVal;
		index++;
		nextVal = 0;
		for (int indexindex=index;indexindex<fromLength;indexindex++)
		{
			nextVal = minimumNotUsed(nextVal);
			used[nextVal] = true;
			result[indexindex] = nextVal;
		}
		return true;
	}
	
	
	
	
	/////////////////////// PRIVATE PART ////////////////////////////
	
	private int minimumNotUsed(int from)
	{
		int ret = -1;
		for (int index=from;index<used.length;index++)
		{
			if (used[index]==false)
			{
				ret = index;
				break; // I hate break.
			}
		}
		return ret;
	}
	
	private int maxNotUsed()
	{
		int ret = toLength;
		for (int index=toLength-1;index>=0;index--)
		{
			if (used[index]==false)
			{
				ret = index;
				break;
			}
		}
		return ret;
	}
	
	private void init()
	{
		result = new int[fromLength];
		for (int index=0;index<fromLength;index++)
		{
			result[index] = index;
			used[index] = true;
		}
	}
	

	
	private int[] result;
	private int fromLength;
	private int toLength;
	
	private boolean[] used;
	

}

