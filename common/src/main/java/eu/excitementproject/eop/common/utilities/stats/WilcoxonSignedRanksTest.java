package eu.excitementproject.eop.common.utilities.stats;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.file.FileUtils;


/**
 * Calc WilcoxonSignedRanks and other related statistics on two lists of doubles
 * <p>
 * Based on http://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test.
 * 
 * @author Idan Spector
 * @since 2 Jan 2010
 * 
 */
public class WilcoxonSignedRanksTest
{	
	//////////////////////////////////////////////////////// public /////////////////////////////////////////////////////////////
	
	/**
	 * Constructor
	 * <br>
	 * Sum up negativeSum and positiveSum over the differences between {@code leftList} and {@code rightList}
	 * 
	 * @param leftList
	 * @param rightList
	 * @throws StatsException 
	 */
	public WilcoxonSignedRanksTest(List<Double> leftList, List<Double> rightList) throws StatsException
	{
		if (leftList == null || rightList == null)
			throw new StatsException("Got null input");
		if (leftList.size() != rightList.size())
			throw new StatsException("The two lists must have the same size");
		
		sampleSize = leftList.size();		
		nonZeroDiffs = 0;	
		List<Double> diffs = new LinkedList<Double>();					
		Iterator<Double> leftIter = leftList.iterator();
		Iterator<Double> rightIter = rightList.iterator();
		
		// fill up diffs
		while(leftIter.hasNext())
		{
			double left = leftIter.next();
			double right = rightIter.next();

			if(left != right){
				diffs.add(left - right);
				nonZeroDiffs++;
			}
		}
		
		Collections.sort(diffs, new AbsComparator());
		Double[] orderedDiffs = new Double[diffs.size()];
		orderedDiffs = diffs.toArray(new Double[0]);
		double[] ranks = new double[orderedDiffs.length];
				
		// sum up negativeSum and positiveSum over all diffs
		negativeSum = positiveSum = 0;
		for(int i = 0; i < orderedDiffs.length; i++)
		{
			int j = i + 1;
			while(j < orderedDiffs.length && (Math.abs(orderedDiffs[j]) == Math.abs(orderedDiffs[i]) ))
				j++;
			
			int ndxDistance = j - i;

			double rank;
			if(ndxDistance > 1)
				rank = i + (ndxDistance+1)/2.0;
			else
				rank = i + 1;
			
			for(int k = 0; k < ndxDistance; k++)
			{
				ranks[i+k] = rank;
				
				if(orderedDiffs[i+k] > 0)
					positiveSum += rank;
				else
					negativeSum += rank;
			}
			
			i += ndxDistance - 1;
		}
	}

	/**
	 * @return minSum
	 */
	public double minSum()
	{
			return Math.min(negativeSum, positiveSum);
	}
	
	/**
	 * @return "RIGHT" or "LEFT"
	 */
	public String getMaxDesc()
	{
		return (negativeSum > positiveSum ? "RIGHT" : "LEFT");
	}
	
	/**
	 * @return sampleSize
	 */
	public int sampleSize()
	{
		return sampleSize;
	}
		
	/**
	 * @return no Of Non Zero Differences
	 */
	public int noOfNonZeroDifferences()
	{
		return nonZeroDiffs;
	}
	
	/**
	 * @return bestOneSideConfidenceLevel
	 */
	public double bestOneSideConfidenceLevel()
	{
		return ONE_SIDE_CRITICAL_VALUES[nonZeroDiffs].bestConfidenceLevelForValue(minSum());
	}
	
	/**
	 * @return bestTwoSideConfidenceLevel
	 */
	public double bestTwoSideConfidenceLevel()
	{
		System.out.println(negativeSum+"\n"+positiveSum+"\n"+nonZeroDiffs+"\n"+sampleSize);
		return TWO_SIDES_CRITICAL_VALUES[nonZeroDiffs].bestConfidenceLevelForValue(minSum());
	}
	
	///////////////////////////////////////////////// public static //////////////////////////////////////////////////////////////////
	
	public final static ConfidenceCriticalValues[] ONE_SIDE_CRITICAL_VALUES;
	public final static ConfidenceCriticalValues[] TWO_SIDES_CRITICAL_VALUES;
	
	public static class ConfidenceCriticalValues
	{
		public ConfidenceCriticalValues(double p01, double p005, double p001, int n)
		{
			p01CriticalValue = p01;
			p005CriticalValue = p005;
			p001CriticalValue = p001;
			this.n = n;
		}
				
		public double bestConfidenceLevelForValue(double iValue)
		{
			if(iValue <= p001CriticalValue)
				return 0.01;

			if(iValue <= p005CriticalValue)
				return 0.05;

			if(iValue <= p01CriticalValue)
				return 0.1;
			
			return 1;
		}
		
		public final double p01CriticalValue;
		public final double p005CriticalValue;
		public final double p001CriticalValue;
		public final int n;
	}
	
