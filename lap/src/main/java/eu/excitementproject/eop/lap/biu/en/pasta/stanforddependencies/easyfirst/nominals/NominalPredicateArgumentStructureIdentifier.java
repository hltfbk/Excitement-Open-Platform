package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.nominals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.pasta.Argument;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;
import eu.excitementproject.eop.common.representation.pasta.ClausalArgument;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.Nominalization;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexArgument;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexMapBuilder;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.ArgumentIdentificationUtilities;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * Given a nominal predicate, this class finds its arguments.
 * 
 * @author Asher Stern
 * @since Oct 15, 2012
 *
 */
@ParserSpecific("easyfirst")
@StandardSpecific({"stanford-dependencies","nomlex"})
public class NominalPredicateArgumentStructureIdentifier<I extends Info, S extends AbstractNode<I, S>>
{
	@StandardSpecific("nomlex")
	public static final String PREPOSITION_PLACE = "PP";

	/**
	 * The constructor takes the parse-tree and "nomlex map", which is created by {@link NomlexMapBuilder}, and the last
	 * argument - the predicate-head, which is known to be a nominal.
	 * 
	 * @param tree the parse tree
	 * @param nomlexMap nomlex-map, created by {@link NomlexMapBuilder}
	 * @param predicateHead the predicate-head (a parse-tree-node that is known to be a nominal predicate)
	 */
	public NominalPredicateArgumentStructureIdentifier(
			TreeAndParentMap<I, S> tree,
			ImmutableMap<String, Nominalization> nomlexMap, S predicateHead)
	{
		super();
		this.tree = tree;
		this.nomlexMap = nomlexMap;
		this.predicateHead = predicateHead;
	}

	public void identify() throws PredicateArgumentIdentificationException
	{
		String lemma = InfoGetFields.getLemma(predicateHead.getInfo());
		if (nomlexMap.containsKey(lemma))
		{
			this.nominalization = nomlexMap.get(lemma);
			if (null==nominalization.getVerbs()) throw new PredicateArgumentIdentificationException("A nominal has no verbs: \""+lemma+"\".");
		}
		else
		{
			throw new PredicateArgumentIdentificationException("Given predicate head is not a known nominal: \""+lemma+"\".");
		}
		
		arguments = new LinkedHashSet<TypedArgument<I,S>>();
		clausalArguments = new LinkedHashSet<ClausalArgument<I,S>>();
		
		
		Map<S, ArgumentType> directChildrenArgumentsMap = new LinkedHashMap<S, ArgumentType>();
		if (predicateHead.hasChildren())
		{
			for (S child : predicateHead.getChildren())
			{
				ArgumentType argumentTypeDirectChildren = isDirectChildArgument(child);
				if (argumentTypeDirectChildren!=null)
				{
					directChildrenArgumentsMap.put(child,argumentTypeDirectChildren);
				}
			}
		}
		Set<NodeAndPathFromPredicateAndArgumentType<I,S>> argumentsByPreposition = getArgumentsByPrepositions();
		
		Set<S> excludedDirectChildrenFromPredicateNodes = new LinkedHashSet<S>();
			// set of nodes that are known to be not part of the predicate.
		excludedDirectChildrenFromPredicateNodes.addAll(directChildrenArgumentsMap.keySet());
		for (NodeAndPathFromPredicateAndArgumentType<I,S> argumentByPreposition : argumentsByPreposition) // for each argument that is connected to the predicate via a preposition
		{
			if (argumentByPreposition.getPathFromPredicateHead().size()<1) throw new PredicateArgumentIdentificationException("Internal bug.");
			S directChild = argumentByPreposition.getPathFromPredicateHead().get(0);
			excludedDirectChildrenFromPredicateNodes.add(directChild);
		}
		
		// Well, now we know the predicate-head, and the arguments. But we don't know which nodes the predicate
		// itself is composed of! We only know the predicate head, but not the other nodes that are part of the predicate.
		// Let's find them. We will use the list of "internal facet" relations, which indicate connections between parse-tree-nodes for
		// nodes that are the same "thing", of part of the same "facet". See RelationTypes.getSemanticInternalFacetRelations()
		// All of this is done by the following method, getPredicateInternalNodes()
		
		predicateNodes = getPredicateInternalNodes(predicateHead,excludedDirectChildrenFromPredicateNodes);
		
		// Now we have the predicate head and its nodes - we can create a Predicate object.
		Predicate<I, S> predicate = new Predicate<I,S>(predicateNodes,predicateHead,nominalization.getVerbs());
		
		// Now, based on the information already collected, we build the arguments.
		buildArguments(directChildrenArgumentsMap,argumentsByPreposition, predicate);
		
		// We have the predicate and the arguments, so we create a PredicateArgumentStructure.
		predicateArgumentStructure = new PredicateArgumentStructure<I, S>(tree,predicate,arguments,clausalArguments);
	}

	
	public PredicateArgumentStructure<I, S> getPredicateArgumentStructure()
	{
		return predicateArgumentStructure;
	}

