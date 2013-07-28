package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst;


import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;
import eu.excitementproject.eop.common.representation.pasta.Predicate;

/**
 * 
 * @author Asher Stern
 * @since 8 October 2012
 *
 */
@ParserSpecific("easyfirst")
public class ArgumentIdentificationUtilities
{
	/**
	 * Returns all the children of the head of the predicate.
	 * A preposition child is not returned, but its first non-preposition descendant 
	 * @param predicate
	 * @return
	 */
	public static <I extends Info, S extends AbstractNode<I, S>> Set<ArgumentNodeAndPathFromPredicate<I,S>> getArgumentNodes(Predicate<I,S> predicate)
	{
		Set<ArgumentNodeAndPathFromPredicate<I,S>> ret = new LinkedHashSet<ArgumentNodeAndPathFromPredicate<I,S>>();
		if (predicate.getHead().hasChildren())
		{
			for (S child : predicate.getHead().getChildren())
			{
				if (!predicate.getNodes().contains(child))
				{
					S argumentNode = child;
					List<S> pathToArgumentNode = null;
					while ( (argumentNode!=null) && (SimplerCanonicalPosTag.PREPOSITION.equals(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(argumentNode.getInfo())))) )
					{
						if (null==pathToArgumentNode) pathToArgumentNode = new LinkedList<S>();
						pathToArgumentNode.add(argumentNode);
						argumentNode = getChildOfPreposition(argumentNode);
					}
					if (argumentNode!=null)
					{
						ArgumentNodeAndPathFromPredicate<I,S> argumentNodeAndPathFromPredicate = new ArgumentNodeAndPathFromPredicate<I,S>(pathToArgumentNode,argumentNode);
						ret.add(argumentNodeAndPathFromPredicate);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Given a parent and a child in a parse tree, which the edge between them is
	 * labeled "prep", this method detects whether the relation ought to be "prepc".
	 * <P> 
	 * {@link EasyFirstParser} has an error which causes every edge that ought to
	 * be labeled with "prepc" to be labeled by "prep". This method recognizes,
	 * for a given parent and child which the edge between them is labeled "prep",
	 * whether that edge ought to be labeled "prepc".
	 * @param parent
	 * @param child
	 * @return
	 */
	@ParserSpecific("easyfirst")
	public static <I extends Info, S extends AbstractNode<I, S>> boolean prepOughtToBePrepc(S parent, S child)
	{
		boolean oughtToBePrepc = false;
		if (InfoGetFields.getRelation(child.getInfo()).equals("prep"))
		{
			if (child.hasChildren())
			{
				for (S grandChild : child.getChildren())
				{
					if (RelationTypes.getSemanticNewClauseRelations().contains(InfoGetFields.getRelation(grandChild.getInfo())))
					{
						oughtToBePrepc = true;
					}
				}
			}
		}
		return oughtToBePrepc;
	}
	
	/**
	 * Returns the "real" node in the parse-tree that is the antecedent of
	 * the given node.
	 * <P>
	 * If the given node has no antecedent - then the method returns the node itself.
	 * Otherwise, it finds the antecedent of the antecedent of the antecedent of the ... that
	 * is the "real" node (i.e., has no antecedent).
	 * 
	 * @param node
	 * @return
	 */
	public static <I extends Info, S extends AbstractNode<I, S>> S getDeepAntecedent(S node)
	{
		S ret = node;
		while (ret.getAntecedent()!=null)
		{
			ret = ret.getAntecedent();
		}
		return ret;
	}
	
	/**
	 * Finds the semantic relation based on a node
	 * connected with "ref" to the argument, as follows:
	 * <BR>
	 * In {@link EasyFirstParser}, if you parse a sentence like
	 * "The boy whom I saw is nice.", you will see that "boy" has a child
	 * connected with "ref", and that child is a duplicate of "whom" (its
	 * antecedent is the node of "whom"), and the real "whom" node is connected
	 * to "see" by "dobj".
	 * <pre>
	 * 
	 *              "boy"
	 *                |
	 *           ------------
	 *           |(rcmod)   |(ref)
	 *          "see"     "whom"(^)
	 *            |(dobj)
	 *          "whom"
	 * </pre>
	 * This teaches us that the real relation of the argument - "boy" - to
	 * the predicate - "see" - is "dobj".
	 * <P>
	 * This relation is translated to {@link ArgumentType}, which is returned by this method.
	 * The method gets the predicate, and the "artificial" node (the node that
	 * has an antecedent), which is connected to the predicate-head with "ref".
	 *            
	 * 
	 * @param predicate The predicate
	 * @param argumentSyntacticHead The node that is connected to the predicate with relation "ref"
	 * @return The argument-type based on the semantic relation between the antecedent
	 * of the given node to the predicate.
	 */
	@StandardSpecific("stanford-dependencies")
	@ParserSpecific("easyfirst")
	public static <I extends Info, S extends AbstractNode<I, S>> ArgumentType argumentTypeByRef(Predicate<I,S> predicate, S argumentSyntacticHead)
	{
		ArgumentType ret = null;
		S refWithAntecedent = getRefWithAntecedent(argumentSyntacticHead);
		if (refWithAntecedent!=null)
		{
			S deepAntecedent = getDeepAntecedent(refWithAntecedent);
			ret = RelationTypes.fromRelation(InfoGetFields.getRelation(deepAntecedent.getInfo()));
		}
		return ret;
	}

	/**
	 * Given a node in the parse-tree, this method finds a direct-child of that node
	 * that is connected to it via "ref" relation, and also has an antecedent.
	 * 
	 * @param node
	 * @return
	 */
	@StandardSpecific("stanford-dependencies")
	@ParserSpecific("easyfirst")
	public static <I extends Info, S extends AbstractNode<I, S>> S getRefWithAntecedent(S node)
	{
		S refWithAntecedent = null;
		if (node.hasChildren())
		{
			for (S child : node.getChildren())
			{
				if ("ref".equals(InfoGetFields.getRelation(child.getInfo())))
				{
					if (child.getAntecedent()!=null)
					{
						refWithAntecedent = child;
						break;
					}
				}
			}
		}
		return refWithAntecedent;
	}
	
	/**
	 * Given a node in the parse-tree that corresponds to a preposition,
	 * this method returns its child, which is the actual argument node.
	 * For example, consider the sentence "I put the book on the table.".
	 * The relevant part of the parse-tree looks like:
	 * <pre>
	 *             put
	 *              |
	 *             on
	 *            	|
	 *            table
	 * </pre>
	 * So this method gets the node of "on", and returns "table".
	 *  
	 * @param node
	 * @return
	 */
	public static <I extends Info, S extends AbstractNode<I, S>> S getChildOfPreposition(S node)
	{
		S child = null;
		if (node.getChildren()!=null)
		{
			if (node.getChildren().size()==1)
			{
				child = node.getChildren().iterator().next();
			}
			else if (node.getChildren().size()>0)
			{
				List<S> nounChildren = new LinkedList<S>();
				for (S childNode : node.getChildren())
				{
					if (SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(childNode.getInfo())).equals(SimplerCanonicalPosTag.NOUN))
					{
						nounChildren.add(childNode);
					}
				}
				if (nounChildren.size()>0)
				{
					// Heuristic: take the first
					child = nounChildren.iterator().next();
				}
			}
		}
		return child;
	}
	
	
	/**
	 * Given an argument head, this method returns all the parse-tree-nodes
	 * that are part of the argument (these nodes contain the head,
	 * and optionally other nodes).
	 * <P>
	 * The method adds all descendants of the argument head that satisfy the following conditions:
	 * <UL>
	 * <LI>They are not part of the predicate</LI>
	 * <LI>They are not in another clause in the tree</LI>
	 * </UL>
	 * 
	 * @param predicate a predicate, such that the given argument-head is an argument of this predicate.
	 * @param head an argument-head
	 * 
	 * @return all argument nodes.
	 */
	public static <I extends Info, S extends AbstractNode<I, S>> Set<S> getInternalNodes(Predicate<I, S> predicate, S head)
	{
		Set<S> ret = new LinkedHashSet<S>();
		ret.add(head);
		if (head.hasChildren())
		{
			for (S child : head.getChildren())
			{
				if (!predicate.getNodes().contains(child))
				{
					if (
						(!RelationTypes.getSemanticNewClauseRelations().contains(InfoGetFields.getRelation(child.getInfo())))
						&&
						(!prepOughtToBePrepc(head, child))
						&&
						(null==child.getAntecedent())
						)
					{
						ret.addAll(getInternalNodes(predicate, child));
					}
				}
			}
		}
		return ret;
	}


}