	// init ONE_SIDE_CRITICAL_VALUES and TWO_SIDES_CRITICAL_VALUES
	static
	{
		ConfidenceCriticalValues[] oneSideCriticalVals = new ConfidenceCriticalValues[31];
		ConfidenceCriticalValues[] twoSidesCriticalVals = new ConfidenceCriticalValues[31];
		
		twoSidesCriticalVals[0] = new ConfidenceCriticalValues(-1,-1,-1,0);
		oneSideCriticalVals[0] = new ConfidenceCriticalValues(-1,-1,-1,0);
		twoSidesCriticalVals[1] = new ConfidenceCriticalValues(-1,-1,-1,1);
		oneSideCriticalVals[1] = new ConfidenceCriticalValues(-1,-1,-1,1);
		twoSidesCriticalVals[2] = new ConfidenceCriticalValues(-1,-1,-1,2);
		oneSideCriticalVals[2] = new ConfidenceCriticalValues(-1,-1,-1,2);
		twoSidesCriticalVals[3] = new ConfidenceCriticalValues(-1,-1,-1,3);
		oneSideCriticalVals[3] = new ConfidenceCriticalValues(-1,-1,-1,3);
		twoSidesCriticalVals[4] = new ConfidenceCriticalValues(-1,-1,-1,4);
		oneSideCriticalVals[4] = new ConfidenceCriticalValues(0,-1,-1,4);
		twoSidesCriticalVals[5] = new ConfidenceCriticalValues(0,-1,-1,5);
		oneSideCriticalVals[5] = new ConfidenceCriticalValues(2,0,-1,5);
		twoSidesCriticalVals[6] = new ConfidenceCriticalValues(2,0,-1,6);
		oneSideCriticalVals[6] = new ConfidenceCriticalValues(3,2,-1,6);
		twoSidesCriticalVals[7] = new ConfidenceCriticalValues(3,2,-1,7);
		oneSideCriticalVals[7] = new ConfidenceCriticalValues(5,3,0,7);
		twoSidesCriticalVals[8] = new ConfidenceCriticalValues(5,3,0,8);
		oneSideCriticalVals[8] = new ConfidenceCriticalValues(8,5,1,8);
		twoSidesCriticalVals[9] = new ConfidenceCriticalValues(8,5,1,9);
		oneSideCriticalVals[9] = new ConfidenceCriticalValues(10,8,3,9);
		twoSidesCriticalVals[10] = new ConfidenceCriticalValues(10,8,3,10);
		oneSideCriticalVals[10] = new ConfidenceCriticalValues(14,10,5,10);
		twoSidesCriticalVals[11] = new ConfidenceCriticalValues(13,10,5,11);
		oneSideCriticalVals[11] = new ConfidenceCriticalValues(17,13,7,11);
		twoSidesCriticalVals[12] = new ConfidenceCriticalValues(17,13,7,12);
		oneSideCriticalVals[12] = new ConfidenceCriticalValues(21,17,9,12);
		twoSidesCriticalVals[13] = new ConfidenceCriticalValues(21,17,9,13);
		oneSideCriticalVals[13] = new ConfidenceCriticalValues(26,21,12,13);
		twoSidesCriticalVals[14] = new ConfidenceCriticalValues(25,21,12,14);
		oneSideCriticalVals[14] = new ConfidenceCriticalValues(31,25,15,14);
		twoSidesCriticalVals[15] = new ConfidenceCriticalValues(30,25,15,15);
		oneSideCriticalVals[15] = new ConfidenceCriticalValues(36,30,19,15);
		twoSidesCriticalVals[16] = new ConfidenceCriticalValues(35,29,19,16);
		oneSideCriticalVals[16] = new ConfidenceCriticalValues(42,35,23,16);
		twoSidesCriticalVals[17] = new ConfidenceCriticalValues(41,34,23,17);
		oneSideCriticalVals[17] = new ConfidenceCriticalValues(48,41,27,17);
		twoSidesCriticalVals[18] = new ConfidenceCriticalValues(47,40,27,18);
		oneSideCriticalVals[18] = new ConfidenceCriticalValues(55,47,32,18);
		twoSidesCriticalVals[19] = new ConfidenceCriticalValues(53,46,32,19);
		oneSideCriticalVals[19] = new ConfidenceCriticalValues(62,53,37,19);
		twoSidesCriticalVals[20] = new ConfidenceCriticalValues(60,52,37,20);
		oneSideCriticalVals[20] = new ConfidenceCriticalValues(69,60,43,20);
		twoSidesCriticalVals[21] = new ConfidenceCriticalValues(67,58,42,21);
		oneSideCriticalVals[21] = new ConfidenceCriticalValues(77,67,49,21);
		twoSidesCriticalVals[22] = new ConfidenceCriticalValues(75,65,48,22);
		oneSideCriticalVals[22] = new ConfidenceCriticalValues(86,75,55,22);
		twoSidesCriticalVals[23] = new ConfidenceCriticalValues(83,73,54,23);
		oneSideCriticalVals[23] = new ConfidenceCriticalValues(94,83,62,23);
		twoSidesCriticalVals[24] = new ConfidenceCriticalValues(91,81,61,24);
		oneSideCriticalVals[24] = new ConfidenceCriticalValues(104,91,69,24);
		twoSidesCriticalVals[25] = new ConfidenceCriticalValues(100,89,68,25);
		oneSideCriticalVals[25] = new ConfidenceCriticalValues(113,100,76,25);
		twoSidesCriticalVals[26] = new ConfidenceCriticalValues(110,98,75,26);
		oneSideCriticalVals[26] = new ConfidenceCriticalValues(124,110,84,26);
		twoSidesCriticalVals[27] = new ConfidenceCriticalValues(119,107,83,27);
		oneSideCriticalVals[27] = new ConfidenceCriticalValues(134,119,92,27);
		twoSidesCriticalVals[28] = new ConfidenceCriticalValues(130,116,91,28);
		oneSideCriticalVals[28] = new ConfidenceCriticalValues(145,130,101,28);
		twoSidesCriticalVals[29] = new ConfidenceCriticalValues(140,126,100,29);
		oneSideCriticalVals[29] = new ConfidenceCriticalValues(157,140,110,29);
		twoSidesCriticalVals[30] = new ConfidenceCriticalValues(151,137,109,30);
		oneSideCriticalVals[30] = new ConfidenceCriticalValues(169,151,120,30);
		
		
		ONE_SIDE_CRITICAL_VALUES = oneSideCriticalVals;
		TWO_SIDES_CRITICAL_VALUES = twoSidesCriticalVals;
	}
	
