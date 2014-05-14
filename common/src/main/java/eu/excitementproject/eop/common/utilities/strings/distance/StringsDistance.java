package eu.excitementproject.eop.common.utilities.strings.distance;

/**
 * An interface for string distantce implementations.
 * A string distance may be Levenshtein distance and any
 * other distance by any other metric.
 * @author Asher Stern
 *
 */
public interface StringsDistance
{
	public void setFirstString(String str);
	public void setSecondString(String str);

	/**
	 * Sets whether the distance is computed in case sensitive computation.
	 * @param isCaseSensitive <code> true </code> if two character
	 * that have different case are treated as different characters.
	 * I.e. "A" is not equal "a".
	 * <code> false </code> otherwise (i.e. "A" is equal to "a").
	 */
	public void setCaseSensitive(boolean isCaseSensitive);
	
	public long computeDistance() throws StringsDistanceException;
}
