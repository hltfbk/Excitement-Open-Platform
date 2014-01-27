package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.ClausalArgument;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.pasta.NominalPredicateHeadsIdentifier;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.Nominalization;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.nominals.NominalPredicateArgumentStructureIdentifier;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals.ArgumentsIdentifier;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals.ClausalArgumentsIdentifier;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals.CopularPredicatesIdentifier;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals.VerbPredicatesIdentifier;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentStructureBuilder;
import static eu.excitementproject.eop.lap.biu.en.pasta.utils.IdentificationStaticMethods.mergeStructureSets;

/**
 * Builds all predicate-argument structures of the given parse-tree, <B>assuming
 * that the given parse-tree was created by {@link EasyFirstParser}</B>.
 * 
 * @author Asher Stern
 * @since Oct 9, 2012
 *
 * @param <I>
 * @param <S>
 */
public class EasyFirstPredicateArgumentStructureBuilder<I extends Info, S extends AbstractNode<I, S>> extends PredicateArgumentStructureBuilder<I, S>
{
	///////////// PUBLIC /////////////
	
	/**
	 * The constructor is given the parse tree, and Nomlex-map which is built by {@link NomlexMapBuilder}.
	 * @param tree
	 * @param nomlexMap
	 */
	public EasyFirstPredicateArgumentStructureBuilder(TreeAndParentMap<I, S> tree, ImmutableMap<String, Nominalization> nomlexMap, PastaMode mode)
	{
		super(tree);
		this.nomlexMap = nomlexMap;
		this.mode = mode;
	}
	

	@Deprecated
	public EasyFirstPredicateArgumentStructureBuilder(TreeAndParentMap<I, S> tree, ImmutableMap<String, Nominalization> nomlexMap)
	{
		this(tree,nomlexMap,PastaMode.BASIC);
	}



	/**
	 * Find all of the predicate argument structures in this tree.
	 */
	@Override
	public void build() throws PredicateArgumentIdentificationException
	{
		predicateArgumentStructures = new LinkedHashSet<PredicateArgumentStructure<I,S>>();
		nominalPredicates = new LinkedHashSet<PredicateArgumentStructure<I,S>>();
		
		buildVerbalPredicates();
		buildNominalPredicates();
		if (mode.isEqualOrGreater(PastaMode.EXPANDED))
		{
			copularPredicates = new LinkedHashSet<PredicateArgumentStructure<I,S>>();
			buildCopularPredicates();
			predicateArgumentStructures.addAll(
					mergeStructureSets(tree,nominalPredicates,copularPredicates));
		}
		else
		{
			predicateArgumentStructures.addAll(nominalPredicates);
		}
		
		
	}
	
	/**
	 * Returns all the predicate argument structures that have been found by {@link #build()}.
	 */
	@Override
	public Set<PredicateArgumentStructure<I, S>> getPredicateArgumentStructures() throws PredicateArgumentIdentificationException
	{
		if (null == predicateArgumentStructures) throw new PredicateArgumentIdentificationException("build() was not called.");
		return predicateArgumentStructures;
	}
	
	///////////// PRIVATE /////////////

	private void buildVerbalPredicates() throws PredicateArgumentIdentificationException
	{
		// Find the verb-predicates
		VerbPredicatesIdentifier<I,S> predicatesIdentifier = new VerbPredicatesIdentifier<I,S>(tree);
		predicatesIdentifier.identifyVerbPredicates();
		Set<Predicate<I, S>> predicates = predicatesIdentifier.getVerbPredicates();
		
		// For each verb-predicate, find its arguments.
		for (Predicate<I, S> predicate : predicates)
		{
			PredicateArgumentStructure<I,S> predicateArgumentStructure = buildForVerbalPredicate(predicate,true);
			predicateArgumentStructures.add(predicateArgumentStructure);
		}
	}
	
	private void buildNominalPredicates() throws PredicateArgumentIdentificationException
	{
		// Find the nominal predicates (only the predicate head)
		NominalPredicateHeadsIdentifier<I, S> nominalPredicateHeadsIdentifier = new NominalPredicateHeadsIdentifier<I, S>(tree,nomlexMap);
		nominalPredicateHeadsIdentifier.identify();
		Set<S> nominalPredicateHeads = nominalPredicateHeadsIdentifier.getPredicateHeads();
		
		// For each nominal-predicate, find the predicate itself (which might more than the predicate-head) and all of its arguments.
		for (S nominalPredicateHead : nominalPredicateHeads)
		{
			NominalPredicateArgumentStructureIdentifier<I, S> nominalPredicateArgumentStructureIdentifier =
					new NominalPredicateArgumentStructureIdentifier<I, S>(tree,nomlexMap,nominalPredicateHead,mode);
			
			nominalPredicateArgumentStructureIdentifier.identify();
			PredicateArgumentStructure<I, S> predicateArgumentStructure = nominalPredicateArgumentStructureIdentifier.getPredicateArgumentStructure();
			nominalPredicates.add(predicateArgumentStructure);
		}
	}
	
	private void buildCopularPredicates() throws PredicateArgumentIdentificationException
	{
		CopularPredicatesIdentifier<I, S> identifier = new CopularPredicatesIdentifier<I, S>(tree);
		identifier.identify();
		Set<Predicate<I, S>> predicates = identifier.getCopularPredicates();
		
		// For each verb-predicate, find its arguments.
		for (Predicate<I, S> predicate : predicates)
		{
			PredicateArgumentStructure<I,S> predicateArgumentStructure = buildForVerbalPredicate(predicate,true);
			copularPredicates.add(predicateArgumentStructure);
		}
	}
	


	private PredicateArgumentStructure<I,S> buildForVerbalPredicate(Predicate<I, S> predicate, boolean itIsVerb) throws PredicateArgumentIdentificationException
	{
		ArgumentsIdentifier<I, S> argumentsIdentifier = new ArgumentsIdentifier<I, S>(tree,predicate,itIsVerb);
		argumentsIdentifier.setAprioriInformation(aprioriInformation);
		argumentsIdentifier.identifyArguments();
		Set<TypedArgument<I, S>> arguments = argumentsIdentifier.getArguments();
		
		ClausalArgumentsIdentifier<I, S> clausalArgumentsIdentifier = new ClausalArgumentsIdentifier<I, S>(tree,predicate);
		clausalArgumentsIdentifier.identifyClausalArguments();
		Set<ClausalArgument<I, S>> clausalArguments = clausalArgumentsIdentifier.getClausalArguments();
		
		PredicateArgumentStructure<I,S> predicateArgumentStructure =
				new PredicateArgumentStructure<I,S>(tree,predicate,arguments,clausalArguments);
		
		return predicateArgumentStructure;
	}
	
	// input
	protected final ImmutableMap<String, Nominalization> nomlexMap;
	protected final PastaMode mode;
	
	// internals
	private Set<PredicateArgumentStructure<I,S>> nominalPredicates = null;
	private Set<PredicateArgumentStructure<I,S>> copularPredicates = null;
	
	// output
	private Set<PredicateArgumentStructure<I,S>> predicateArgumentStructures = null;
}