	///////////////////////////////////////////////////////// private ///////////////////////////////////////////////////////////////////
	
	private class AbsComparator implements Comparator<Double>
	{
		public int compare(Double arg0, Double arg1)
		{
			return Double.compare(Math.abs(arg0), Math.abs(arg1));
		}
	}
	
	/**
	 * right bigger than left
	 */
	private double negativeSum;
	/**
	 * left bigger than right
	 */
	private double positiveSum;
	/**
	 * # of numbers in each sample
	 */
	private int sampleSize;
	/**
	 * # of none zero differences between matching numbers in both samples
	 */
	private int nonZeroDiffs;
	
	////////////////////////////////////////////////////////// main ///////////////////////////////////////////////////////////////////
	
//	public static void main(String[] args) throws StatsException
//	{
//		WilcoxonSignedRanksTest wtest;
//		Random rnd = new Random(new Date().getTime());
//		List<Double> left, right;
//		int N = 10;
//		
//		left = new LinkedList<Double>();
//		right = new LinkedList<Double>();
//		
//		for(int i = 0; i < N; i++){
//			left.add(rnd.nextGaussian());
//			right.add(rnd.nextGaussian());
//		}
//		
//		wtest = new WilcoxonSignedRanksTest(left, right);
//		System.out.println("same dist");
//		System.out.println("N: " + N + ", two-sides-best-conf-level: " + wtest.bestTwoSideConfidenceLevel() +
//				", min: " + wtest.minSum());
//
//		left.clear();
//		right.clear();
//		for(int i = 0; i < N; i++){
//			left.add(rnd.nextGaussian());
//			right.add(rnd.nextGaussian() + 0.1);
//		}
//		
//		System.out.println("different dist");
//		wtest = new WilcoxonSignedRanksTest(left, right);
//		System.out.println("N: " + N + ", two-sides-best-conf-level: " + wtest.bestTwoSideConfidenceLevel() +
//				", mina: " + wtest.minSum());
//	}
//}

	public static void main(String[] args) throws StatsException, IOException
	{
		List<String> listAsStringsLeft = FileUtils.loadFileToList(new File(args[2]));
		List<String> listAsStringsRight = FileUtils.loadFileToList(new File(args[3]));
		
		if(listAsStringsLeft.size() != listAsStringsRight.size()){
			throw new StatsException("input list are not of the same length");
		}
		
		List<Double> left = new LinkedList<Double>();
		List<Double> right = new LinkedList<Double>();
		for(int i = 0 ; i < listAsStringsLeft.size(); i++){
			left.add(Double.parseDouble(listAsStringsLeft.get(i)));
			right.add(Double.parseDouble(listAsStringsRight.get(i)));
		}
		System.out.println(left);
		System.out.println(right);
		
		WilcoxonSignedRanksTest wtest = new WilcoxonSignedRanksTest(left, right);
		System.out.println("Wikcoxon:\t" + wtest.bestTwoSideConfidenceLevel() +
				", min: " + wtest.minSum());

		
	}
}