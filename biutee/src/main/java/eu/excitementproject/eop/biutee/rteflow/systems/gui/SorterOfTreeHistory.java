package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.common.utilities.Utils;

/**
 * To be used to display the history with colors indicating the more expensive and less
 * expensive components.
 * 
 * @author Asher Stern
 * @since Dec 24, 2011
 *
 */
public class SorterOfTreeHistory
{
	public SorterOfTreeHistory(TreeHistory treeHistory,
			LinearClassifier classifier,
			Map<Integer, Double> featureVectorCompleteProof) throws ClassifierException
	{
		super();
		this.treeHistory = treeHistory;
		this.classifier = classifier;
		this.featureVectorCompleteProof = featureVectorCompleteProof;
		
		createSorted();
		createSets();
	}

	public Set<TreeHistoryComponent> getCausingFailure() throws ClassifierException
	{
		return this.causingFailure;
	}
	
	public Set<TreeHistoryComponent> getWorseThanMedian()
	{
		return this.worseThanMedian;
	}
	
	private void createSorted() throws ClassifierException
	{
		if (logger.isDebugEnabled()){logger.debug("SorterOfTreeHistory: creating sorted map...");}
		if (logger.isDebugEnabled()){logger.debug(String.format("Classifier threshold = %-4.4f", classifier.getThreshold()));}
		Map<TreeHistoryComponent,Double> map = new HashMap<TreeHistoryComponent, Double>();
		double initialCost = -classifier.getProduct(treeHistory.getInitialComponent().getFeatureVector());
		if (logger.isDebugEnabled()){logger.debug(String.format("Initial cost = %-4.4f", initialCost));}
		double prevCost = initialCost;
		for (TreeHistoryComponent component : treeHistory.getComponents())
		{
			double currentCost = -classifier.getProduct(component.getFeatureVector());
			map.put(component, currentCost-prevCost);
			if (logger.isDebugEnabled()){logger.debug(String.format("Cost = %-4.4f. Delta = %-4.4f Spec = %s", currentCost, (currentCost-prevCost), component.getSpecification().toString()));}
			prevCost = currentCost;
		}
		List<TreeHistoryComponent> sorted = Utils.getSortedByValue(map);
		Collections.reverse(sorted);
		sortedNonIncreasingOrder = new LinkedHashMap<TreeHistoryComponent, Double>();
		for (TreeHistoryComponent component : sorted)
		{
			sortedNonIncreasingOrder.put(component,map.get(component));
		}
	}
	
	private void createSets() throws ClassifierException
	{
		causingFailure = new LinkedHashSet<TreeHistoryComponent>();
		double proofCost = -classifier.getProduct(featureVectorCompleteProof);
		double currentCost = proofCost;
		double threshold = classifier.getThreshold();
		for (TreeHistoryComponent component : sortedNonIncreasingOrder.keySet())
		{
			if (currentCost>threshold)
			{
				causingFailure.add(component);
				currentCost -= sortedNonIncreasingOrder.get(component);
			}
		}
		
		worseThanMedian = new LinkedHashSet<TreeHistoryComponent>();
		int index=0;
		Iterator<TreeHistoryComponent> iterator = sortedNonIncreasingOrder.keySet().iterator();
		while (index<sortedNonIncreasingOrder.keySet().size()/2)
		{
			worseThanMedian.add(iterator.next());
			index++;
		}

	}
	

	private TreeHistory treeHistory;
	private LinearClassifier classifier;
	private Map<Integer,Double> featureVectorCompleteProof;

	private LinkedHashMap<TreeHistoryComponent,Double> sortedNonIncreasingOrder; // bad to good
	private Set<TreeHistoryComponent> causingFailure;
	private Set<TreeHistoryComponent> worseThanMedian;
	
	private static final Logger logger = Logger.getLogger(SorterOfTreeHistory.class);
}
