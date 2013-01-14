package eu.excitementproject.eop.biutee.rteflow.macro.multiword_namedentity_utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.transformations.operations.rules.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils.LexicalRuleInTreeFinder;
import eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils.ListNodesToTree;
import eu.excitementproject.eop.transformations.utilities.RuleToString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * 
 * @author Asher Stern
 * @since Aug 21, 2011
 *
 */
public class MultiWordNamedEntityUtils
{
	public static final String RULE_BASE_NAME = "Multi-Word-Named-Entity";
	
	public Set<RuleWithConfidenceAndDescription<Info, BasicNode>>
	setRulesOfNamedEntities(BasicNode hypothesisTree) throws TeEngineMlException
	{
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> ret = new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();
		ValueSetMap<BasicNode, List<BasicNode>> vsmNeInTree = mapNamedEntitiesFromTree(hypothesisTree);
		for (BasicNode lhsNode : vsmNeInTree.keySet())
		{
			SimplerCanonicalPosTag pos = SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(lhsNode.getInfo()));
			if (pos!=null){ if (pos.equals(SimplerCanonicalPosTag.NOUN))
			{
				for (List<BasicNode> neList : vsmNeInTree.get(lhsNode))
				{
					if (neList.size()>1)
					{
						BasicNode headOfNe = ListNodesToTree.findHead(hypothesisTree, neList);
						BasicNode rhsTree = ListNodesToTree.createTree(headOfNe, neList);
						if (AbstractNodeUtils.treeToSet(rhsTree).size()==neList.size())
						{
							BidirectionalMap<BasicNode, BasicNode> mapNodes = new SimpleBidirectionalMap<BasicNode, BasicNode>();
							BasicNode mappedRhsNode = null;
							for (BasicNode rhsNode : AbstractNodeUtils.treeToSet(rhsTree))
							{
								// TODO I don't like this way. I would prefer avoiding
								// the usage of the IDs. However, I guess that as long
								// as it is done only on the original tree of hypothesis
								// it should work.
								if (rhsNode.getInfo().getId().equals(lhsNode.getInfo().getId()))
									mappedRhsNode = rhsNode;
							}
							if (null==mappedRhsNode)
							{
								try
								{
									StringBuffer sb = new StringBuffer();
									sb.append("null==mappedRhsNode!\n");
									sb.append("Hypothesis tree:\n");
									sb.append(TreeUtilities.treeToString(hypothesisTree));
									sb.append("\nlhsNode.getInfo().getId() = ").append(lhsNode.getInfo().getId());
									sb.append("\nneList = \n");
									for (BasicNode nn : neList)
									{
										sb.append(nn.getInfo().getId());
										sb.append(" - ");
										sb.append(InfoGetFields.getLemma(nn.getInfo()));
										sb.append(" , ");
									}
									sb.append("\nrhsTree = \n");
									sb.append(TreeUtilities.treeToString(rhsTree));
									throw new TeEngineMlException(sb.toString());
								}
								catch(TreeStringGeneratorException e)
								{
									throw new TeEngineMlException("null==mappedRhsNode");
								}
								catch(NullPointerException e)
								{
									throw new TeEngineMlException("null==mappedRhsNode");
								}


							}
							BasicNode ruleLhs = new BasicNode(lhsNode.getInfo());
							mapNodes.put(ruleLhs,mappedRhsNode);
							SyntacticRule<Info, BasicNode> rule = new SyntacticRule<Info, BasicNode>(ruleLhs, rhsTree, mapNodes);
							String description = buildDescription(lhsNode,neList);
							RuleWithConfidenceAndDescription<Info, BasicNode> rwcad =
								new RuleWithConfidenceAndDescription<Info, BasicNode>(rule,E_MINUS_1,description);

							if (logger.isDebugEnabled())
							{
								logger.debug("Adding multi-word-named-entity \"rule\": "+description);
								try{logger.debug(RuleToString.ruleToString(rwcad));}catch(TreeStringGeneratorException e){logger.error("Could not print the rule to log file.",e);}
							}

							ret.add(rwcad);
						}
						else
						{
							try
							{
								StringBuffer sb = new StringBuffer();
								sb.append("Skipping multi-word-named-entity rule due to: AbstractNodeUtils.treeToSet(rhsTree).size()!=neList.size()\n");
								sb.append("The neList is:\n");
								for (BasicNode nn : neList)
								{
									sb.append(nn.getInfo().getId()).append(" - ").append(InfoGetFields.getLemma(nn.getInfo()));
									sb.append(", ");
								}
								sb.append("\n");
								logger.warn(sb.toString());
							}
							catch(NullPointerException e)
							{
								logger.warn("Skipping multi-word-named-entity rule due to: AbstractNodeUtils.treeToSet(rhsTree).size()!=neList.size(). Cannot print the nodes.");
							}
						}
					}
				}
			}}
		}
		return ret;
	}
	
	
	/////////////////////////// PRIVATE //////////////////////////
	
	
	private static <T extends Info, S extends AbstractNode<T,S>>
	boolean isNamedEntity(S node)
	{
		boolean ret = false;
		try
		{
			if (node.getInfo().getNodeInfo().getNamedEntityAnnotation() != null)
				ret = true;
		}
		catch(NullPointerException e)
		{}
		return ret;
	}
	
	private static <T extends Info, S extends AbstractNode<T,S>>
	List<S> filterHasAntecedent(List<S> list)
	{
		List<S> ret = new ArrayList<S>(list.size());
		for (S node : list)
		{
			if (null==node.getAntecedent())
			{
				ret.add(node);
			}
		}
		return ret;
	}
	
	private static <T extends Info, S extends AbstractNode<T,S>>
	List<List<S>> listsNamedEntitiesFromTree(S tree)
	{
		List<List<S>> ret = new LinkedList<List<S>>();
		List<S> nodesList = LexicalRuleInTreeFinder.treeToList(tree);
		nodesList = filterHasAntecedent(nodesList);
		Iterator<S> iterator = nodesList.iterator();
		while (iterator.hasNext())
		{
			S node = iterator.next();
			if (isNamedEntity(node))
			{
				List<S> neList = new LinkedList<S>();
				neList.add(node);
				boolean lastWasNe = true;
				while ( (iterator.hasNext()) && (lastWasNe) )
				{
					node = iterator.next();
					if (isNamedEntity(node))
					{
						neList.add(node);
						lastWasNe=true;
					}
					else
						lastWasNe=false;
				}
				ret.add(neList);
			}
		}
		return ret;
	}
	

	private static <T extends Info, S extends AbstractNode<T,S>>
	ValueSetMap<S, List<S>> mapNamedEntitiesFromTree(S tree)
	{
		ValueSetMap<S, List<S>> ret = new SimpleValueSetMap<S, List<S>>();
		List<List<S>> listOfLists = listsNamedEntitiesFromTree(tree);
		
		for (List<S> neList : listOfLists)
		{
			for (S node : neList)
			{
				ret.put(node,neList);
			}
		}
		return ret;
	}
	

	
	
	private static <T extends Info, S extends AbstractNode<T,S>>
	String buildDescription(S lhsNode, List<S> neList)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(InfoGetFields.getLemma(lhsNode.getInfo()));
		sb.append(" => ");
		for (S ne : neList)
		{
			sb.append(InfoGetFields.getLemma(ne.getInfo()));
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	private static final double E_MINUS_1 = Math.exp(-1.0);
	
	private static final Logger logger = Logger.getLogger(MultiWordNamedEntityUtils.class);
}
