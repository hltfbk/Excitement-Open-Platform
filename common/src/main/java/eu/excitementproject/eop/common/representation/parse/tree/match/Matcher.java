package eu.excitementproject.eop.common.representation.parse.tree.match;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultMatchCriteria;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.AllChoices;
import eu.excitementproject.eop.common.utilities.AllChoices.AllChoicesException;

/**
 * Matching two trees. Tests whether "testedTree" is embedded in "mainTree", and if it is - 
 * returns all possible mappings of their nodes.
 * <P>
 * "testedTree" plays the role of the hypothesis, or a rule's left-hand-side. I.e. you don't
 * need the two trees to be equal, but you only ask whether the testedTree can be found
 * in the mainTree.
 * <P>
 * Note that the matcher looks for matches such that <B>the roots are equal.</B>
 * If you want to find all matches, such that any node in mainTree can match the testedTree's root,
 * run {@link AllEmbeddedMatcher}
 * <BR>
 * If you are not sure what you need, I guess you need {@link AllEmbeddedMatcher}. Think about it:
 * when you are looking for the left-hand-side of a rule in a text-tree, you do not require
 * that their root will be identical and the match will include the text-tree's root. You merely
 * want to find the matches of the rule's LHS <B>somewhere</B> in the text tree.
 * 
 * @see AllEmbeddedMatcher
 * @see MatchCriteria
 * @see DefaultMatchCriteria
 * 
 * 
 * @author Asher Stern
 * @since Jan 16, 2011
 *
 * @param <TM> the information in the nodes of the main tree (e.g. {@link Info})
 * @param <TM> the information in the nodes of the tested tree (e.g. {@link Info})
 * @param <SM> the type of the mainTree nodes (e.g. {@link BasicNode})
 * @param <ST> the type of the testedTree nodes (e.g. {@link BasicNode})
 */
public class Matcher<TM,TT, SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>>
{
	/**
	 * Constructor with {@link MatchCriteria}, defined by the user.
	 * @param matchCriteria
	 * @throws MatcherException
	 */
	public Matcher(MatchCriteria<TM,TT, SM, ST> matchCriteria) throws MatcherException
	{
		super();
		if (null==matchCriteria) throw new MatcherException("matchCriteria is null");
		this.matchCriteria = matchCriteria;
	}

	
	/**
	 * Sets the two trees. Later, when calling to {@link #findMathces()}, it will
	 * look for all occurrences of testedTree in mainTree, such that the match starts 
	 * at the root (the roots must be equal, and all the matches start in the roots (mapping
	 * the mainTree's root to testedTree's root)).
	 * 
	 * @param mainTree the tree in which we are looking for the testedTree to be embedded
	 * @param testedTree the tree which we are looking for whether it is embedded in the
	 * mainTree
	 * @throws MatcherException
	 */
	public void setTreeRoots(SM mainTree, ST testedTree) throws MatcherException
	{
		if (null==mainTree) throw new MatcherException("main tree is null");
		if (null==testedTree) throw new MatcherException("tested tree is null");
		this.mainTree = mainTree;
		this.testedTree = testedTree;
		matches = null;
	}
	
	/**
	 * Finds the matches. Must be called after {@link #setTreeRoots(AbstractNode, AbstractNode)},
	 * and before {@link #getMatches()}
	 * @throws MatcherException
	 */
	public void findMathces() throws MatcherException
	{
		matches = new LinkedHashSet<BidirectionalMap<SM,ST>>();
		
		boolean rootsMatch = matchCriteria.nodesMatch(mainTree, testedTree);
		if (rootsMatch)
		{
			if (collectionEmpty(testedTree.getChildren()))
			{
				BidirectionalMap<SM, ST> map = new SimpleBidirectionalMap<SM, ST>();
				map.put(mainTree, testedTree);
				matches.add(map);
			}
			else
			{
				if (collectionEmpty(mainTree.getChildren()))
					;
				else
				{
					try
					{
						matchChildren();
					}
					catch (AllChoicesException e)
					{
						throw new MatcherException("AllChoices failure during Matcher.findMatches()",e);
					}
				}
			}
		}
	}
	
	public Set<BidirectionalMap<SM, ST>> getMatches() throws MatcherException
	{
		if (null==matches)
			throw new MatcherException("findMatches() was not called!");
		return this.matches;
	}
	
	
	
	protected void matchChildren() throws MatcherException, AllChoicesException
	{
		// this method assumes the children of both trees are not empty
		Map<ST, Map<SM, Set<BidirectionalMap<SM, ST>>>> childrenMatches = new LinkedHashMap<ST, Map<SM,Set<BidirectionalMap<SM,ST>>>>();
		for (ST testedTreeChild : testedTree.getChildren())
		{
			childrenMatches.put(testedTreeChild, new LinkedHashMap<SM, Set<BidirectionalMap<SM,ST>>>());
			for (SM mainTreeChild : mainTree.getChildren())
			{
				if (matchCriteria.edgesMatch(mainTreeChild.getInfo(), testedTreeChild.getInfo()))
				{
					Matcher<TM,TT,SM,ST> childrenMatcher = new Matcher<TM, TT, SM, ST>(matchCriteria);
					childrenMatcher.setTreeRoots(mainTreeChild, testedTreeChild);
					childrenMatcher.findMathces();
					Set<BidirectionalMap<SM, ST>> currentMatches = childrenMatcher.getMatches();
					if (currentMatches.size()>0)
					{
						childrenMatches.get(testedTreeChild).put(mainTreeChild, currentMatches);
					}
				}
			}
		}

		boolean everyChildHasMatch = true;
		for (ST testedTreeChild : testedTree.getChildren())
		{
			if (childrenMatches.get(testedTreeChild).size()==0)
				everyChildHasMatch = false;
		}
		
		
		if (everyChildHasMatch)
		{
			@SuppressWarnings("unchecked")
			Set<SM>[] matchingSubteesPerChild = (Set<SM>[]) new Set[testedTree.getChildren().size()];
			int index=0;
			for (ST testedTreeChild : testedTree.getChildren())
			{
				matchingSubteesPerChild[index] = childrenMatches.get(testedTreeChild).keySet();
				++index;
			}
			
			
			MatcherChoiceHandler<TM, TT, SM, ST> matcherChoiceHandler = new MatcherChoiceHandler<TM, TT, SM, ST>(testedTree.getChildren(), childrenMatches, mainTree, testedTree, matches);
			AllChoices<SM> allChoices = new AllChoices<SM>(matchingSubteesPerChild,matcherChoiceHandler);
			allChoices.run();
			if (matcherChoiceHandler.getException()!=null)
				throw matcherChoiceHandler.getException();
		}
	}
	
	
	private static boolean collectionEmpty(Collection<?> collection)
	{
		boolean ret = true;
		if (collection!=null)if (collection.size()>0) ret = false;
		return ret;
	}
	
	
	
	
	
	protected MatchCriteria<TM,TT, SM, ST> matchCriteria;
	protected SM mainTree;
	protected ST testedTree;
	protected Set<BidirectionalMap<SM, ST>> matches = null;
}
