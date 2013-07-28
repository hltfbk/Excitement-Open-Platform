package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;
import eu.excitementproject.eop.common.representation.pasta.ClausalArgument;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.ArgumentIdentificationUtilities;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.ArgumentNodeAndPathFromPredicate;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * 
 * @author Asher Stern
 * @since Oct 9, 2012
 *
 */
@ParserSpecific("easyfirst")
public class ClausalArgumentsIdentifier<I extends Info, S extends AbstractNode<I, S>>
{
	public ClausalArgumentsIdentifier(TreeAndParentMap<I, S> tree,
			Predicate<I, S> predicate)
	{
		super();
		this.tree = tree;
		this.predicate = predicate;
	}
	
	public void identifyClausalArguments()
	{
		clausalArguments = new LinkedHashSet<ClausalArgument<I,S>>();
		Set<ArgumentNodeAndPathFromPredicate<I,S>> candidates = ArgumentIdentificationUtilities.getArgumentNodes(predicate);
		for (ArgumentNodeAndPathFromPredicate<I,S> candidate : candidates)
		{
			ArgumentType argumentType = checkIfClausalArgument(candidate);
			if (argumentType!=null)
			{
				clausalArguments.add(fromArgumentNode(candidate,argumentType));
			}
		}
	}
	
	
	
	public Set<ClausalArgument<I, S>> getClausalArguments() throws PredicateArgumentIdentificationException
	{
		if (null==clausalArguments) throw new PredicateArgumentIdentificationException("Please call identifyClausalArguments() before calling this method.");
		return clausalArguments;
	}

	/**
	 * Returns null if the given node is not a clausal-argument.
	 * If it is - returns the type of the clausal-argument (subject, object, modifier or unknown).
	 * 
	 * @param argumentNode
	 * @return
	 */
	@ParserSpecific("easyfirst")
	@StandardSpecific("stanford-dependencies")
	private ArgumentType checkIfClausalArgument(ArgumentNodeAndPathFromPredicate<I,S> argumentNode)
	{
		String relation = null;
		String relationOfPath = null;
		ArgumentType argumentType = null;
		boolean itIsClausalArgument = false;
		if (argumentNode.getPathFromPredicateToArgument()!=null)
		{
			if (argumentNode.getPathFromPredicateToArgument().size()>0)
			{
				if (ArgumentIdentificationUtilities.prepOughtToBePrepc(predicate.getHead(), argumentNode.getPathFromPredicateToArgument().get(0)))
				{
					itIsClausalArgument = true;
					relationOfPath = "prepc";
				}
				else
				{
					String relationToPath = InfoGetFields.getRelation(argumentNode.getPathFromPredicateToArgument().get(0).getInfo());
					itIsClausalArgument = itIsClausalArgument || ( RelationTypes.getSemanticNewClauseRelations().contains(relationToPath) );
				}
			}
		}
		relation = InfoGetFields.getRelation(argumentNode.getSyntacticArgumentNode().getInfo());
		if (RelationTypes.getSemanticNewClauseRelations().contains(relation))
		{
			itIsClausalArgument = true;
		}
		if (!itIsClausalArgument)
		{
			argumentType = null;
		}
		else
		{
			if (
				(RelationTypes.getSemanticNewSubjectClauseRelations().contains(relation))
				||
				(RelationTypes.getSemanticNewSubjectClauseRelations().contains(relationOfPath))
				)
			{
				argumentType = ArgumentType.SUBJECT;
			}
			else if (
					(RelationTypes.getSemanticNewObjectClauseRelations().contains(relation))
					||
					(RelationTypes.getSemanticNewObjectClauseRelations().contains(relationOfPath))
					)
			{
				argumentType = ArgumentType.OBJECT;
			}
			else if (
					(RelationTypes.getSemanticNewModifierClauseRelations().contains(relation))
					||
					(RelationTypes.getSemanticNewModifierClauseRelations().contains(relationOfPath))
					)
			{
				argumentType = ArgumentType.MODIFIER;
			}
			else
			{
				argumentType = ArgumentType.UNKNOWN;
			}
		}
		
		return argumentType;
		
	}
	
	private ClausalArgument<I, S> fromArgumentNode(ArgumentNodeAndPathFromPredicate<I,S> argumentNode, ArgumentType argumentType)
	{
		S clause = ArgumentIdentificationUtilities.getDeepAntecedent(argumentNode.getSyntacticArgumentNode());
		ClausalArgument<I, S> ret;
		if (clause != argumentNode.getSyntacticArgumentNode())
		{
			ret = new ClausalArgument<I, S>(argumentNode.getPathFromPredicateToArgument(), clause, argumentNode.getSyntacticArgumentNode(), argumentType);
		}
		else
		{
			ret = new ClausalArgument<I, S>(argumentNode.getPathFromPredicateToArgument(), clause, argumentType);
		}
		
		return ret;		
	}
	

	@SuppressWarnings("unused")
	private final TreeAndParentMap<I, S> tree;
	private final Predicate<I, S> predicate;
	
	private Set<ClausalArgument<I, S>> clausalArguments = null;
}
