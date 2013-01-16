package eu.excitementproject.eop.common.utilities.stats;

import java.util.ArrayList;

/**
 * This class computes Fleiss Kappa inter-annotator agreement.
 * This is for when you have more than 2 annotators and each item is rated
 * a fixed number of times. The number you get is not greater than 1.
 * 
 * It also computes the Cohen's kappa coefficient for when you have two 
 * annotators making a binary decision for a set of items.
 * <p>
 * This class was originally in project BURST.
 * 
 * @author Jonathan
 * @since 2 Jan 2010
 */
public class AgreementTests {
	
	/**
	 * computeFleiss kappa score for the table
	 * 
	 * @param table logically this is a matrix of <i>items</i> on <i>categories</i>. Each cell counts how many <i>ratings</i> 
	 * the matching <i>item</i> got as the matching <i>category</i>. Each item should have the same total amount of ratings
	 * 
	 * @return the kappa score.
	 * @throws StatsException
	 */
	public static double computeFleiss(ArrayList<ArrayList<Integer>> table) throws StatsException 
	{		
		validateTable(table);
		
		// initialize stuff
		int numOfRatingsPerItem = 0;	
		for(Integer numOfCategoryRatings : table.get(0))
			numOfRatingsPerItem += numOfCategoryRatings;
		int numOfCategories = table.get(0).size();
		int[] categoryRatingsSum = new int[numOfCategories];
		double[] itemScore = new double[table.size()];
		int totalSum=0;
		
		// calc a special score for each item
		for(int itemNdx = 0; itemNdx < table.size(); itemNdx++) 
		{			
			ArrayList<Integer> item = table.get(itemNdx);
			int sumOfSquares = 0;
			
			// sum up the counts in this item
			for(int countNdx=0; countNdx < item.size();++countNdx) 
			{				
				int itemCategoryNumOfRatings = item.get(countNdx);
				categoryRatingsSum[countNdx] += itemCategoryNumOfRatings;
				totalSum += itemCategoryNumOfRatings;
				sumOfSquares += Math.pow(itemCategoryNumOfRatings, 2);
			}
			
			sumOfSquares -= numOfRatingsPerItem;
			itemScore[itemNdx] = (double) sumOfSquares / (numOfRatingsPerItem * (numOfRatingsPerItem-1));
			
			//System.out.println("score for item" + (itemNdx+1) + ": " + itemScore[itemNdx]);
		}
		
		// calc a score for each category
		double[] categoryScore = new double[numOfCategories];
		for(int i = 0; i < categoryScore.length; i++) 
		{
			categoryScore[i] = (double) categoryRatingsSum[i] / totalSum;
			//System.out.println("score for category" + (i+1) + ": " + categoryScore[i]);
		}
		
		// calc itemScoreAverage
		double itemScoreAverage = 0;		
		for(int i = 0; i < itemScore.length; ++i)
			itemScoreAverage+=itemScore[i];		
		itemScoreAverage /= itemScore.length;
		
		// calc categoryScoreSumOfSquares
		double categoryScoreSumOfSquares = 0;
		for(int i = 0; i < categoryScore.length;++i)
			categoryScoreSumOfSquares += Math.pow(categoryScore[i], 2);
		
		//System.out.println("Item score average is: " + itemScoreAverage);
		//System.out.println("category sum of sqaures is: " + categoryScoreSumOfSquares);
		
		return (itemScoreAverage-categoryScoreSumOfSquares) / (1-categoryScoreSumOfSquares);
	}
	
	/**
	 * computeKappa from two lists of boolean annotator judgments 
	 * 
	 * @param annotator1
	 * @param annotator2
	 * @return computeKappa from two lists of boolean annotator judgments
	 * @throws StatsException
	 */
	public static double computeKappa(ArrayList<Boolean> annotator1, ArrayList<Boolean> annotator2) throws StatsException 
	{		
		if (annotator1 == null || annotator2 == null)
			throw new StatsException("Got null input");
		if(annotator1.size() != annotator2.size())
			throw new StatsException("Number of items is different for the two annotators");
		
		int yesYes=0,yesNo=0,noYes=0,noNo=0;
		
		for(int i = 0; i < annotator1.size(); i++) 
		{
			if(annotator1.get(i)) {
				if(annotator2.get(i))
					yesYes++;
				else
					yesNo++;
			}
			else {
				if(annotator2.get(i))
					noYes++;
				else
					noNo++;
			}
		}
		
		return computeKappa(yesYes,yesNo,noYes,noNo);
	}
	