	////////////////////////// PRIVATE //////////////////////////
	
	private void buildArguments(Map<S, ArgumentType> directChildrenArguments, Set<NodeAndPathFromPredicateAndArgumentType<I,S>> argumentsByPreposition, Predicate<I, S> predicate) throws PredicateArgumentIdentificationException
	{
		for (S directChildArgument : directChildrenArguments.keySet())
		{
			S deepAntecedent = ArgumentIdentificationUtilities.getDeepAntecedent(directChildArgument);
			Set<S> argumentNodes = ArgumentIdentificationUtilities.getInternalNodes(predicate,deepAntecedent);
			S syntacticRepresentation = null;
			if (deepAntecedent!=directChildArgument)
			{
				syntacticRepresentation = directChildArgument;
			}
			Argument<I, S> argument = new Argument<I, S>(argumentNodes,deepAntecedent,deepAntecedent,syntacticRepresentation);
			if (null==directChildrenArguments.get(directChildArgument)) throw new PredicateArgumentIdentificationException("Bug. null argument-type");
			TypedArgument<I, S> typedArgument = new TypedArgument<I, S>(argument,directChildrenArguments.get(directChildArgument));
			arguments.add(typedArgument);
		}
		
		for (NodeAndPathFromPredicateAndArgumentType<I,S> nodePathType : argumentsByPreposition)
		{
			if (nodePathType.getPathFromPredicateHead().size()<1) throw new PredicateArgumentIdentificationException("BUG");
			S surfaceSyntacticHead = nodePathType.getPathFromPredicateHead().get(0);
			S deepAntecedent = ArgumentIdentificationUtilities.getDeepAntecedent(nodePathType.getNode());
			S syntacticRepresentative = null;
			if (nodePathType.getNode()!=deepAntecedent)
			{
				syntacticRepresentative = nodePathType.getNode();
			}
			if (checkIfClausal(nodePathType))
			{
				// it is clausal
				ClausalArgument<I, S> clausalArgument = new ClausalArgument<I, S>(nodePathType.getPathFromPredicateHead(),deepAntecedent,syntacticRepresentative,nodePathType.getArgumentType());
				clausalArguments.add(clausalArgument);
			}
			else
			{
				// it is not clausal
				Set<S> argumentNodes = ArgumentIdentificationUtilities.getInternalNodes(predicate,deepAntecedent);
				Argument<I, S> argument = new Argument<I, S>(argumentNodes,deepAntecedent,surfaceSyntacticHead,syntacticRepresentative);
				TypedArgument<I, S> typedArgument = new TypedArgument<I, S>(argument,nodePathType.getArgumentType(),nodePathType.getPathFromPredicateHead());
				arguments.add(typedArgument);
			}
		}
	}
	
	private boolean checkIfClausal(NodeAndPathFromPredicateAndArgumentType<I,S> nodePathType)
	{
		boolean ret = false;
		String nodeRelation = InfoGetFields.getRelation(nodePathType.getNode().getInfo());
		if (RelationTypes.getSemanticNewClauseRelations().contains(nodeRelation))
		{
			ret = true;
		}
		
		if (false == ret){if (nodePathType.getPathFromPredicateHead()!=null){if (nodePathType.getPathFromPredicateHead().size()>=1)
		{
			String pathRelation = InfoGetFields.getRelation(nodePathType.getPathFromPredicateHead().get(0).getInfo());
			if (RelationTypes.getSemanticNewClauseRelations().contains(pathRelation))
			{
				ret = true;
			}
		}}}
		
		return ret;
	}
	
	private Set<S> getPredicateInternalNodes(S node, Set<S> excludedDirectChildren)
	{
		Set<S> ret = new LinkedHashSet<S>();
		ret.add(node);
		if (node.hasChildren())
		{
			for (S child : node.getChildren())
			{
				if (!excludedDirectChildren.contains(child))
				{
					String relation = InfoGetFields.getRelation(child.getInfo());
					if (RelationTypes.getSemanticInternalFacetRelations().contains(relation))
					{
						ret.addAll(getPredicateInternalNodes(child));
					}
				}
			}
		}
		return ret;
	}
	
