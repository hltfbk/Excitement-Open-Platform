package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.utilities.linguistics.LinguisticsException;
import eu.excitementproject.eop.common.utilities.linguistics.english.tense.EnglishVerbFormsEntity;
import eu.excitementproject.eop.common.utilities.linguistics.english.tense.EnglishVerbTenseRetriever;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.pasta.Argument;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.ArgumentIdentificationUtilities;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.ArgumentNodeAndPathFromPredicate;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.PredicateArgumentAprioriInformation;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * Given a parse-tree and a <B>verbal</B> {@link Predicate} in that tree, find all of the predicate's arguments.
 * 
 * @author Asher Stern
 * @since October 7, 2012
 *
 */
@LanguageDependent("english")
@StandardSpecific("stanford-dependencies")
@ParserSpecific("easyfirst")
public class ArgumentsIdentifier<I extends Info, S extends AbstractNode<I, S>>
{
	/**
	 * A constructor with the parse-tree and the predicate.
	 * @param tree
	 * @param predicate
	 */
	public ArgumentsIdentifier(TreeAndParentMap<I, S> tree, Predicate<I, S> predicate, boolean itIsVerb)
	{
		super();
		this.tree = tree;
		this.predicate = predicate;
		this.itIsVerb = itIsVerb;
	}
	
	
	public void setAprioriInformation(PredicateArgumentAprioriInformation<I, S> aprioriInformation)
	{
		this.aprioriInformation = aprioriInformation;
	}





	/**
	 * Finds all of the arguments.
	 * @throws PredicateArgumentIdentificationException
	 */
	public void identifyArguments() throws PredicateArgumentIdentificationException
	{
		// First, find all the the arguments that are children or at least descendants of the predicate-head.
		Set<ArgumentNodeAndPathFromPredicate<I,S>> argumentNodes = getArgumentNodes();
		arguments = new LinkedHashSet<TypedArgument<I, S>>();
		for (ArgumentNodeAndPathFromPredicate<I,S> argumentNode : argumentNodes)
		{
			TypedArgument<I, S> typedArgument = fromPathAndSyntacticHead(argumentNode.getPathFromPredicateToArgument(), argumentNode.getSyntacticArgumentNode());
			arguments.add(typedArgument);
		}
		
		// Second, find all arguments that are not descendants of the predicate head, but are connected somehow in a way
		// that implies that they are arguments.
		identifyNonDirectDescendantArguments();
	}
	
	
	/**
	 * Returns all the arguments that have been found by {@link #identifyArguments()}.
	 * @return
	 */
	public Set<TypedArgument<I, S>> getArguments()
	{
		return arguments;
	}

	/////////////////////////// PRIVATE ///////////////////////////


	private Set<ArgumentNodeAndPathFromPredicate<I,S>> getArgumentNodes()
	{
		Set<ArgumentNodeAndPathFromPredicate<I,S>> ret = new LinkedHashSet<ArgumentNodeAndPathFromPredicate<I,S>>();
		Set<ArgumentNodeAndPathFromPredicate<I,S>> argumentCandidates = ArgumentIdentificationUtilities.getArgumentNodes(predicate);
		for (ArgumentNodeAndPathFromPredicate<I,S> argument : argumentCandidates)
		{
			if (checkIfArgument(predicate, argument.getPathFromPredicateToArgument(), argument.getSyntacticArgumentNode()))
			{
				ret.add(argument);
			}
		}
		
		return ret;
	}
	