	/**
	 * computeKappa from four Yes/No combination counts
	 * @param iYesYes
	 * @param iYesNo
	 * @param iNoYes
	 * @param iNoNo
	 * @return computeKappa from four Yes/No combination counts
	 */
	public static double computeKappa(int iYesYes, int iYesNo, int iNoYes, int iNoNo) {
		
		int total = iYesYes+iYesNo+iNoYes+iNoNo;
		
		double firstYesProb = (double) (iYesYes+iYesNo) / total;
		double secondYesProb = (double) (iYesYes+iNoYes) / total;
		
		double trueAgreement = (double) (iYesYes+iNoNo) / total;
		double randomAgreement = (firstYesProb*secondYesProb) + (1-firstYesProb)*(1-secondYesProb); 
		
		return (trueAgreement-randomAgreement) / (1-randomAgreement);
	}
	
	
	/**
	 * Validate that all items in table are consistent in number of categories (== size of the inner Lists) and in the amount of ratings
	 * in each category (== sum of ints in each inner list)
	 * 
	 * @param table
	 * @throws StatsException if any inconsistency is found
	 */
	private static void validateTable(ArrayList<ArrayList<Integer>> table) throws StatsException
	{
		//init the variables		
		int numOfRatings = 0;
		for(Integer numOfCategoryRatings : table.get(0))
			numOfRatings += numOfCategoryRatings;
		
		int numOfCategories = table.get(0).size();
		
		for(ArrayList<Integer> itemRatings: table) 
		{			
			if(itemRatings.size() != numOfCategories)
				throw new StatsException("The number of categories in item " + itemRatings + " isn't consistent with  other items");
			
			// sum the number of ratings for all categories in this item
			int currItemNumOfRatings=0;
			for(Integer numOfCategoryRatings: itemRatings)
				currItemNumOfRatings+=numOfCategoryRatings;
						
			if(currItemNumOfRatings != numOfRatings)
				throw new StatsException("The number of ratings in item " + itemRatings + " isn't consistent with  other items");
		}	
	}
	
	public static void main(String args[]) throws Exception {
		
		ArrayList<ArrayList<Integer>> table = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> item0 = new ArrayList<Integer>();
		ArrayList<Integer> item1 = new ArrayList<Integer>();
		ArrayList<Integer> item2 = new ArrayList<Integer>();
		ArrayList<Integer> item3 = new ArrayList<Integer>();
		ArrayList<Integer> item4 = new ArrayList<Integer>();
		ArrayList<Integer> item5 = new ArrayList<Integer>();
		ArrayList<Integer> item6 = new ArrayList<Integer>();
		ArrayList<Integer> item7 = new ArrayList<Integer>();
		ArrayList<Integer> item8 = new ArrayList<Integer>();
		ArrayList<Integer> item9 = new ArrayList<Integer>();
		
		item1.add(0);
		item1.add(0);
		item1.add(0);
		item1.add(0);
		item1.add(14);
		item2.add(0);
		item2.add(2);
		item2.add(6);
		item2.add(4);
		item2.add(2);
		item3.add(0);
		item3.add(0);
		item3.add(3);
		item3.add(5);
		item3.add(6);
		item4.add(0);
		item4.add(3);
		item4.add(9);
		item4.add(2);
		item4.add(0);
		item5.add(2);
		item5.add(2);
		item5.add(8);
		item5.add(1);
		item5.add(1);
		item6.add(7);
		item6.add(7);
		item6.add(0);
		item6.add(0);
		item6.add(0);
		item7.add(3);
		item7.add(2);
		item7.add(6);
		item7.add(3);
		item7.add(0);
		item8.add(2);
		item8.add(5);
		item8.add(3);
		item8.add(2);
		item8.add(2);
		item9.add(6);
		item9.add(5);
		item9.add(2);
		item9.add(1);
		item9.add(0);
		item0.add(0);
		item0.add(2);
		item0.add(2);
		item0.add(3);
		item0.add(7);
		
		table.add(item1);
		table.add(item2);
		table.add(item3);
		table.add(item4);
		table.add(item5);
		table.add(item6);
		table.add(item7);
		table.add(item8);
		table.add(item9);
		table.add(item0);
		
		System.out.println("agreement is: "+computeFleiss(table));	
		
		System.out.println(computeKappa(20, 5, 10, 15));
		System.out.println(computeKappa(45, 15, 25, 15));
		System.out.println(computeKappa(25, 35, 5, 35));
		
	}
	
	

}
