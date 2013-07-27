package eu.excitementproject.eop.common.utilities.math;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Shachar Mirkin
 * 2009
 * 
 * A class of various static math utils methods
 * 
 * 22.10.10 moved to Infrastructure by Amnon
 * 
 * 
 */
public class MathUtils {

	private final static double LOG2 = Math.log(2);
	private final static double LOG10 = Math.log(10);
	private final static String LIST_SZ_ERROR = "L2 distance is defined only over vectors of equal size";
	
	/**
	 * 
	 * @param x
	 * @return log2(x)
	 */
	public static double log2(double x) {
		return Math.log(x) / LOG2;
	}
	
	/**
	 * 
	 * @param x
	 * @return log10(x)
	 */
	public static double log10(double x) {
		return Math.log(x) / LOG10;
	}

	/**
	 * 
	 * @param x
	 * @return the value 0 if x is numerically 0; a value less than 0 if x is numerically negative; 
	 * and a value greater than 0 if x numerically positive
	 */
	public static int sign(double x) {
		return ((Double) x).compareTo(0.0);
	}
	
	/**
	 * 	
	 * @param x
	 * @param y
	 * @param epsilon
	 * @return whether x is in the epsilon-vicinity of y
	 */
	public static boolean isEpsilonClose(double x, double y, double epsilon) {
		return (Math.abs(x - y) <= epsilon);
	}
	
	/**
	 * 
	 * @param recall
	 * @param precision
	 * @return F1 score
	 */
	public static double FScore(double recall, double precision) {
		
		if(recall+precision==0)
			return 0;
		return (2*recall*precision) / (recall + precision);
	}
	