	private Set<S> getPredicateInternalNodes(S node)
	{
		Set<S> ret = new LinkedHashSet<S>();
		ret.add(node);
		if (node.hasChildren())
		{
			for (S child : node.getChildren())
			{
				String relation = InfoGetFields.getRelation(child.getInfo());
				if (RelationTypes.getSemanticInternalFacetRelations().contains(relation))
				{
					ret.addAll(getPredicateInternalNodes(child));
				}
			}
		}
		return ret;
	}

	
	
	/**
	 * If the given child is an argument of its parent (the parent is the predicate head), then this
	 * method returns its argument-type.
	 * Otherwise (this child is not an argument at all), this method returns null.
	 * 
	 * @param directChild A node in the parse tree which is a direct child of the predicate-head.
	 * @return
	 */
	private ArgumentType isDirectChildArgument(S directChild)
	{
		ArgumentType ret = null;
		
		String relation = InfoGetFields.getRelation(directChild.getInfo());
		for (NomlexArgument nomlexArgument : nominalization.getMapArgumentToType().keySet())
		{
			if (null==nomlexArgument.getPreposition())
			{
				if (PlaceToRelationMap.getMapPlaceToRelation().get(nomlexArgument.getPlace()).contains(relation))
				{
					ret = nominalization.getMapArgumentToType().get(nomlexArgument);
					break;
				}
				
			}
		}
		return ret;
	}
	
	/**
	 * Finds all the arguments that are connected to the predicate-head by a preposition (e.g. "employment of John" -
	 * "employment" is the predicate, and "John" is an argument, and the argument is connected via a preposition ("of") to the
	 * predicate head).
	 * 
	 * @return All arguments that are connected to the predicate via a preposition.
	 */
	@StandardSpecific("nomlex")
	private Set<NodeAndPathFromPredicateAndArgumentType<I,S>> getArgumentsByPrepositions()
	{
		Set<NodeAndPathFromPredicateAndArgumentType<I,S>> ret = new LinkedHashSet<NodeAndPathFromPredicateAndArgumentType<I,S>>();
		if (predicateHead.hasChildren())
		{
			for (S child : predicateHead.getChildren()) // for each child
			{
				if (SimplerCanonicalPosTag.PREPOSITION.equals(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(child.getInfo())))) // if this child is a preposition
				{
					// create a path from the predicate to an argument - the path is a path of prepositions.
					List<S> path = new ArrayList<S>(1);
					path.add(child);
					S current = child;
					while (current!=null)
					{
						current = ArgumentIdentificationUtilities.getChildOfPreposition(current);
						if (current!=null)
						{
							if (!SimplerCanonicalPosTag.PREPOSITION.equals(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(current.getInfo()))))
							{
								break;
							}
							else
							{
								path.add(current);
							}
						}
					}
					if (current != null) // the lowest node in the path has a child - this child is the argument.
					{
						if (path.size()==1) // the path contains only one node - only one preposition. Thus - it might be a Nomlex argument.
						{
							// Create a NomlexArgument object, which represents this preposition, and find the argument type.
							String preposition = InfoGetFields.getLemma(path.get(0).getInfo());
							NomlexArgument observedNomlexArgument = new NomlexArgument(PREPOSITION_PLACE,preposition);
							if (nominalization.getMapArgumentToType().containsKey(observedNomlexArgument))
							{
								// Find the argument type, and add it to the returned set of arguments.
								ArgumentType argumentType = nominalization.getMapArgumentToType().get(observedNomlexArgument);
								ret.add(new NodeAndPathFromPredicateAndArgumentType<I,S>(current,path,argumentType));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	
	private static class NodeAndPathFromPredicateAndArgumentType<I extends Info, S extends AbstractNode<I, S>>
	{
		public NodeAndPathFromPredicateAndArgumentType(S node,
				List<S> pathFromPredicateHead, ArgumentType argumentType)
		{
			super();
			this.node = node;
			this.pathFromPredicateHead = pathFromPredicateHead;
			this.argumentType = argumentType;
		}
		
		
		
		public S getNode()
		{
			return node;
		}
		public List<S> getPathFromPredicateHead()
		{
			return pathFromPredicateHead;
		}
		public ArgumentType getArgumentType()
		{
			return argumentType;
		}



		private final S node;
		private final List<S> pathFromPredicateHead;
		private final ArgumentType argumentType;
	}

	// input
	private final TreeAndParentMap<I, S> tree;
	private final ImmutableMap<String, Nominalization> nomlexMap;
	private final S predicateHead;
	
	
	// internals
	private Nominalization nominalization;
	private Set<S> predicateNodes;
	private Set<TypedArgument<I, S>> arguments;
	private Set<ClausalArgument<I, S>> clausalArguments;

	
	// output
	private PredicateArgumentStructure<I, S> predicateArgumentStructure;
}
