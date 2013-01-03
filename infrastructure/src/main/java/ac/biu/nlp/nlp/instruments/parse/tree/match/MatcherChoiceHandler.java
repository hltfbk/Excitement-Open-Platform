package ac.biu.nlp.nlp.instruments.parse.tree.match;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.general.AllChoices;
import ac.biu.nlp.nlp.general.AllChoices.AllChoicesException;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.general.ChoiceHandler;
import ac.biu.nlp.nlp.general.SimpleBidirectionalMap;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;


/**
 * This class is used by {@link Matcher}. In order to use {@link Matcher} and {@link AllEmbeddedMatcher},
 * <B>you don't have to read this class.</B>
 * 
 * @author Asher Stern
 * @since Jan 16, 2011
 *
 * @param <TM>
 * @param <TT>
 * @param <SM>
 * @param <ST>
 */
public class MatcherChoiceHandler<TM, TT, SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>> implements ChoiceHandler<SM>
{
	public MatcherChoiceHandler(List<ST> testedTreeChildren,
			Map<ST, Map<SM, Set<BidirectionalMap<SM, ST>>>> childrenMatches,
			SM mainTree, ST testedTree, Set<BidirectionalMap<SM, ST>> matches)
	{
		super();
		this.testedTreeChildren = testedTreeChildren;
		this.childrenMatches = childrenMatches;
		this.mainTree = mainTree;
		this.testedTree = testedTree;
		this.matches = matches;
	}

	public void handleChoice(List<SM> choice)
	{
		Set<SM> chosenNodes = new HashSet<SM>();
		for (SM node : choice)
			chosenNodes.add(node);
		
		if (chosenNodes.size()==choice.size()) // just make sure there is no duplicate. I.e. one node in mainTree is mapped to two different nodes in testedTree
		{
			@SuppressWarnings("unchecked")
			Set<BidirectionalMap<SM, ST>>[] testedTreeChildrenMatches = (Set<BidirectionalMap<SM, ST>>[]) new Set[testedTreeChildren.size()];
			
			int index=0;
			Iterator<ST> testedTreeChildrenIterator = testedTreeChildren.iterator();
			Iterator<SM> choiceIterator = choice.iterator();
			while(testedTreeChildrenIterator.hasNext()&&choiceIterator.hasNext())
			{
				Set<BidirectionalMap<SM, ST>> allMatchesThisChild = childrenMatches.get(testedTreeChildrenIterator.next()).get(choiceIterator.next());
				testedTreeChildrenMatches[index] = allMatchesThisChild;
				++index;
			}
			if (testedTreeChildrenIterator.hasNext()||choiceIterator.hasNext())
			{
				this.exception = new MatcherException("choice length not equal to children length");
			}
			
			try
			{
				AllChoices<BidirectionalMap<SM, ST>> allChoicesChildrenMatches = new AllChoices<BidirectionalMap<SM,ST>>(testedTreeChildrenMatches, new ChildrenMatchesChoiceHadler());
				allChoicesChildrenMatches.run();
			}
			catch(AllChoicesException e)
			{
				this.exception = new MatcherException("inner AllChoices failure in MatcherChoiceHandler.", e);
			}
			

			
			
			
		}
	}
	
	public MatcherException getException()
	{
		return exception;
	}
	
	
	
	//////////////////// PRIVATE & PROTECTED //////////////////////////
	
	private class ChildrenMatchesChoiceHadler implements ChoiceHandler<BidirectionalMap<SM, ST>>
	{
		public void handleChoice(List<BidirectionalMap<SM, ST>> choice)
		{
			BidirectionalMap<SM, ST> unionMap = new SimpleBidirectionalMap<SM, ST>();
			for (BidirectionalMap<SM, ST> map : choice)
			{
				for (SM mainTreeNode : map.leftSet())
				{
					unionMap.put(mainTreeNode, map.leftGet(mainTreeNode));
				}
			}
			
			unionMap.put(mainTree, testedTree);
			matches.add(unionMap);
		}
		
	}
	
	
	protected List<ST> testedTreeChildren;
	protected Map<ST, Map<SM, Set<BidirectionalMap<SM, ST>>>> childrenMatches;
	
	protected SM mainTree;
	protected ST testedTree;
	protected Set<BidirectionalMap<SM, ST>> matches;
	
	protected MatcherException exception = null;

	
	
	
}
