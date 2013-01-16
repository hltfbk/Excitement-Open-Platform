package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A generic implementation of A* algorithm, using priority queue.
 * 
 * @author Asher Stern
 * @since Jun 17, 2011
 *
 */
public class AStarAlgorithm<T extends Comparable<T>>
{
	///////////////////// NESTED STATIC CLASSES AND INTERFACES ////////////////////
	@SuppressWarnings("serial")
	public static class AStarException extends Exception
	{
		public AStarException(String message){super(message);}
		public AStarException(String message, Throwable t){super(message,t);}
	}
	
	/**
	 * Provides method related to a single element (state) in the search.
	 * The main method is: Given a state - create all its children. For example,
	 * in a square-puzzle - given a square (a state) that the agent is currently standing on,
	 * return all squares near that square that the agent can go to.
	 * 
	 * @author Asher Stern
	 * @since Aug 8, 2011
	 *
	 * @param <T>
	 */
	public static interface StateCalculations<T>
	{
		/**
		 * Return <tt>true</tt> if the given state is a goal state.
		 * @param state
		 * @return
		 * @throws AStarException
		 */
		public boolean isGoal(T state) throws AStarException;
		/**
		 * Returns <tt>true</tt> if there is no need (for the {@link StateCalculations} object)
		 * to create the children for this state, since they are already known.
		 * I.e. in the next call to {@link #getChildren(Object, Set)} for this
		 * state, no work will be done: The relevant children are already known,
		 * and they will be returned.
		 * <P>
		 * In simple words: If it returns <tt>true</tt> then it means that
		 * children generation is not expensive
		 * @param state
		 * @param cloasedSet
		 * @return
		 */
		public boolean stateChildrenAlreadyKnown(T state, Set<T> cloasedSet);
		
		/**
		 * Given a state - return all "children" or "neighbors" - all states that can come as
		 * "the next state" in the search path.
		 * 
		 * @param state
		 * @param cloasedSet a "closed set" of states that the algorithm has already visited.
		 * Implementations of {@link StateCalculations} can ignore this parameter.
		 * @return all "children" or "neighbors" - all states that can come as
		 * "the next state" in the search path.
		 * @throws AStarException
		 */
		public List<T> getChildren(T state, Set<T> cloasedSet) throws AStarException;
	}
	
	/**
	 * This is only used by "smart any time" mode, which seem to be logically wrong, and
	 * has been proven to have bad performance empirically.
	 * <P>
	 * Don't use this interface - just forget it.
	 * 
	 * @author Asher Stern
	 * @since Aug 8, 2011
	 *
	 * @param <T>
	 */
	public static interface StateManipulator<T>
	{
		public T makeManipulation(T state);
		public StateCalculations<T> getNewStateCalculations();
	}

	/////////////////////// PUBLIC CONSTRUCTORS AND METHODS //////////////////////
	
	
	/**
	 * Constructor with:
	 * <UL>
	 * <LI>"starting states" - the state or states that the search starts from</LI>
	 * <LI>"state calculations" a class that creates all possible "next states" for a given state</LI>
	 * <LI>"comparatorByCostOnly" - which is used as follows: if the algorithm found several goal
	 * states, then the one to be returned by the algorithm is the one with "the lowest cost" - and
	 * "lowest cost" is defined by this comparator.
	 * </UL> 
	 * 
	 * @param startStates the state or states that the search starts from
	 * @param stateCalculations "state calculations" a class that creates all possible
	 * "next states" for a given state
	 * @param comparatorByCostOnly "lowest cost" is defined by this comparator.
	 * 
	 * @throws AStarException
	 */
	public AStarAlgorithm(Set<T> startStates, StateCalculations<T> stateCalculations, Comparator<T> comparatorByCostOnly) throws AStarException
	{
		super();
		if (null==startStates) throw new AStarException("Null startStates");
		if (null==stateCalculations) throw new AStarException("Null stateCalculations");
		if (null==comparatorByCostOnly) throw new AStarException("Null comparatorByCostOnly");
		this.startStates = startStates;
		this.stateCalculations = stateCalculations;
		this.comparatorByCostOnly = comparatorByCostOnly;
	}
	
