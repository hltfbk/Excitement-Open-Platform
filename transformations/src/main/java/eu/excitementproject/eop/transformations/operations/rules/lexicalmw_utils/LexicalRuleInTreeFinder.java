package eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfRulesWithConfidenceAndDescription;


/**
 * 
 * @author Asher Stern
 * @since Jul 4, 2011
 *
 */
public class LexicalRuleInTreeFinder
{
	///////////////////////// PUBLIC ///////////////////////////
	
	/**
	 * A comparator for parse-tree-nodes that treats:
	 * <I>nodeA is less than nodeB</I> iff the "serial" or nodeA is less than the
	 * "serial" of nodeB. "Serial" is the location in the sentence. The first word
	 * in the sentence has serial 1, the second word has serial 2, etc.
	 *
	 * @param <T> The information on each node (e.g. ac.biu.nlp.nlp.instruments.parse.representation.english.Info)
	 * @param <S> The nodes (e.g. BasicNode)
	 */
	public static class BySerialComparator<T extends Info, S extends AbstractNode<T,S>> implements Comparator<S>
	{
		public int compare(S o1, S o2)
		{
			boolean serial1Found = false;
			boolean serial2Found = false;
			int serial1=0;
			int serial2=0;
			try
			{
				serial1 = o1.getInfo().getNodeInfo().getSerial();
				serial1Found=true;
			}catch(NullPointerException e){}
			try
			{
				serial2 = o2.getInfo().getNodeInfo().getSerial();
				serial2Found=true;
			}catch(NullPointerException e){}

			if (serial1Found&&serial2Found)
			{
				if (serial1<serial2)return-1;
				else if (serial1==serial2)return 0;
				else return 1;
			}
			else
			{
				if (serial1Found)return -1;
				else if (serial2Found) return 1;
				else return 0;
			}
		}
	}
	
	/**
	 * Given a sub-tree - returns all its nodes as a list, sorted by their "serial"
	 * 
	 * @param <T> The information on each node (e.g. ac.biu.nlp.nlp.instruments.parse.representation.english.Info)
	 * @param <S> The nodes (e.g. EnglishNode)
	 * @param tree The given sub-tree.
	 * @return The nodes of the sub-tree as a sorted list (sorted by "serial" field of the nodes).
	 */
	public static <T extends Info, S extends AbstractNode<T,S>> List<S> treeToList(S tree)
	{
		Set<S> set = AbstractNodeUtils.treeToLinkedHashSet(tree);
		List<S> list = new ArrayList<S>(set.size());
		list.addAll(set);
		Collections.sort(list, new BySerialComparator<T,S>());
		
		return list;
	}
	
	/**
	 * Given a right-hand-side of a <B>lexical rule</B> - given as a lexical
	 * expression - which is a list of lemmas, and given list of parse-tree-nodes:
	 * this class finds out whether there is a "match" between the given rule's
	 * right-hand-side and a sub-list of the given nodes.
	 * <BR>
	 * A "match" is defined as a sub-list of nodes such that their lemmas are
	 * equal to the lemmas of the rule's right-hand-side (in the same order).
	 * For example: if the rule's right-hand-side is ["Irish", "Republican", "Army"], and
	 * there is a sub-list of nodes that their lemmas are "Irish", "Republican", "Army"
	 * (in that order) - then they match.
	 * 
	 * @param allNodes list of nodes, should be list of all nodes of a tree
	 * or sub-tree, sorted according to their appearance in the original sentence -
	 * 
	 * that sort is accomplished by using the <code>Comparator</code> {@link BySerialComparator}.
	 * @param rule The right-hand-side of the rule, which is just a lexical
	 * expression, given as a list of lemmas. The normal usage is for right-hand-side,
	 * though this function can work for left-hand-side as well.
	 * 
	 * @return All matches found, such that a match is a sub-list of the given list of nodes.
	 */
	public List<List<BasicNode>> findRuleAsList(List<BasicNode> allNodes, List<String> rule)
	{
		List<List<BasicNode>> ret = new Vector<List<BasicNode>>();
		BasicNode[] nodesArray = allNodes.toArray(new BasicNode[0]);
		String[] ruleArray = rule.toArray(new String[0]);
		
		if (ruleArray.length>nodesArray.length)
		{
			// do nothing
		}
		else
		{
			for (int nodesIndex=0;nodesIndex<(nodesArray.length-ruleArray.length+1);nodesIndex++)
			{
				boolean mismatchFound=false;
				for (int ruleIndex=0;ruleIndex<ruleArray.length;++ruleIndex)
				{
					String lemma = InfoGetFields.getLemma(nodesArray[nodesIndex+ruleIndex].getInfo());
					if (lemma.equalsIgnoreCase(ruleArray[ruleIndex]))
					{
						// do nothing
					}
					else
					{
						mismatchFound=true;
					}
						
				}
				if (!mismatchFound)
				{
					List<BasicNode> matchAsList = new ArrayList<BasicNode>(ruleArray.length);
					for (int ruleIndex=0;ruleIndex<ruleArray.length;++ruleIndex) 
					{
						matchAsList.add(nodesArray[nodesIndex+ruleIndex]);
					}
					ret.add(matchAsList);
				}
			}
			
		}
		return ret;
	}

