package eu.excitementproject.eop.common.representation.parse.tree.match.pathmatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;

/**
 * Much like {@link AllEmbeddedMatcher}, but only for tested-trees that are paths
 * (like DIRT templates).
 * 
 * @author Asher Stern
 * @since Feb 9, 2012
 *
 */
public class PathAllEmbeddedMatcher<TM, SM extends AbstractNode<TM, SM>, TT, ST extends AbstractNode<TT, ST>>
{
	public PathAllEmbeddedMatcher(MatchCriteria<TM, TT, SM, ST> matchCriteria)
	{
		super();
		this.matchCriteria = matchCriteria;
	}
	
	public void setTrees(SM mainTree, ST testedTree) throws MatcherException
	{
		if (null==mainTree) throw new MatcherException("null==mainTree");
		if (null==testedTree) throw new MatcherException("null==testedTree");
		this.mainTree = mainTree;
		this.testedTree = testedTree;
		this.matches = null;
	}
	
	public void findMatches() throws MatcherException
	{
		matches = new ArrayList<BidirectionalMap<SM,ST>>();
		Set<SM> mainTreeNodes = AbstractNodeUtils.treeToLinkedHashSet(mainTree);
		PathMatcher<TM,SM,TT,ST> matcher = new PathMatcher<TM,SM,TT,ST>(matchCriteria);
		for (SM mainTreeNode : mainTreeNodes)
		{
			matches.addAll(matcher.findMatches(mainTreeNode, testedTree));
		}
	}
	
	public List<BidirectionalMap<SM, ST>> getMatches() throws MatcherException
	{
		if (null==matches)
			throw new MatcherException("findMatches() was not called!");
		return this.matches;
	}



	
	
	
	protected MatchCriteria<TM,TT, SM, ST> matchCriteria;
	protected SM mainTree;
	protected ST testedTree;
	protected List<BidirectionalMap<SM, ST>> matches = null;

}

