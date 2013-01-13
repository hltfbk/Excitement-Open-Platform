package eu.excitementproject.eop.common.representation.parse.tree.match.pathmatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.DummyList;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria;
import eu.excitementproject.eop.common.representation.parse.tree.match.Matcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;

/**
 * Much like {@link Matcher}, but only for tested-trees that are paths
 * (like DIRT templates).
 * 
 * @author Asher Stern
 * @since Feb 5, 2012
 *
 * @param <TM>
 * @param <SM>
 * @param <TT>
 * @param <ST>
 */
public class PathMatcher<TM, SM extends AbstractNode<TM, SM>, TT, ST extends AbstractNode<TT, ST>>
{
	public PathMatcher(MatchCriteria<TM, TT, SM, ST> matchCriteria)
	{
		super();
		this.matchCriteria = matchCriteria;
	}

	public List<BidirectionalMap<SM, ST>> findMatches(SM mainNode, ST testedNode) throws MatcherException
	{
		if (matchCriteria.nodesMatch(mainNode, testedNode))
		{
			if (!testedNode.hasChildren())
			{
				BidirectionalMap<SM, ST> retMatch = new SimpleBidirectionalMap<SM, ST>();
				retMatch.put(mainNode, testedNode);
				List<BidirectionalMap<SM, ST>> ret = Collections.singletonList(retMatch);
				return ret;
			}
			else
			{
				if (!mainNode.hasChildren())
				{
					return new DummyList<BidirectionalMap<SM,ST>>();
				}
				else
				{
					if (testedNode.getChildren().size()>mainNode.getChildren().size())
					{
						return new DummyList<BidirectionalMap<SM,ST>>();
					}
					else
					{
						if (testedNode.getChildren().size()==1)
						{
							return findMatchesForNode(mainNode,testedNode);
						}
						else if (testedNode.getChildren().size()==(1+1))
						{
							Iterator<ST> testedChildrenIterator = testedNode.getChildren().iterator();
							ST testedChild1 = testedChildrenIterator.next();
							ST testedChild2 = testedChildrenIterator.next();
							List<BidirectionalMap<SM, ST>> ret = new ArrayList<BidirectionalMap<SM,ST>>();
							for (SM child1 : mainNode.getChildren())
							{
								for (SM child2 : mainNode.getChildren())
								{
									if (child1!=child2)
									{
										if (
												(matchCriteria.edgesMatch(child1.getInfo(), testedChild1.getInfo()))
												&&
												(matchCriteria.edgesMatch(child2.getInfo(), testedChild2.getInfo()))
												)
										{
											List<BidirectionalMap<SM, ST>> forTestedChild1 = findMatchesForNode(child1,testedChild1);
											List<BidirectionalMap<SM, ST>> forTestedChild2 = findMatchesForNode(child2,testedChild2);
											List<BidirectionalMap<SM, ST>> combined = PathMatcherUtils.combineLists(forTestedChild1, forTestedChild2);
											for (BidirectionalMap<SM, ST> map : combined)
											{
												map.put(mainNode, testedNode);
											}
											ret.addAll(combined);
										}
									}
								}
							}
							return ret;
						}
						else
						{
							throw new MatcherException("This class matches templates only.");
						}
					}
				}
			}
		}
		else
		{
			return new DummyList<BidirectionalMap<SM,ST>>();
		}

	}

	private List<BidirectionalMap<SM, ST>> findMatchesForNode(SM mainNode, ST testedNode) throws MatcherException
	{
		if (matchCriteria.nodesMatch(mainNode, testedNode))
		{
			if (!testedNode.hasChildren())
			{
				BidirectionalMap<SM, ST> retMatch = new SimpleBidirectionalMap<SM, ST>();
				retMatch.put(mainNode,testedNode);
				List<BidirectionalMap<SM, ST>> ret = Collections.singletonList(retMatch);
				return ret;
			}
			else if (testedNode.getChildren().size()==1)
			{
				if (!mainNode.hasChildren())
				{
					return new DummyList<BidirectionalMap<SM,ST>>();
				}
				else
				{
					List<BidirectionalMap<SM, ST>> ret = new ArrayList<BidirectionalMap<SM,ST>>();
					ST testedChild = testedNode.getChildren().iterator().next();
					for (SM child : mainNode.getChildren())
					{
						if (matchCriteria.edgesMatch(child.getInfo(), testedChild.getInfo()))
						{
							List<BidirectionalMap<SM, ST>> forChild = findMatchesForNode(child,testedChild);
							for (BidirectionalMap<SM, ST> map : forChild)
							{
								map.put(mainNode, testedNode);
							}
							ret.addAll(forChild);
						}
					}
					return ret;
				}
			}
			else
			{
				throw new MatcherException("This class can handle only templates");
			}
			
		}
		else
		{
			return new DummyList<BidirectionalMap<SM,ST>>();
		}
	}
	

	private MatchCriteria<TM, TT, SM, ST> matchCriteria;
}