	private boolean checkIfArgument(Predicate<I, S> predicate, List<S> pathFromPredicateToNode, S node)
	{
		String relation = InfoGetFields.getRelation(node.getInfo());
		if ( 
				(RelationTypes.getSemanticModifierRelations().contains(relation))
				||
				(RelationTypes.getSemanticSubjectRelations().contains(relation))
				||
				(RelationTypes.getSemanticObjectRelations().contains(relation))
			)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	

	
	
	
	/**
	 * 
	 * @param pathFromPredicateToArgument Either null, or the preposition node between the verb and the argument.
	 * @param syntacticArgumentNode The argument node, syntactically connected to he predicate, either by
	 * a preposition, or directly.
	 * @return
	 * @throws PredicateArgumentIdentificationException 
	 */
	private TypedArgument<I, S> fromPathAndSyntacticHead(List<S> pathFromPredicateToArgument, S syntacticArgumentNode) throws PredicateArgumentIdentificationException
	{
		String relation = InfoGetFields.getRelation(syntacticArgumentNode.getInfo());
		ArgumentType argumentType = RelationTypes.fromRelation(relation);
		if (checkSemanticPassiveSubject(predicate,pathFromPredicateToArgument,syntacticArgumentNode))
		{
			argumentType = ArgumentType.SUBJECT;
		}
		S semanticHead = ArgumentIdentificationUtilities.getDeepAntecedent(syntacticArgumentNode);
		Set<S> nodes = getInternalNodes(semanticHead);
		
		S surfaceSyntacticHead = syntacticArgumentNode;
		if (pathFromPredicateToArgument!=null) // should not be empty
		{
			surfaceSyntacticHead = pathFromPredicateToArgument.get(0);
		}
		
		TypedArgument<I, S> ret = null;
		Argument<I, S> argument = null;
		if (syntacticArgumentNode!=semanticHead)
		{
			argument = new Argument<I, S>(nodes,semanticHead,surfaceSyntacticHead,syntacticArgumentNode);
		}
		else
		{
			argument = new Argument<I, S>(nodes,semanticHead,surfaceSyntacticHead);
		}
		
		if (pathFromPredicateToArgument!=null)
		{
			ret = new TypedArgument<I, S>(argument,argumentType,pathFromPredicateToArgument);
		}
		else
		{
			ret = new TypedArgument<I, S>(argument,argumentType);
		}
		
		return ret;
	}
	
	/**
	 * Returns true if the argument is syntactic object, but semantic subject, due
	 * to passivization. Like "The computer was programmed by the programmer", the
	 * "programmer" is semantically a subject.
	 * <P>
	 * This function searches for a path of VERB-->"by"-->(syntactic)Object
	 * @param predicate
	 * @param pathFromPredicateToArgument
	 * @param syntacticArgumentNode
	 * @param argumentType
	 * @return
	 * @throws PredicateArgumentIdentificationException
	 */
	private boolean checkSemanticPassiveSubject(Predicate<I, S> predicate, List<S> pathFromPredicateToArgument, S syntacticArgumentNode) throws PredicateArgumentIdentificationException
	{
		if (!itIsVerb) {return false;}
		
		if (aprioriInformation!=null)
		{
			Boolean fromApriori = aprioriInformation.checkSemanticPassiveSubject(predicate, pathFromPredicateToArgument, syntacticArgumentNode);
			if (fromApriori!=null)
			{
				return fromApriori;
			}
		}
		
		boolean itIsSemanticSubject = false;
		if (pathFromPredicateToArgument!=null)
		{
			if (pathFromPredicateToArgument.size()==1)
			{
				ArgumentType argumentType = RelationTypes.fromRelation(InfoGetFields.getRelation(syntacticArgumentNode.getInfo()));
				if (
					(InfoGetFields.getLemma(pathFromPredicateToArgument.get(0).getInfo()).equals("by"))
					&&
					(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(predicate.getHead().getInfo())).equals(SimplerCanonicalPosTag.VERB))
					&&
					(ArgumentType.OBJECT.equals(argumentType))
					)
				{
					try
					{
						EnglishVerbFormsEntity entity = EnglishVerbTenseRetriever.getTenseForVerb(InfoGetFields.getLemma(predicate.getHead().getInfo()));
						if ( entity.getPastParticipleAlternatives().contains(InfoGetFields.getWord(predicate.getHead().getInfo())) )
						{
							itIsSemanticSubject = true;
						}
						
					}
					catch(LinguisticsException e)
					{
						throw new PredicateArgumentIdentificationException("Failed to analyze argument. See nested.",e);
					}
				}
			}
		}
		return itIsSemanticSubject;
	}
	
	private Set<S> getInternalNodes(S head)
	{
		return ArgumentIdentificationUtilities.getInternalNodes(predicate, head);
	}
	
	private void identifyNonDirectDescendantArguments()
	{
		identifyRcmodAndPartmodArguments();
	}
	
	@StandardSpecific("stanford-dependencies")
	@ParserSpecific("easyfirst")
	private void identifyRcmodAndPartmodArguments()
	{
		// If this predicate-head is connected to its parent with relation "rcmod" or "partmod", then the parent is actually
		// an argument of this predicate.
		String predicateHeadRelation = InfoGetFields.getRelation(predicate.getHead().getInfo());
		if ( ("rcmod".equals(predicateHeadRelation)) || ("partmod".equals(predicateHeadRelation)) )
		{
			// The parent is an argument. Let's build it.
			S predicateParent = tree.getParentMap().get(predicate.getHead());
			Set<S> predicateParentNodes = getInternalNodes(predicateParent);
			Argument<I, S> argument = new Argument<I, S>(predicateParentNodes,predicateParent,predicateParent);
			// By default - it is an object.
			ArgumentType argumentType = ArgumentType.OBJECT;
			
			// If the parent (the argument that we've built right now) has an "node-with-antecedent" child (it is a sibling of the predicate-head),
			// and that child is connected via "ref" relation - than this teaches us that the real semantic relation is the relation
			// between the antecedent and the predicate-head. So we find it, and use it instead of the default (which is "object").
			ArgumentType argumentTypeByRef = ArgumentIdentificationUtilities.argumentTypeByRef(predicate, predicateParent);
			if (argumentTypeByRef!=null)
			{
				argumentType = argumentTypeByRef;
			}
			
			// Now we have the argument and the argument type (there is no path between the predicate and the argument, so it
			// is left "null"). Let's build a TypedArgument.
			TypedArgument<I, S> typedArgument = new TypedArgument<I, S>(argument,argumentType, null, false);
			
			// If this is the case as noted above, that the parent has a "node-with-antecedent" child, and this child is connected to
			// the parent via "ref" relation, then the antecedent of that child should not be counted as an argument.
			// Let's remove it. (it was added as an argument, since it is a surface-syntactic argument).
			S refWithAntecedent = ArgumentIdentificationUtilities.getRefWithAntecedent(predicateParent);
			if (refWithAntecedent!=null)
			{
				S deepAntecedent = ArgumentIdentificationUtilities.getDeepAntecedent(refWithAntecedent);
				removeArgumentBySyntacticHead(deepAntecedent);
			}
			
			arguments.add(typedArgument);
		}
	}
	
	private void removeArgumentBySyntacticHead(S syntacticHead)
	{
		TypedArgument<I, S> toRemove = null;
		for (TypedArgument<I, S> typedArgument : arguments)
		{
			if (typedArgument.getArgument().getSyntacticHead()==syntacticHead)
			{
				toRemove = typedArgument;
				break;
			}
		}
		if (toRemove!=null)
		{
			arguments.remove(toRemove);
		}
	}
	
	
	
	// input
	private final TreeAndParentMap<I,S> tree;
	private final Predicate<I, S> predicate;
	private final boolean itIsVerb;
	
	private PredicateArgumentAprioriInformation<I, S> aprioriInformation = null;
	
	// output
	private Set<TypedArgument<I, S>> arguments;
}
