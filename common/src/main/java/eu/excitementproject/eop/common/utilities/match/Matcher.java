package eu.excitementproject.eop.common.utilities.match;

import java.util.Iterator;

import eu.excitementproject.eop.common.datastructures.DuplicateableIterator;
import eu.excitementproject.eop.common.datastructures.SimpleDuplicateableIterator;



/**
 * The <code>Matcher</code> class finds the matches between two sequences' elements, and activates
 * an operation on each matched pair.
 * <P>
 * <B>What does it mean:</B><BR>
 * Given two lists {1,2,3,4} and {10,20,30,40}. Let's decide that x in the first list matches 10x of
 * the second list.
 * In addition, let's define the operation as System.out.println(x);
 * <BR>
 * Now, the Matcher will find the matches: (1 and 10), (2 and 20), (3 and 30), (4 and 40). The result
 * will be the following output:
 * <pre>
 * 1
 * 2
 * 3
 * 4
 * </pre>
 * <P>
 * Now, what if the second list is only {10,20,40}? <B>The Matcher is smart</B>, and will find the
 * matches (1 and 10), (2 and 20), (4 and 40). The output will be:
 * <pre>
 * 1
 * 2
 * 4
 * </pre>
 * <P>
 * And, even if the second list is: {10,20,10,40}<B> The Matcher is smart</B>, and will find the
 * matches (1 and 10), (2 and 20), (4 and 40). The output will be:
 * <pre>
 * 1
 * 2
 * 4
 * </pre>
 * <P>
 * And, even if the first list is: {1,3,2,4} and the second is {30,40,50}, the matches that will be
 * found are: (3 and 30), (4 and 40).
 * <P>
 * <B>Now, what is it used for?</B><BR>
 * Here is an example. We want to merge parser output with NER output. if we have the sentence:
 * "Kine is gonna kill Hevel". The parser and the NER will tokenize the sentence into similar lists
 * of tokens, but not identical. The parser may token to: "Kine", "is", "gon", "na", "kill", "Hevel".
 * The NER may tokenize and output NEs like that: "Kine/PERSON", "is", "gonna", "kill", "Hevel/PERSON".
 * We have to merge them.<BR>
 * The matcher, given those to tokens lists, will find the following matches:
 * (Kine and Kine), (is and is), (kill and kill), (Hevel and Hevel). This gives us the ability to merge
 * the outputs.
 * <P>
 * <P>
 * <B>USAGE</B>
 * <OL>
 * <LI>Give iterator over the first sequence's elements. Those elements are of type <code>T</code></LI>
 * <LI>Give iterator over the second sequence's elements. Those elements are of type <code>U</code></LI>
 * <LI>Define a predicate {@linkplain MatchFinder}, which - given an element of type <code>T</code>
 * and an element of type <code>U</code> - decides whether they match or not.</LI>
 * <LI>Define an {@linkplain Operator}, which - given two elements or types T and U - does something
 * with them.</LI>
 * <LI>All the above are given in {@linkplain Matcher}'s constructor</LI>
 * <LI>Call {@linkplain #makeMatchOperation()} to find the matches and activate the {@linkplain Operator}
 * on them.</LI>
 * <LI></LI>
 * </OL>
 * 
 * 
 * 
 * @author Asher Stern
 *
 * @param <T> type of first sequence's element
 * @param <U> type of second sequence's element
 */
public class Matcher<T,U>
{
	
	////////////////////// public part /////////////////////////////

	
	/**
	 * Constructs the {@linkplain Matcher}. The {@link Matcher} does nothing until
	 * {@link #makeMatchOperation()} is called.
	 * 
	 * @param lhsIterator iterator over the first sequence.
	 * @param rhsIterator iterator over the second sequence.
	 * @param matchFinder A predicate that decides whether two elements of types <code>T</code>
	 * and <code>U</code> match or not.
	 * @param operator An operation to be done on two matched elements. Note that if an element is
	 * mutable - the operator may operate on that element (i.e. change it, add information into it, etc.). 
	 */
	public Matcher(Iterator<T> lhsIterator, Iterator<U> rhsIterator,MatchFinder<T,U> matchFinder,Operator<T,U> operator)
	{
		this.lhsIteratorForward = new SimpleDuplicateableIterator<T>(lhsIterator);
		this.rhsIteratorForward = new SimpleDuplicateableIterator<U>(rhsIterator);

		this.matchFinder = matchFinder;
		this.operator = operator;
	}
	
	/**
	 * Finds all the matches, and activates the operation, defined by
	 * {@linkplain Operator} on them. 
	 */
	public void makeMatchOperation()
	{
		while (lhsIteratorForward.hasNext() && rhsIteratorForward.hasNext())
		{
			this.lhsIteratorBase = this.lhsIteratorForward.duplicate();
			this.rhsIteratorBase = this.rhsIteratorForward.duplicate();

			T lhsElement = lhsIteratorForward.next();
			U rhsElement = rhsIteratorForward.next();
			matchFinder.set(lhsElement, rhsElement);
			if (matchFinder.areMatch())
			{
				operator.set(lhsElement, rhsElement);
				operator.makeOperation();
			}
			else
			{
				steps = 1;
				boolean found = false;
				boolean nowLeft = true;
				while ( (!found) && (lhsIteratorForward.hasNext() || rhsIteratorForward.hasNext()))
				{
					if (nowLeft && lhsIteratorForward.hasNext())
						found = makeMatchOperationFromLeft();
					else if (rhsIteratorForward.hasNext())
						found = makeMatchOperationFromRight();
					
					if (!nowLeft)
						steps++;

					
					nowLeft = !nowLeft;
				}
				
				
			}
		}
	}
	
	//////////////////////// protected part //////////////////////////
	
	protected boolean makeMatchOperationFromLeft()
	{
		T lhsElement = lhsIteratorForward.next();
		DuplicateableIterator<U> localRhsBase = rhsIteratorBase.duplicate();
		boolean found = false;
		int stepsIndex = 0;
		while ( (!found) && (localRhsBase.hasNext()) && (stepsIndex<steps) )
		{
			U rhsElement = localRhsBase.next();
			matchFinder.set(lhsElement, rhsElement);
			if (matchFinder.areMatch())
			{
				operator.set(lhsElement, rhsElement);
				operator.makeOperation();
				found = true;
				this.rhsIteratorForward = localRhsBase.duplicate();
			}
			
			stepsIndex++;
		}
		return found;
		
	}
	
	protected boolean makeMatchOperationFromRight()
	{
		U rhsElement = rhsIteratorForward.next();
		DuplicateableIterator<T> localLhsBase = lhsIteratorBase.duplicate();
		boolean found = false;
		int stepsIndex = 0;
		while ( (!found) && (localLhsBase.hasNext()) && (stepsIndex<steps) )
		{
			T lhsElement = localLhsBase.next();
			matchFinder.set(lhsElement, rhsElement);
			if (matchFinder.areMatch())
			{
				operator.set(lhsElement, rhsElement);
				operator.makeOperation();
				found = true;
				this.lhsIteratorForward = localLhsBase.duplicate();
			}
			
			stepsIndex++;
		}
		return found;

		
	}
	
	
	protected DuplicateableIterator<T> lhsIteratorBase;
	protected DuplicateableIterator<U> rhsIteratorBase;
	protected DuplicateableIterator<T> lhsIteratorForward;
	protected DuplicateableIterator<U> rhsIteratorForward;

	protected MatchFinder<T,U> matchFinder;
	protected Operator<T,U> operator;
	
	protected int steps = 0;

}