	/**
	 * In each iteration, i.e. each time that the algorithm "polls" a state from the queue -
	 * don't poll only one element, but K elements. Default - 1.
	 * 
	 * @param k_expandInEachIteration
	 */
	public void setK_expandInEachIteration(int k_expandInEachIteration)
	{
		this.k_expandInEachIteration = k_expandInEachIteration;
	}
	
	/**
	 * Any time - even though the algorithm found the goal state, it continues to iterate
	 * and poll elements from the queue as if no goal was found. After
	 * <code>anyTime_numberOfGoalStates</code> goal states were found - stop.
	 * 
	 * @param anyTime_numberOfGoalStates
	 */
	public void setAnyTime_numberOfGoalStates(int anyTime_numberOfGoalStates)
	{
		this.anyTime_numberOfGoalStates = anyTime_numberOfGoalStates;
	}
	
	/**
	 * Forces the algorithm to stop, even though no goal was found, if number of generations
	 * exceeded <code>maxNumberOfGenerations</code>.
	 * "generation" is a creation of a child. In each iteration the algorithm polls an element
	 * from the queue, and generates (i.e. creates) its children (i.e. states that can follow the
	 * current state in the search path).
	 * @param maxNumberOfGenerations
	 */
	public void setMaxNumberOfGenerations(long maxNumberOfGenerations)
	{
		this.maxNumberOfGenerations = maxNumberOfGenerations;
	}
	
	/**
	 * This is similar to {@link #setMaxNumberOfGenerations(long)}. The difference is that
	 * if the current state has "cached" children - i.e. in the implementation, that states
	 * "cache" their children, then the generations for this current state are not counted.
	 * @see StateCalculations#stateChildrenAlreadyKnown(Object, Set).
	 * 
	 * @param maxNumberOfExpensiveGenerations an upper limit on the number of expensive
	 * generations allowed.
	 */
	public void setMaxNumberOfExpensiveGenerations(long maxNumberOfExpensiveGenerations)
	{
		this.maxNumberOfExpensiveGenerations = maxNumberOfExpensiveGenerations;
	}

	/**
	 * This is a bad idea and is no longer used. Forget it.
	 * 
	 * @param stateManipulator
	 * @param maxNumberOfGenerations
	 * @throws AStarException
	 */
	public void useSmartAnyTime(StateManipulator<T> stateManipulator, long maxNumberOfGenerations) throws AStarException
	{
		if (null==stateManipulator) throw new AStarException("Null");
		this.stateManipulator = stateManipulator;
		this.maxNumberOfGenerations = maxNumberOfGenerations;
	}
	
	/**
	 * Directs the algorithm to poll all elements from the queue that are equal to the top.
	 * The default is to take only the top.
	 * If K was set by {@link #setK_expandInEachIteration(int)}, then first the algorithm
	 * polls K elements, and then all elements that are equal to the last element of those K
	 * elements.
	 * <BR>
	 * Default - false 
	 * @param whenEqualTakeAll
	 */
	public void setWhenEqualTakeAll(boolean whenEqualTakeAll)
	{
		this.whenEqualTakeAll = whenEqualTakeAll;
	}

	/**
	 * <B>This is the main method</B> - find the goal state by A* algorithm.
	 * <P>
	 * Later, that goal state can be retrieved by {@link #getFoundGoalState()}.
	 * @throws AStarException
	 */
	public void find() throws AStarException
	{
		if (null==this.stateManipulator)
		{
			regularFind();
		}
		else
		{
			smartAnyTimeModeFind();
		}
	}
	

	/**
	 * Returns <tt>true</tt> if the goal state was found.
	 * <BR>
	 * Note that if the search-algorithm was directed to find more than one goal, by
	 * calling {@link #setAnyTime_numberOfGoalStates(int)}, then only if <B>all of</B>
	 * the required goal states were found, this method returns <tt>trure</tt>.
	 * Use {@link #isAnyGoalFound()} to find out whether at least one goal state was found.
	 *  
	 * @return
	 * @throws AStarException
	 */
	public boolean isFound() throws AStarException
	{
		if(!searchDone) throw new AStarException("Search not done.");
		return found;
	}
	
