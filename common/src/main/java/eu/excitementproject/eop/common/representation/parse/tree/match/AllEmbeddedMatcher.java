package eu.excitementproject.eop.common.representation.parse.tree.match;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultMatchCriteria;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;


/**
 * Finds out whether the "testedTree" is matched somewhere in "mainTree".
 * This class is similar to {@link Matcher}, actually it uses {@link Matcher}. The only difference
 * is that matcher finds all matches such that the roots of both trees match. On the contrary,
 * this class returns matches of testedTree anywhere in mainTree (i.e. testedTree may match
 * a subtree of mainTree).
 * <P>
 * Match is done according to a {@link MatchCriteria}, which should be defined by the user.
 * <P>
 * This class (like {@link Matcher}) does not only find whether there is a match. It also returns
 * the matches.
 * The matches are returned as sets of {@link BidirectionalMap}, and each map is mapping
 * from the relevant mainTree's nodes to the testedTree's nodes. Clearly, all of the testedTree's
 * nodes exist in each map, but only subset of the mainTree's nodes. 
 * 
 * 
 * @see Matcher
 * @see MatchCriteria
 * @see DefaultMatchCriteria
 *    
 * @author Asher Stern
 * @since Jan 16, 2011
 *
 * @param <TM>
 * @param <TT> 
 * @param <SM>
 * @param <ST>
 */
public class AllEmbeddedMatcher<TM, TT, SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>>
{
	
	/**
	 * Constructor with {@link MatchCriteria} defined by the user.
	 * @param matchCriteria
	 * @throws MatcherException
	 */
	public AllEmbeddedMatcher(MatchCriteria<TM,TT, SM, ST> matchCriteria) throws MatcherException
	{
		super();
		this.matchCriteria = matchCriteria;
	}

	/**
	 * Set the two trees: the mainTree in which we are looking for the "testedTree" - whether
	 * it is embedded in mainTree.
	 * @param mainTree
	 * @param testedTree
	 * @throws MatcherException
	 */
	public void setTrees(SM mainTree, ST testedTree) throws MatcherException
	{
		if (null==mainTree) throw new MatcherException("null==mainTree");
		if (null==testedTree) throw new MatcherException("null==testedTree");
		this.mainTree = mainTree;
		this.testedTree = testedTree;
		this.matches = null;
	}
	
	/**
	 * Finds the matches. This method must be called after {@link #setTrees(AbstractNode, AbstractNode)}
	 * and before {@link #getMatches()}.
	 * @throws MatcherException
	 */
	public void findMatches() throws MatcherException
	{
		matches = new LinkedHashSet<BidirectionalMap<SM,ST>>();
		for (SM mainTreeNode : TreeIterator.iterableTree(mainTree))
		{
			Matcher<TM, TT, SM, ST> matcher = new Matcher<TM, TT, SM, ST>(matchCriteria);
			matcher.setTreeRoots(mainTreeNode, testedTree);
			matcher.findMathces();
			Set<BidirectionalMap<SM, ST>> currentMatches = matcher.getMatches();
			matches.addAll(currentMatches);
		}
	}
	
	
	/**
	 * Call this method after {@link #findMatches()}, to get the results.
	 * @return a set of the matches. Never returns null. It may return an empty set.
	 * @throws MatcherException
	 */
	public Set<BidirectionalMap<SM, ST>> getMatches() throws MatcherException
	{
		if (null==matches)
			throw new MatcherException("findMatches() was not called!");
		return this.matches;
	}

	
	
	
	protected MatchCriteria<TM,TT, SM, ST> matchCriteria;
	protected SM mainTree;
	protected ST testedTree;
	protected Set<BidirectionalMap<SM, ST>> matches = null;


}
