package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;



/**
 * 
 * @author Asher Stern
 * @since Aug 14, 2011
 *
 */
public class KStagedAlgorithm<T>
{
	public KStagedAlgorithm(List<T> initialStates,
			ByIterationComparator<T> comparatorForExpand,
			ByIterationComparator<T> comparatorForCut,
			StateCalculator<T> stateCalculator, int numberToExpand,
			int numberToRetain, boolean maintainClosedList,
			int numberOfIterationsAfterFound, Comparator<T> bestGoalComparator,
			boolean discardExpandedStates)
	{
		super();
		this.initialStates = initialStates;
		this.comparatorForExpand = comparatorForExpand;
		this.comparatorForCut = comparatorForCut;
		this.stateCalculator = stateCalculator;
		this.numberToExpand = numberToExpand;
		this.numberToRetain = numberToRetain;
		this.maintainClosedList = maintainClosedList;
		this.numberOfIterationsAfterFound = numberOfIterationsAfterFound;
		this.bestGoalComparator = bestGoalComparator;
		this.discardExpandedStates = discardExpandedStates;
	}

	public void find() throws KStagedAlgorithmException
	{
		cache = new HashMap<T, List<T>>();
		openList = new ArrayList<T>(numberToRetain);
		if (maintainClosedList) closedList = new LinkedHashSet<T>();
		openList.addAll(initialStates);
		goals = new ArrayList<T>();
		int iterationIndex=0;
		int iterationIndexAfterFound = 0;
		while ( (openList.size()>0) && ( (goals.size()==0) || (iterationIndexAfterFound<numberOfIterationsAfterFound) ) )
		{
			if (logger.isDebugEnabled())logger.debug("Starting while loop...\nopenList.size() = "+openList.size()+"\ngoals.size() = "+goals.size()+"\niterationIndex = "+iterationIndex);
			cleanCache();
			comparatorForExpand.setIteration(iterationIndex);
			comparatorForCut.setIteration(iterationIndex);
			Collections.sort(openList,comparatorForExpand);
			List<T> newOpenList = new ArrayList<T>();
			Iterator<T> openListIterator = openList.iterator();
			for (int itemsIndex=0;itemsIndex<numberToExpand && openListIterator.hasNext();++itemsIndex)
			{
				T currentState = openListIterator.next();
				if (stateCalculator.isGoal(currentState))
				{
					goals.add(currentState);
					if (maintainClosedList)
						closedList.add(currentState);
				}
				else
				{
					++numberOfExpansions;
					List<T> generated=null;
					if (cache.containsKey(currentState))
					{
						generated = cache.get(currentState);
					}
					else
					{
						generated = stateCalculator.generateChildren(currentState, closedList);
						numberOfExpensiveGenerations+=generated.size();
						cache.put(currentState,generated);
					}
					numberOfGenerations+=generated.size();
					
					newOpenList.addAll(generated);
					if (!discardExpandedStates)
						newOpenList.add(currentState);
				}
			}
			
			// Add all elements that were not expanded. Note! this is the meaning of big-K.
			// Otherwise, there is not meaning to big-K.
			while (openListIterator.hasNext())
			{
				newOpenList.add(openListIterator.next());
			}
			Collections.sort(newOpenList,comparatorForCut);
			if (logger.isDebugEnabled())logger.debug("newOpenList.size() = "+newOpenList.size());

			openList = new ArrayList<T>(numberToRetain);
			Iterator<T> newOpenListIterator = newOpenList.iterator();
			for (int itemsIndex=0;itemsIndex<numberToRetain && newOpenListIterator.hasNext();++itemsIndex)
			{
				T currentElement = newOpenListIterator.next();
				openList.add(currentElement);
			}
			if (logger.isDebugEnabled())logger.debug("End of while iteration. openList.size() = "+openList.size());
			if (logger.isDebugEnabled()){logger.debug("Cache size = "+cache.keySet().size());}

			
			++iterationIndex;
			if (goals.size()>0)
				++iterationIndexAfterFound;
		}
		if (goals.size()>0)
		{
			bestGoal = Collections.min(goals, bestGoalComparator);
//			Collections.sort(goals,bestGoalComparator);
//			bestGoal = goals.iterator().next();
		}
		findDone=true;
	}
	
	public List<T> getGoals() throws KStagedAlgorithmException
	{
		if (!findDone) throw new KStagedAlgorithmException("find() was not called.");
		return goals;
	}

	public T getBestGoal() throws KStagedAlgorithmException
	{
		if (!findDone) throw new KStagedAlgorithmException("find() was not called.");
		return bestGoal;
	}

	public long getNumberOfExpansions() throws KStagedAlgorithmException
	{
		if (!findDone) throw new KStagedAlgorithmException("find() was not called.");
		return numberOfExpansions;
	}

	public long getNumberOfExpensiveGenerations() throws KStagedAlgorithmException
	{
		if (!findDone) throw new KStagedAlgorithmException("find() was not called.");
		return numberOfExpensiveGenerations;
	}
	
	public long getNumberOfGenerations() throws KStagedAlgorithmException
	{
		if (!findDone) throw new KStagedAlgorithmException("find() was not called.");
		return numberOfGenerations;
	}
	
	protected void cleanCache()
	{
		Map<T,List<T>> cleanedCache = new HashMap<T, List<T>>();
		Set<T> cacheKeySet = cache.keySet();
		for (T element : openList)
		{
			if (cacheKeySet.contains(element))
			{
				List<T> children = cache.get(element);
				if (children!=null)
					cleanedCache.put(element, children);
			}
		}
		this.cache = cleanedCache;
	}






	protected List<T> initialStates;
	protected ByIterationComparator<T> comparatorForExpand;
	protected ByIterationComparator<T> comparatorForCut;
	protected StateCalculator<T> stateCalculator;

	protected int numberToExpand; // small k
	protected int numberToRetain; // big K
	protected boolean maintainClosedList;
	protected int numberOfIterationsAfterFound;
	protected Comparator<T> bestGoalComparator;
	/**
	 * <tt>true</tt> if an element picked out from the open-list, will not
	 * be part of the new open-list.
	 */
	protected boolean discardExpandedStates = false;
	
	
	
	protected List<T> openList;
	protected Set<T> closedList;
	
	protected List<T> goals;
	protected T bestGoal;
	
	protected long numberOfExpansions = 0;
	protected long numberOfExpensiveGenerations = 0;
	protected long numberOfGenerations = 0;
	protected Map<T,List<T>> cache;
	
	private boolean findDone = false;
	
	private static final Logger logger = Logger.getLogger(KStagedAlgorithm.class);
}