	/**
	 * 	
	 * @param numbers
	 * @return the harmonic mean
	 */
	public static double harmonicMean(double[] numbers) {
		
		double sum=0;
		for(int i = 0; i < numbers.length;++i) {
			if(numbers[i]<=0)
				return 0;
			sum+= 1/numbers[i];
		}
		return numbers.length / sum;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return the geometric mean
	 */
	public static double geometricMean(double a, double b) {
		return Math.pow(a*b, 0.5);
	}

	/**
	 * 
	 * @param a1 first element
	 * @param q  power
	 * @param n  length of the series
	 * @return the sum of the geometric series
	 */
	public static double geometricSeriesSum(double a1, double q, int n) {
		
		return a1*(Math.pow(q, n)-1) / (q-1); 
	}
	
	public static Integer sum(Collection<Integer> collection) {
		Integer result = 0;
		for (Integer n : collection) {
			result += n;
		}
		return result;
	}

	/**
	 * 	
	 * @param numbers - doubles
	 * @return their average
	 */
	public static double average(List<Double> numbers) {
		
		double sum = 0; 
		for(Double number: numbers)
			sum+=number;
		return sum/numbers.size();
	}
	
	/**
	 * 	
	 * @param numbers - integers
	 * @return their average
	 */
	public static double averageInt(List<Integer> numbers) {
		double sum = 0;
		if(numbers.size() == 0){
			return 0;
		}
		for(Integer number: numbers)
			sum+=number;
		return sum/(double)numbers.size();
	}
	
	/**
	 * 	
	 * @param iNumbers - integers
	 * @return the standard deviation
	 */
	public static Double stdInt(List<Integer> elements) {
		List<Double> doubles = new ArrayList<Double>(elements.size());
		for (Integer n : elements) {
			doubles.add(new Double(n));
		}
		return std(doubles);
	}
	
	/**
	 * 	
	 * @param iNumbers - doubles
	 * @return the standard deviation
	 */
	public static double std(List<Double> iNumbers) {
		
		double result=0;
		double mu = average(iNumbers);
		for(Double number: iNumbers)
			result+=Math.pow(number-mu, 2);
		
		result/=iNumbers.size();
		
		return Math.sqrt(result);
	}
	
	/**
	 * 	
	 * @param iNumbers - doubles
	 * @param mu - the average
	 * @return the standard deviation
	 */
	public static double std(List<Double> iNumbers,double mu) {
		
		double result=0;
		
		for(Double number: iNumbers)
			result+=Math.pow(number-mu, 2);
		
		result/=iNumbers.size();
		
		return Math.sqrt(result);
	}
	
	/**
	 * 	
	 * @param iNumbers - doubles
	 * 
	 * normalize the list according to their standard deviation
	 */
	public static void standardNormalization(List<Double> iNumbers) {
		
		double mu = average(iNumbers);
		double std = std(iNumbers,mu);
		
		for(int i = 0; i < iNumbers.size();++i) {
			if(std==0)
				iNumbers.set(i, iNumbers.get(i)-mu);
			else 
				iNumbers.set(i, (iNumbers.get(i)-mu)/std);
		}
	}
	
	/**
	 * 	
	 * @param v1 doubles
	 * @param v2 doubles - must have the same length as v1
	 * @return the L1 distance between the two vectors - the square root of the sum of all squares of the differences between matching elements
	 * @throws MathUtilsListDistanceException if(v1.size()!=v2.size())
	 */
	public static double l2Distance(List<Double> v1, List<Double> v2) throws MathUtilsListDistanceException {
		
		if(v1.size()!=v2.size())
			throw new MathUtilsListDistanceException(LIST_SZ_ERROR);
		double sum = 0;
		for(int i = 0; i < v1.size(); i++)
			sum+=Math.pow(v1.get(i)-v2.get(i),2);
			
		return Math.sqrt(sum);
	}

	/**
	 * 	
	 * @param v1 doubles
	 * @param v2 doubles - must have the same length as v1
	 * @return the L2 distance between the two vectors - the sum of the differences between matching elements
	 * @throws MathUtilsListDistanceException if(v1.size()!=v2.size())
	 */	
	public static double l1Distance(List<Double> v1, List<Double> v2) throws MathUtilsListDistanceException {
		
		if(v1.size()!=v2.size())
			throw new MathUtilsListDistanceException(LIST_SZ_ERROR);
		double sum = 0;
		for(int i = 0; i < v1.size(); i++)
			sum+=v1.get(i)-v2.get(i);
			
		return sum;
	}
	/**
	 * Prints to a file the accuracy, recall, precision and F1 on a curve corresponding to various thresholds
	 * 
	 * @param positiveValues - scores for gold standard examples labeled as "yes"
	 * @param negativeValues - scores for gold standard examples labeled as "no"
	 * @param out
	 * @param outFrequency - number of points on the curve
	 */
	public static void generateMeasureCurve(List<Double> positiveValues, List<Double> negativeValues, PrintStream out, int outFrequency) {

		int posUnder=0,negUnder=0,posOver=positiveValues.size(),negOver=negativeValues.size();
		int posIndex = 0, negIndex = 0;
		
		Collections.sort(positiveValues);
		Collections.sort(negativeValues);

		int i = 0;
		while(posIndex < positiveValues.size() && negIndex < negativeValues.size()) {

			i++;
			if(positiveValues.get(posIndex) < negativeValues.get(negIndex)) {
				posIndex++;
				posUnder++;
				posOver--;
			}
			else if(negativeValues.get(negIndex) < positiveValues.get(posIndex)) {
				negIndex++;
				negUnder++;
				negOver--;
			}
			else {
				posIndex++;
				posUnder++;
				posOver--;
				negIndex++;
				negUnder++;
				negOver--;
			}

			if(i % outFrequency == 0) {
				double accuracy = (double) (posOver+negUnder) / (posOver+posUnder+negOver+negUnder);
				double recall = (double) posOver / (posOver + posUnder);
				double precision = (double) posOver / (posOver + negOver);
				out.println("accuracy\t"+accuracy+"\trecall\t"+recall+"\tprecision\t"+precision
						+"\tF1\t"+MathUtils.FScore(recall, precision));
			}
		}
	}

	/**
	 * Prints to a file the accuracy, recall, precision and F1 on a curve corresponding to various thresholds
	 * 
	 * @param positiveValues - scores for gold standard examples that are classified as "yes"
	 * @param negativeValues - scores for gold standard examples that are classified as "no"
	 * @param out
	 * @param outFrequency - number of points on the curve
	 */
	public static void generateMeasureCurve(List<Double> positiveValues, List<Double> negativeValues, PrintWriter out, int outFrequency) {

		int posUnder,posOver,negUnder,negOver;
		int posIndex = 0, negIndex = 0;
		
		Collections.sort(positiveValues);
		Collections.sort(negativeValues);

		//init
		posUnder = negUnder = 0;
		posOver = positiveValues.size();
		negOver= negativeValues.size();

		int i = 0;
		while(posIndex < positiveValues.size() && negIndex < negativeValues.size()) {

			i++;
			if(positiveValues.get(posIndex) < negativeValues.get(negIndex)) {
				posIndex++;
				posUnder++;
				posOver--;
			}
			else if(negativeValues.get(negIndex) < positiveValues.get(posIndex)) {
				negIndex++;
				negUnder++;
				negOver--;
			}
			else {
				posIndex++;
				posUnder++;
				posOver--;
				negIndex++;
				negUnder++;
				negOver--;
			}

			if(i % outFrequency == 0) {
				double accuracy = (double) (posOver+negUnder) / (posOver+posUnder+negOver+negUnder);
				double recall = (double) posOver / (posOver + posUnder);
				double precision = (double) posOver / (posOver + negOver);
				double f1 = MathUtils.FScore(recall, precision);
				out.println(accuracy+"\t"+recall+"\t"+precision
						+"\t"+f1);
			}
		}
	}
	/**
	 * @param recallValues array of recall values sorted in increasing order
	 * @param precisionValues array of precision values
	 * @param startRange Point where we start computing AUC
	 * @param endRange Point where we end computing AUC
	 * @return AUC
	 * @throws MathUtilsAucException 
	 */
	public static double computeAUC(double[] recallValues, double[] precisionValues, double startRange, double endRange) throws MathUtilsAucException {
		
		if(recallValues.length != precisionValues.length || recallValues.length<2)
			throw new MathUtilsAucException("Size of recall and precision arrays is not valid, recall values array size: " + recallValues.length + 
					"precision values array size: " + precisionValues.length);
		
		
		if(startRange < recallValues[0] || endRange > recallValues[recallValues.length-1])
			throw new MathUtilsAucException("The ranges are not compatible to the recall and precision arrays");
		
		int startIndex = -1;
		for(int i = 0; i < recallValues.length-1;++i) {
			if(recallValues[i]<=startRange && recallValues[i+1] > startRange) {
				startIndex = i;
				break;
			}
		}
		
		int endIndex = -1;
		for(int j = recallValues.length-1; j>=0;  j--) {
			if(recallValues[j]>=endRange && recallValues[j-1] < endRange) {
				endIndex=j;
				break;
			}
		}
		
		//compute the recall and precision of the start of the range
		double startRangeProportion = (startRange-recallValues[startIndex]) / (recallValues[startIndex+1]-recallValues[startIndex]);
		double startPrecisionChange = precisionValues[startIndex]-precisionValues[startIndex+1];
		recallValues[startIndex] = startRange;
		precisionValues[startIndex] -= startRangeProportion * startPrecisionChange; 

		//compute the recall and precision of the end of the range
		double endRangeProportion = (recallValues[endIndex]-endRange) / (recallValues[endIndex]-recallValues[endIndex-1]);
		double endPrecisionChange =precisionValues[endIndex] - precisionValues[endIndex-1];
		recallValues[endIndex] = endRange;
		precisionValues[endIndex] -= endRangeProportion * endPrecisionChange;
		
		double auc = 0.0;
		for(int i = startIndex; i < endIndex ; ++i) {
			
			double xRange = recallValues[i+1] - recallValues[i];
			double yMin = Math.min(precisionValues[i+1], precisionValues[i]);
			double yMax = Math.max(precisionValues[i+1], precisionValues[i]);
			auc += xRange * yMin;
			auc += xRange * ((yMax-yMin) / 2);
		}
		return auc;
	}
	
	/**
	 * computes the cosine similarity measure between two vectors
	 * @param vec1
	 * @param vec2
	 * @return
	 * @throws MathUtilsListDistanceException
	 */
	public static double cosine(double[] vec1, double[] vec2) throws MathUtilsListDistanceException{
		
		if(vec1.length != vec2.length)
			throw new MathUtilsListDistanceException(LIST_SZ_ERROR);
		
		double nominator = 0;
		for(int i = 0; i < vec1.length;++i)
			nominator+=vec1[i]*vec2[i];
			
		return nominator / (norm2(vec1)*norm2(vec2));	
	}
	
	/**
	 * returns the norm2 of an array
	 * @param vec
	 * @return
	 */
	public static double norm2(double[] vec) {
		double result = 0.0;
		for(int i = 0; i < vec.length;++i)
			result+=Math.pow(vec[i], 2);
		return Math.pow(result, 0.5);
	}
	
	/**
	 * returns the sigmoid of a real number
	 * @param x
	 * @return sigmoid(x)
	 */
	public static double sigmoid(double x) {
		return (double) 1 / (1 + Math.exp(-x));
	}

}
