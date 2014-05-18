package eu.excitementproject.eop.common.utilities.stats;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.file.FileUtils;


/**
 * Calculates the Z value of the sign test
 * @author Eyal Shnarch
 * @since 12/01/2012
 */
public class SignTest
{	
	

	private int sampleSize;
	private int numRightBiggerThanLeft;
	private int nonZeroDiffs;
	
	
	public SignTest(List<Double> leftList, List<Double> rightList) throws StatsException
	{
		if (leftList == null || rightList == null)
			throw new StatsException("Got null input");
		if (leftList.size() != rightList.size())
			throw new StatsException("The two lists must have the same size");
		
		sampleSize = leftList.size();		
		numRightBiggerThanLeft = 0;	
		nonZeroDiffs = 0;
		Iterator<Double> leftIter = leftList.iterator();
		Iterator<Double> rightIter = rightList.iterator();
		
		
		while(leftIter.hasNext()){
			double left = leftIter.next();
			double right = rightIter.next();

			if(right != left){
				nonZeroDiffs++;
				if(right > left){
					numRightBiggerThanLeft++;
				}
			}
		}
	}
		

	public double getBinomilaNormalAprox(){
		final double CONTINUITY_CORRECTION =0.5;
		double mu = getNumNonZeroDiffs()*0.5;
		double theta = Math.sqrt(getNumNonZeroDiffs()*0.5*0.5);
		double z = (getNumRightIsBigger()-CONTINUITY_CORRECTION-mu)/theta;
		return z;
	}
	
	public int getSampleSize()
	{
		return sampleSize;
	}
	
	public int getNumRightIsBigger(){
		return numRightBiggerThanLeft;
	}
	
	public int getNumNonZeroDiffs(){
		return nonZeroDiffs;
	}
		

	///////////////////////////////// main //////////////////////////////////////////

	public static void main(String[] args) throws StatsException, IOException
	{
		List<String> listAsStringsLeft = FileUtils.loadFileToList(new File(args[0]));
		List<String> listAsStringsRight = FileUtils.loadFileToList(new File(args[1]));
		
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
		System.out.println();
		
		SignTest signTest = new SignTest(left, right);
		System.out.println("Sing test:\nW (times Right is bigger than Left): " + 
				signTest.getNumRightIsBigger()+"" +
						" out of "+signTest.getNumNonZeroDiffs()+" non zero differences\n" +
								"under H0: W~Bin("+signTest.getNumNonZeroDiffs()+",0.5)");
		
		double mu = signTest.getNumNonZeroDiffs()*0.5;
		double theta = Math.sqrt(signTest.getNumNonZeroDiffs()*0.5*0.5);
		System.out.println("therefore,\nmu = "+mu+"\ntheta = "+theta);
		System.out.println("Z = "+signTest.getBinomilaNormalAprox());
		System.out.println("now go and check the standard normal distribution table!");

		
	}
}