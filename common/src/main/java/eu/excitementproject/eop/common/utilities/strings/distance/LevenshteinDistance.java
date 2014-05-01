package eu.excitementproject.eop.common.utilities.strings.distance;

/**
 * Computes LevenshteinDistance.
 * The code was copied from http://www.merriampark.com/ld.htm,
 * with some modifications.
 * @author Asher Stern
 *
 */
public class LevenshteinDistance implements StringsDistance
{
	/////////////////// PROTECTED METHODS ////////////////////
	
	protected long minOfThree(long a, long b, long c)
	{
		long minimum;
		minimum = a;
		if (b < minimum)
		{
			minimum = b;
		}
		if (c < minimum)
		{
			minimum = c;
		}
		return minimum;
	}
	
	
	/**
	 * Algorithm:
	 * <BR>
	 * (terms: s = firstString. t = secondString)<BR>
	 * 1<BR>
	 * Set n to be the length of s.<BR>
	 * Set m to be the length of t.<BR>
	 * If n = 0, return m and exit.<BR>
	 * If m = 0, return n and exit.<BR>
	 * Construct a matrix containing 0..m rows and 0..n columns.<BR>
	 * 2<BR>
	 * Initialize the first row to 0..n.<BR>
	 * Initialize the first column to 0..m.<BR>
	 * 3<BR>
	 * Examine each character of s (i from 1 to n)<BR>
	 * 4<BR>
	 * Examine each character of t (j from 1 to m)<BR>
	 * 5<BR>
	 * If s[i] equals t[j], the cost is 0.<BR>
	 * If s[i] doesn't equal t[j], the cost is 1.<BR>
	 * 6<BR>
	 * Set cell d[i,j] of the matrix equal to the minimum of:<BR>
	 * a. The cell immediately above plus 1: d[i-1,j] + 1.<BR>
	 * b. The cell immediately to the left plus 1: d[i,j-1] + 1.<BR>
	 * c. The cell diagonally above and to the left plus the cost: d[i-1,j-1] + cost.<BR>
	 * 7<BR>
	 * After the iteration steps (3, 4, 5, 6) are complete, the distance is found in cell d[n,m].<BR>
	 *  
	 * @return the distance
	 * @throws StringsDistanceException illegal strings
	 */
	protected long computeDistanceImpl() throws StringsDistanceException
	{
		if (firstString==null)
			throw new StringsDistanceException("first string is null. Did you forget to call setFirstString()?");
		if (secondString==null)
			throw new StringsDistanceException("second string is null. Did you forget to call setSecondString()?");
		
		int firstStringLength = firstString.length();
		int secondStringLength = secondString.length();

		long matrix[][]; // matrix

		// Step 1

		if (firstStringLength == 0) {
			return secondStringLength;
		}
		if (secondStringLength == 0) {
			return firstStringLength;
		}
		matrix = new long[firstStringLength+1][secondStringLength+1];

		// Step 2

		for (int firstStringIndex = 0; firstStringIndex <= firstStringLength; firstStringIndex++) {
			matrix[firstStringIndex][0] = firstStringIndex;
		}

		for (int secondStringIndex = 0; secondStringIndex <= secondStringLength; secondStringIndex++) {
			matrix[0][secondStringIndex] = secondStringIndex;
		}

		// Step 3

		for (int firstStringIndex = 1; firstStringIndex <= firstStringLength; firstStringIndex++)
		{

			char firstStringChar = firstString.charAt (firstStringIndex - 1);

			// Step 4

			for (int secondStringIndex = 1; secondStringIndex <= secondStringLength; secondStringIndex++) {

				char secondStringChar = secondString.charAt (secondStringIndex - 1);
				
				long cost = 0;

				// Step 5

				if (firstStringChar == secondStringChar) {
					cost = 0;
				}
				else {
					cost = 1;
				}

				// Step 6

				matrix[firstStringIndex][secondStringIndex] = minOfThree(matrix[firstStringIndex-1][secondStringIndex]+1, matrix[firstStringIndex][secondStringIndex-1]+1, matrix[firstStringIndex-1][secondStringIndex-1] + cost);

			}

		}

		// Step 7

		return matrix[firstStringLength][secondStringLength];
	}
	
	

	
	
	
	/////////////////// PUBLIC PART /////////////////////////
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.strings.distance.StringsDistance#setFirstString(java.lang.String)
	 */
	public void setFirstString(String str)
	{
		this.originalFirstString = str;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.strings.distance.StringsDistance#setSecondString(java.lang.String)
	 */
	public void setSecondString(String str)
	{
		this.originalSecondString = str;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.strings.distance.StringsDistance#setCaseSensitive(boolean)
	 */
	public void setCaseSensitive(boolean isCaseSensitive)
	{
		this.caseSensitive = isCaseSensitive;
 	}


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.strings.distance.StringsDistance#computeDistance()
	 */
	public long computeDistance() throws StringsDistanceException
	{
		if (caseSensitive)
		{
			this.firstString = originalFirstString;
			this.secondString = originalSecondString;
		}
		else
		{
			this.firstString = originalFirstString.toLowerCase();
			this.secondString = originalSecondString.toLowerCase();
		}
		return computeDistanceImpl();
	}

	
	
	//////////////// PROTECTED FIELDS /////////////////////
	protected String originalFirstString;
	protected String originalSecondString;
	protected boolean caseSensitive = true;
	
	protected String firstString;
	protected String secondString;




}
