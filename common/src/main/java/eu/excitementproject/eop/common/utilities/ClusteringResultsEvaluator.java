package eu.excitementproject.eop.common.utilities;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains methods for calculating clustering evaluation measures (Purity, RandIndex, Recall, Precision and F-measures), as described in 
 * "Introduction to Information Retrieval" (Manning et al, 2008)
 * 
 * @author Lili Kotlerman
 * @since 20-March-2012
 */
public class ClusteringResultsEvaluator {
	

	/**
	 * @param goldStandardClusters - a hash table with the gold-standard clusters. Keys are cluster labels, values are LinkedLists containing the ids of items in the corresponding cluster
	 * @param evaluatedClusters - a hash table with the clusters to be evaluated (the same format as above)
	 * @return Putiry value
	 */
	public static double calculatePurity(Map<String, ? extends List<Integer>> goldStandardClusters, Map<String, ? extends List<Integer>> evaluatedClusters){
		double purity=0.0;
		double denominator=0.0;
		for (String clustName : evaluatedClusters.keySet()){
			denominator += evaluatedClusters.get(clustName).size(); //the number of reasons in the cluster
			double maxNominator = 0.0;
			for (String gsCluster : goldStandardClusters.keySet()){
				LinkedList<Integer> tmpGScluster = new LinkedList<Integer>();
				for (Integer x: goldStandardClusters.get(gsCluster)){
					tmpGScluster.add(x);
				}
				//System.out.print(clustName+" "+gsCluster+" : "+tmpGScluster.size()+ " ");
				tmpGScluster.retainAll(evaluatedClusters.get(clustName));
				//System.out.println(tmpGScluster.size());
				double candidateNominator = tmpGScluster.size();
				if (maxNominator<candidateNominator) maxNominator=candidateNominator;
			}
			purity += maxNominator;
		}		
		purity /= denominator;
		return purity;
	}	

	/**
	 * @param itemIds - a set containing the ids of all the clustered items
	 * @param goldStandardClusters - a hash table with the gold-standard clusters. Keys are cluster labels, values are LinkedLists containing the ids of items in the corresponding cluster
	 * @param evaluatedClusters - a hash table with the clusters to be evaluated (the same format as above)
	 * @return A hash table with the values of Recall, Precision, F1, F2, F0.5 and RandIndex measures
	 * <p> The keys are as follows: "R" (recall), "P" (precision), "F1", "F2", "F0.5", "randIndex".
	 */
	public static Map<String,Double> calculateRecallPrecisionFmeasuresAndRandIndex(Set<Integer> itemIds, Map<String, ? extends List<Integer>> goldStandardClusters, Map<String, ? extends List<Integer>> evaluatedClusters){

		Map<String,Double> d = calculateTrueAndFalseDecisions(itemIds, goldStandardClusters, evaluatedClusters);
		Map<String,Double> measures = new LinkedHashMap<String, Double>();
		double tp = d.get("tp");
		double fp = d.get("fp");
		double tn = d.get("tn");
		double fn = d.get("fn");
		
		double randIndex = (tp+tn)/(tp+tn+fp+fn);
		measures.put("randIndex", randIndex);
		
		double P = tp/(tp+fp); // what out of our positives is really positive?
		double R = tp/(tp+fn); // what out of real positives have we found?
		measures.put("P", P);
		measures.put("R", R);
		
		double beta =1.0;
		double F = ((beta*beta + 1)*P*R) / (beta*beta*P + R);
		measures.put("F1", F);
		
		beta = 2.0;
		F = ((beta*beta + 1)*P*R) / (beta*beta*P + R);
		measures.put("F2", F);
			
		beta = 0.5;
		F = ((beta*beta + 1)*P*R) / (beta*beta*P + R);
		measures.put("F0.5", F);
	
		return measures;
	}	
	
	
	private static Map<String,Double> calculateTrueAndFalseDecisions(Set<Integer> itemIds, Map<String, ? extends List<Integer>> clustGS, Map<String, ? extends List<Integer>> clustEval){
		double tp=0.0;
		double tn=0.0;
		double fp = 0.0;
		double fn = 0.0;
		
		for (Integer i : itemIds){
			for (Integer j : itemIds){
				if (i>=j) continue; // don't check the same pair twice
				// check in GS whether reasons i and j are clustered together
				boolean togetherInGS = false;
				for (String gsCluster : clustGS.keySet()){
					if((clustGS.get(gsCluster).contains(i))&&(clustGS.get(gsCluster).contains(j))){
						togetherInGS=true;
						break; // if seen together - no need to search in other clusters
					}
				}
				// check in clusters that are evaluated whether reasons i and j are clustered together
				boolean togetherInClustersToEvaluate = false;
				for (String cluster : clustEval.keySet()){
					if((clustEval.get(cluster).contains(i))&&(clustEval.get(cluster).contains(j))){
						togetherInClustersToEvaluate=true;
						break; // if seen together - no need to search in other clusters
					}
				}
				// check whether the decision is the same or no
				if (togetherInGS==togetherInClustersToEvaluate){
					if (togetherInClustersToEvaluate==true) tp++; //true positive (clustered the 2 together and it's correct)
					else tn++; //true negative (did not cluster the 2 together and it's correct) 
				}
				else {
					if (togetherInClustersToEvaluate==true) fp++; //false positive (clustered the 2 together and it's wrong)
					else fn++; //false negative (did not cluster the 2 together and it's wrong) 
				}
			}
		}
		Map<String,Double> decisions = new LinkedHashMap<String, Double>();
		decisions.put("tp",tp);
		decisions.put("fp",fp);
		decisions.put("tn",tn);
		decisions.put("fn",fn);
		return decisions;
	}	
	
}