	public boolean isAnyGoalFound() throws AStarException
	{
		if(!searchDone) throw new AStarException("Search not done.");
		return (foundGoalStates.size()>0);
	}
	
	public T getFoundGoalState() throws AStarException
	{
		if(!searchDone) throw new AStarException("Search not done.");
		if (foundGoalStates.size()==0) throw new AStarException("Goal was not found.");
		// assuming the list is sorted
		return foundGoalStates.get(0);
	}
	
	public long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}

	public long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}
	
	public long getNumberOfExpensiveGeneratedElements()
	{
		return numberOfExpansiveGeneratedElements;
	}
	
	public boolean isEndedWithEmptyQueue()
	{
		return endedWithEmptyQueue;
	}
	
	///////////////////// PROTECTED & PRIVATE /////////////////////////////

	protected void regularFind() throws AStarException
	{
		logger.info("Starting search");
		closedSet = new LinkedHashSet<T>();
		
		createInitialQueue();
		searchGivenQueue();
		
		logger.info("Search done. Queue size = "+priorityQueue.size()+". Number of goal states = "+foundGoalStates.size());
		searchDone=true;
	}
	
	protected void smartAnyTimeModeFind() throws AStarException
	{
		logger.info("Starting search (smart any time mode)");
		closedSet = new LinkedHashSet<T>();
		
		if (this.maxNumberOfGenerations<0) throw new AStarException("maxNumberOfGenerations<0 Not allowed in this mode.");
		createInitialQueue();
		
		long realMaxNumberOfGenerations = this.maxNumberOfGenerations;
		List<T> allFoundGoalStates = new ArrayList<T>();
		
		this.maxNumberOfGenerations=-1;
		searchGivenQueue();
		logger.debug("In iteration 0 found "+foundGoalStates.size()+" goals.");
		allFoundGoalStates.addAll(foundGoalStates);
		
		
		this.maxNumberOfGenerations = realMaxNumberOfGenerations;
		int numberOfIterations = 1;
		while (numberOfGeneratedElements<this.maxNumberOfGenerations)
		{
			this.stateCalculations = this.stateManipulator.getNewStateCalculations();
			PriorityQueue<T> newQueue = new PriorityQueue<T>();
			for (T state : this.priorityQueue)
			{
				newQueue.add(stateManipulator.makeManipulation(state));
			}
			this.priorityQueue = newQueue;
			searchGivenQueue();
			logger.debug("In iteration "+numberOfIterations+" found "+foundGoalStates.size()+" goals.");
			allFoundGoalStates.addAll(foundGoalStates);
			numberOfIterations++;
		}
		Collections.sort(allFoundGoalStates,comparatorByCostOnly);
		
		
		
		
		this.foundGoalStates = allFoundGoalStates;
		this.found = true; // Must be true after the first iteration, we can ignore the last iteration. 
		
		logger.info("Search done, with "+numberOfIterations+" iterations, and "+foundGoalStates.size()+" goal states.");
		searchDone=true;
	}

	
	protected void createInitialQueue()
	{
		priorityQueue = new PriorityQueue<T>();
		for (T startState : startStates)
		{
			priorityQueue.offer(startState);
		}
	}
	
	protected void searchGivenQueue() throws AStarException
	{
		foundGoalStates = new ArrayList<T>(anyTime_numberOfGoalStates);
		found = false;
		boolean maxNumberOfGenerationsExceeded = false;
		while ( (!found) && (priorityQueue.size()>0) && (!maxNumberOfGenerationsExceeded) )
		{
			T lastStatePolled = null;
			List<T> currentBestStates = new ArrayList<T>(k_expandInEachIteration);
			int kIndex=0;
			for (kIndex=0;( (kIndex<k_expandInEachIteration) && (priorityQueue.size()>0) ); kIndex++)
			{
				T currentBestState = priorityQueue.poll();
				lastStatePolled = currentBestState;
				if (null==currentBestState)throw new AStarException("BUG");
				currentBestStates.add(currentBestState);
			}
			if ( (lastStatePolled!=null) && (whenEqualTakeAll) )
			{
				boolean stop = false;
				if (priorityQueue.size()==0) stop = true;
				while (!stop)
				{
					T currentTop = priorityQueue.peek();
					if (currentTop.compareTo(lastStatePolled)<0)throw new AStarException("BUG");
					if (currentTop.compareTo(lastStatePolled)>0)
					{
						stop = true;
					}
					else
					{
						T currentBestState = priorityQueue.poll();
						if (null==currentBestState)throw new AStarException("BUG");
						currentBestStates.add(currentBestState);
					}
					if (priorityQueue.size()==0) stop = true;
				}
			}
			for (T currentBestState : currentBestStates)
			{
//				if (logger.isDebugEnabled())
//				{
//					AStarElement ase = (AStarElement)currentBestState;
//					String lastSpecDesc = "-";
//					if (ase.getHistory().getSpecifications().size()>0)
//					{
//						lastSpecDesc = ase.getHistory().getSpecifications().get(ase.getHistory().getSpecifications().size()-1).toString();
//					}
//					logger.debug(lastSpecDesc);
//				}

				++numberOfExpandedElements;
				closedSet.add(currentBestState);
				boolean currentIsGoal = false;
				if (stateCalculations.isGoal(currentBestState))
				{
					logger.debug("A goal state was found");
					currentIsGoal = true;
					foundGoalStates.add(currentBestState);
					if (foundGoalStates.size()>=anyTime_numberOfGoalStates)
						found=true;
				}
				if ( (!found) && (!currentIsGoal) )
				{
					boolean childrenGenerationIsExpansive = !stateCalculations.stateChildrenAlreadyKnown(currentBestState,closedSet);
					List<T> children = stateCalculations.getChildren(currentBestState,closedSet);
					numberOfGeneratedElements+=children.size();
					if (childrenGenerationIsExpansive)
					{
						this.numberOfExpansiveGeneratedElements+=children.size();
					}
					for (T child : children)
					{
						priorityQueue.offer(child);
					}
				}
			}
			if (this.maxNumberOfGenerations>=0) // if it is -1 then it means to ignore it (default).
			{
				if (numberOfGeneratedElements>maxNumberOfGenerations)
				{
					maxNumberOfGenerationsExceeded = true;
				}
			}
			if (this.maxNumberOfExpensiveGenerations>=0) // if it is -1 then it means to ignore it (default).
			{
				if (numberOfExpansiveGeneratedElements>maxNumberOfExpensiveGenerations)
				{
					maxNumberOfGenerationsExceeded = true;
				}
			}
		}
		if (priorityQueue.size()==0)
		{
			endedWithEmptyQueue = true;
		}
		
		Collections.sort(foundGoalStates,comparatorByCostOnly);
	}






	private Set<T> startStates;
	private StateCalculations<T> stateCalculations;
	private Comparator<T> comparatorByCostOnly;
	
	private PriorityQueue<T> priorityQueue;
	private Set<T> closedSet;
	private List<T> foundGoalStates;
	private boolean found=false;
	
	private long numberOfExpandedElements = 0;
	private long numberOfGeneratedElements = 0;
	private long numberOfExpansiveGeneratedElements = 0;
	private boolean endedWithEmptyQueue = false;
	
	
	private int k_expandInEachIteration = 1;
	private int anyTime_numberOfGoalStates = 1;
	private long maxNumberOfGenerations = -1; 
	private long maxNumberOfExpensiveGenerations = -1;
	private boolean whenEqualTakeAll = false;
	
	private StateManipulator<T> stateManipulator = null;
	
	private boolean searchDone = false;
	
	private static final Logger logger = Logger.getLogger(AStarAlgorithm.class);
}