	/**
	 * Given a lexical rule and the hypothesis tree, this function creates 
	 * a rule identical to the given lexical rule, but now with the right-hand-side
	 * being a sub-tree instead of a lexical expression (string). The rule is
	 * created if and only if the right hand side can be found in the hypothesis.
	 * 
	 * @param lexicalRule
	 * @param hypothesisTree
	 * @param ruleBaseName
	 * @return List of all rules that can be created (the list size is larger than 1
	 * if the right hand side is matched in more than one location in the hypotehsis tree).
	 */
	public List<RuleWithConfidenceAndDescription<Info, BasicNode>> findRule(LexicalRule lexicalRule, BasicNode hypothesisTree, String ruleBaseName)
	{
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> ret = null;
		String[] rhsWords = lexicalRule.getRhsLemma().split("\\s+");
		if (rhsWords.length<2)
			ret=null;
		else
		{
			List<BasicNode> hypothesisTreeNodes = treeToList(hypothesisTree);
			List<String> ruleAsList = Utils.arrayToCollection(rhsWords, new ArrayList<String>(rhsWords.length));
			
			List<List<BasicNode>> matches = findRuleAsList(hypothesisTreeNodes,ruleAsList);
			if (matches.size()>0)
			{
				ret = new Vector<RuleWithConfidenceAndDescription<Info,BasicNode>>();
				for (List<BasicNode> match : matches)
				{
					BasicNode ruleHead = ListNodesToTree.findHead(hypothesisTree, match);
					BasicNode ruleRHS = ListNodesToTree.createTree(ruleHead, match);

					BasicNode ruleLHS = new BasicNode(new DefaultInfo("lhs",
							new DefaultNodeInfo(lexicalRule.getLhsLemma(), lexicalRule.getLhsLemma(), 0, null, new DefaultSyntacticInfo(lexicalRule.getLhsPos())),
							new DefaultEdgeInfo(new DependencyRelation("", null))));

					BidirectionalMap<BasicNode, BasicNode> map = new SimpleBidirectionalMap<BasicNode, BasicNode>();
					map.put(ruleLHS,ruleRHS);

					SyntacticRule<Info,BasicNode> rule = new SyntacticRule<Info, BasicNode>(ruleLHS, ruleRHS, map);
					
					String description = ruleBaseName+": "+ lexicalRule.getLhsLemma()+" => "+lexicalRule.getRhsLemma();
					
					RuleWithConfidenceAndDescription<Info,BasicNode> rwcad =
						new RuleWithConfidenceAndDescription<Info, BasicNode>(rule, lexicalRule.getConfidence(), description);

					// TODO Get rid of this RTTI
					if (lexicalRule instanceof ChainOfLexicalRules)
					{
						ChainOfLexicalRules chainOfLexicalRules = (ChainOfLexicalRules) lexicalRule;
						rwcad = ChainOfRulesWithConfidenceAndDescription.fromChainOfLexicalRules(chainOfLexicalRules, rwcad);
					}

					ret.add(rwcad);
				}
			}
		}
		return ret;
	}
	
}
