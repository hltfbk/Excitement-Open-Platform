package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.IdLemmaPosRelNodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.lap.biu.PreprocessUtilities;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.AbstractMiniparParser;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeNodeString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
//import eu.excitementproject.eop.transformations.rteflow.macro.SingleTreeEvaluations;
//import eu.excitementproject.eop.transformations.rteflow.micro.OperationsEnvironment;


/**
 * 
 * @author Asher Stern
 * @since Dec 29, 2010
 *
 */
public class TreeUtilities
{
	public static String treeToString(ExtendedNode tree) throws TreeStringGeneratorException
	{
		TreeStringGenerator<ExtendedInfo> tsg = new TreeStringGenerator<ExtendedInfo>(new ExtendedNodeNodeString(), tree);
		return tsg.generateString();
	}

	public static String treeToString(BasicNode tree) throws TreeStringGeneratorException
	{
		TreeStringGenerator<Info> tsg = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(), tree);
		return tsg.generateString();
	}

	/**
	 * Returns a set of nodes in <tt>hypothesis</tt> that have no matching node in <tt>text</tt>
	 * @param text
	 * @param hypothesis
	 * @return
	 */
	public static Set<ExtendedNode> findNodesNoMatch(TreeAndParentMap<ExtendedInfo,ExtendedNode> text, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis)
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToSet(text.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToSet(hypothesis.getTree());
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			boolean found = false;
			for (ExtendedNode textNode : textNodes)
			{
				if (Equalities.areEqualNodes(hypothesisNode.getInfo(), textNode.getInfo()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				ret.add(hypothesisNode);
		}
		return ret;
	}
	
	public static Set<String> constructSetLemmasLowerCase(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree)
	{
		Set<String> ret = new LinkedHashSet<String>();
		for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree.getTree()))
		{
			String lemma = InfoGetFields.getLemma(node.getInfo());
			if (lemma.length()>0)
			{
				ret.add(lemma.toLowerCase());
			}
		}
		return ret;
	}
	
	public static Set<String> findCoveredLemmasLowerCase(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree, Set<String> hypothesisLemmasLowerCase)
	{
		Set<String> textTreeLemmasLowerCase = constructSetLemmasLowerCase(textTree);
		Set<String> ret = new LinkedHashSet<String>();
		for (String hypothesisLemma : hypothesisLemmasLowerCase)
		{
			if (textTreeLemmasLowerCase.contains(hypothesisLemma))
				ret.add(hypothesisLemma);
		}
		return ret;
	}
	
	public static double missingLemmasPortion(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree, Set<String> hypothesisLemmasLowerCase)
	{
		Set<String> coveredLemmas = findCoveredLemmasLowerCase(textTree,hypothesisLemmasLowerCase);
		double numberOfMissingLemmas = (double)(hypothesisLemmasLowerCase.size()-coveredLemmas.size());
		return numberOfMissingLemmas/((double)hypothesisLemmasLowerCase.size());
	}
	
	/**
	 * Returns a set of nodes of the hypothesis that have no compatible node in the text.
	 * <BR>
	 * <I>Compatible node</I> is a node that is equal according to {@link Equalities#areEqualNodes(BasicNode, BasicNode)},
	 * and also the parents are equal in the same manner, and the relations between the nodes
	 * to their parents are equal (according to {@link Equalities#areEqualRelations(BasicNode, BasicNode, java.util.Map, java.util.Map)})
	 * 
	 * @deprecated My fault. Actually {@link Equalities#areEqualRelations(BasicNode, BasicNode, java.util.Map, java.util.Map)}
	 * does it (verifies node, relation and parent).
	 * 
	 * @param text
	 * @param hypothesis
	 * @return
	 */
	@Deprecated
	public static Set<ExtendedNode> findNodesNotCompatible(TreeAndParentMap<ExtendedInfo,ExtendedNode> text, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis)
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToSet(hypothesis.getTree());
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToSet(text.getTree());
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			boolean found = false;
			for (ExtendedNode textNode : textNodes)
			{
				ExtendedNode parentHypothesis = hypothesis.getParentMap().get(hypothesisNode);
				ExtendedNode parentText = text.getParentMap().get(textNode);
				if (Equalities.areEqualNodes(hypothesisNode.getInfo(), textNode.getInfo()))
				{
					if ((parentHypothesis==null)&&(parentText==null))
						found = true;
					else if ((parentHypothesis==null)||(parentText==null))
						found = false;
					else
					{
						if (
							Equalities.areEqualRelations(hypothesisNode, textNode, hypothesis.getParentMap(), text.getParentMap())
							&&
							Equalities.areEqualNodes(parentHypothesis.getInfo(),parentText.getInfo())
							)
							found = true;
						else
							found = false;
					}
				}
				if (found)
					break;
			}
			if (!found)
				ret.add(hypothesisNode);
		}
		return ret;
	}

	public static Set<ExtendedNode> findRelationsNoMatch(TreeAndParentMap<ExtendedInfo,ExtendedNode> text, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis)
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToSet(text.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToSet(hypothesis.getTree());
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			boolean found = false;
			for (ExtendedNode textNode : textNodes)
			{
				if (Equalities.areEqualRelations(hypothesisNode, textNode, hypothesis.getParentMap(),text.getParentMap()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				ret.add(hypothesisNode);
		}
		return ret;
	}
	
	public static double missingNodesPortion(TreeAndParentMap<ExtendedInfo,ExtendedNode> text, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis)
	{
		int totalNumberOfNodes = AbstractNodeUtils.treeToSet(hypothesis.getTree()).size();
		int numberOfMissingNodes = findNodesNoMatch(text, hypothesis).size();
		return ((double)numberOfMissingNodes)/((double)totalNumberOfNodes);
	}
	

	public static double missingRelationsPortion(TreeAndParentMap<ExtendedInfo,ExtendedNode> text, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesis)
	{
		int totalNumberOfRelations = AbstractNodeUtils.treeToSet(hypothesis.getTree()).size()-1;
		if (0==totalNumberOfRelations)
			return 0;
		else
		{
			int numberOfMissingRelations = findRelationsNoMatch(text, hypothesis).size();
			return ((double)numberOfMissingRelations)/((double)totalNumberOfRelations);
		}
	}
	
	
	public static ValueSetMap<ExtendedNode, ExtendedNode> findAllMatchingNodes(ExtendedNode fromTree, ExtendedNode toTree)
	{
		ValueSetMap<ExtendedNode, ExtendedNode> ret = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		Set<ExtendedNode> fromTreeNodes = AbstractNodeUtils.treeToSet(fromTree);
		Set<ExtendedNode> toTreeNodes = AbstractNodeUtils.treeToSet(toTree);
		
		for (ExtendedNode fromNode : fromTreeNodes)
		{
			for (ExtendedNode toNode : toTreeNodes)
			{
				if (Equalities.areEqualNodes(fromNode.getInfo(), toNode.getInfo()))
				{
					ret.put(fromNode, toNode);
				}
			}
		}
		
		return ret;
	}
	
	
	/**
	 * Returns a set of hypothesis nodes, that
	 * <ol>
	 * <li>Do exist in the text tree</li>
	 * <li>The parent, or relation to parent is not compatible</li>
	 * </ol>
	 * If the hypothesis node exists many times in the text tree, then only if for all of
	 * the matching text nodes the relation is bad - the node is returned in the set. 
	 * @param textTree
	 * @param hypothesisTree
	 * @return
	 */
	public static Set<ExtendedNode> findNodeBadParents(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree)
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		ValueSetMap<ExtendedNode, ExtendedNode> matchingNodesMap = findAllMatchingNodes(hypothesisTree.getTree(), textTree.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToSet(hypothesisTree.getTree());
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			if (
				(hypothesisTree.getParentMap().get(hypothesisNode)!=null) // not root
				&&
				notEmpty(matchingNodesMap.get(hypothesisNode)) // it has matching node in the text tree
				)
			{
				boolean goodTextNodeFound = false;
				for (ExtendedNode matchingTextNode : matchingNodesMap.get(hypothesisNode))
				{
					if (Equalities.areEqualRelations(hypothesisNode, matchingTextNode, hypothesisTree.getParentMap(), textTree.getParentMap()))
					{
						goodTextNodeFound = true;
						break;
					}
				}
				if (!goodTextNodeFound)
					ret.add(hypothesisNode);
				
				
			}
		}
		return ret;
	}
	
	
	
	public static boolean isArtificialRoot(AbstractNode<? extends Info, ?> node)
	{
		return PreprocessUtilities.isArtificialRoot(node);
	}
	
	public static BasicNode addArtificialRoot(BasicNode tree)
	{
		return PreprocessUtilities.addArtificialRoot(tree);
	}
	
	public static ExtendedNode addArtificialRoot(ExtendedNode tree)
	{
		ExtendedInfo rootInfo = new ExtendedInfo(AbstractMiniparParser.ROOT_NODE_ID,new DefaultNodeInfo(null,null,0,null,new DefaultSyntacticInfo(null)),new DefaultEdgeInfo(null),ExtendedNodeConstructor.EMPTY_ADDITIONAL_NODE_INFORMATION);
		ExtendedNode root = new ExtendedNode(rootInfo);
		root.addChild(tree);
		return root;
	}
	
	
	
	
	
	
	
	public static boolean notEmpty(ImmutableSet<?> set)
	{
		boolean ret = false;
		if (set!=null) if (set.size()>0) ret = true;
		
		return ret;
	}
	
	
	
	public static ExtendedNode copyFromBasicNode(BasicNode originalTree)
	{
		TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode> treeCopier =
			new TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode>(
					originalTree,
					new TreeCopier.InfoConverter<BasicNode, ExtendedInfo>()
					{
						public ExtendedInfo convert(BasicNode node)
						{
							return new ExtendedInfo(node.getInfo(),ExtendedNodeConstructor.EMPTY_ADDITIONAL_NODE_INFORMATION);
						}
					},
					new ExtendedNodeConstructor()
			);
		treeCopier.copy();
		return treeCopier.getGeneratedTree();
	}
	

	public static boolean areEqualTrees(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree1,TreeAndParentMap<ExtendedInfo, ExtendedNode> tree2)
	{
		boolean ret = false;
		if (findRelationsNoMatch(tree1, tree2).size()==0)
		{
			if (findRelationsNoMatch(tree2, tree1).size()==0)
				ret = true;
		}
		return ret;
	}
	
	public static <T extends Info, S extends AbstractNode<T,S>> Map<String,S> mapIdToNode(S tree) throws TeEngineMlException
	{
		Map<String,S> ret = new LinkedHashMap<String, S>();
		Set<S> nodes = AbstractNodeUtils.treeToSet(tree);
		for (S node : nodes)
		{
			String id = node.getInfo().getId();
			if (null==id) throw new TeEngineMlException("Null id");
			ret.put(id,node);
		}
		return ret;
	}
	
	public static <T extends Info, S extends AbstractNode<T,S>> Map<String,S> mapVarIdToNode(S tree)
	{
		Map<String,S> ret = new LinkedHashMap<String, S>();
		Set<S> nodes = AbstractNodeUtils.treeToSet(tree);
		for (S node : nodes)
		{
			if (InfoGetFields.isVariable(node.getInfo()))
			{
				Integer varIdInteger = node.getInfo().getNodeInfo().getVariableId();
				String varId = varIdInteger.toString();
				ret.put(varId,node);
			}
		}
		return ret;
		
		
		
	}
	
//	@Deprecated
//	public static double getHeuristicGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree, OperationsEnvironment operationsEnvironment)
//	{
//		SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(textTree,operationsEnvironment.getHypothesis(), operationsEnvironment.getHypothesisLemmasLowerCase(),operationsEnvironment.getHypothesisNumberOfNodes());
//		return (double)(evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations());
//	}

	
	
	
}
