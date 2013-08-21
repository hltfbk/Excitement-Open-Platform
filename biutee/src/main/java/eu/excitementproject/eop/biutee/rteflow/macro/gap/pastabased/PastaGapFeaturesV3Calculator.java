package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * 
 * @author Asher Stern
 * @since Aug 20, 2013
 *
 * @param <I>
 * @param <S>
 */
public class PastaGapFeaturesV3Calculator<I extends Info, S extends AbstractNode<I, S>>
{
	public PastaGapFeaturesV3Calculator(TreeAndParentMap<I, S> hypothesisTree,
			Set<PredicateArgumentStructure<I, S>> hypothesisStructures,
			TreeAndParentMap<I, S> textTree,
			Set<PredicateArgumentStructure<I, S>> textStructures)
	{
		super();
		this.hypothesisTree = hypothesisTree;
		this.hypothesisStructures = hypothesisStructures;
		this.textTree = textTree;
		this.textStructures = textStructures;
	}
	
	
	public void calculate() throws GapException
	{
		buildContentLemmasOfHypothesis();
		buildLemmasOfText();
		buildArgumentMap();
		calculateLists();
	}
	
	
	
	
	public List<PredicateAndArgument<I, S>> getCalculatedNoMatchNamedEntities()
	{
		return calculatedNoMatchNamedEntities;
	}


	public List<PredicateAndArgument<I, S>> getCalculatedNoMatch()
	{
		return calculatedNoMatch;
	}


	public List<PredicateAndArgument<I, S>> getCalculatedMatchWrongPredicateMissingWords()
	{
		return calculatedMatchWrongPredicateMissingWords;
	}


	public List<PredicateAndArgument<I, S>> getCalculatedMatchWrongPredicate()
	{
		return calculatedMatchWrongPredicate;
	}


	public List<PredicateAndArgument<I, S>> getCalculatedMatchMissingWords()
	{
		return calculatedMatchMissingWords;
	}


	public List<PredicateAndArgument<I, S>> getCalculatedMatch()
	{
		return calculatedMatch;
	}
	
	public Set<String> getCalculatedTotallyOmittedHypothesisContentLemmas()
	{
		return calculatedTotallyOmittedHypothesisContentLemmas;
	}

	
	//////////////////// PRIVATE ////////////////////

	private void buildContentLemmasOfHypothesis()
	{
		contentLemmasOfHypothesis_lowerCase = contentLemmasOfNodes_lowerCase(TreeIterator.iterableTree(hypothesisTree.getTree()));
	}
	
	private void buildLemmasOfText()
	{
		lemmasOfText_lowerCase = new LinkedHashSet<>();
		for (S node : TreeIterator.iterableTree(textTree.getTree()))
		{
			lemmasOfText_lowerCase.add(InfoGetFields.getLemma(node.getInfo()).toLowerCase());
		}
	}
	
	private void buildArgumentMap()
	{
		mapArgumentsHypothesisToText = new SimpleValueSetMap<>();
		hypothesisArguments = listOfArguments(hypothesisStructures);
		textArguments = listOfArguments(textStructures);
		
		for (PredicateAndArgument<I, S> hypothesisArgument : hypothesisArguments)
		{
			for (PredicateAndArgument<I, S> textArgument : textArguments)
			{
				if (argumentsMatch(textArgument,hypothesisArgument))
				{
					mapArgumentsHypothesisToText.put(hypothesisArgument, textArgument);	
				}
			}
		}
	}
	
	private boolean argumentsMatch(PredicateAndArgument<I, S> textArgument, PredicateAndArgument<I, S> hypothesisArgument)
	{
		String hypothesisLemma = InfoGetFields.getLemma(hypothesisArgument.getArgument().getArgument().getSemanticHead().getInfo()).toLowerCase();
		if (BiuteeConstants.PASTA_GAP_STRICT_ARGUMENT_HEAD_MODE)
		{
			String textLemma = InfoGetFields.getLemma(textArgument.getArgument().getArgument().getSemanticHead().getInfo()).toLowerCase();
			return textLemma.equals(hypothesisLemma);
		}
		else
		{
			return (TreeUtilities.lemmasLowerCaseOfNodes(textArgument.getArgument().getArgument().getNodes()).contains(hypothesisLemma));
		}
	}
	
	private List<PredicateAndArgument<I, S>> listOfArguments(Set<PredicateArgumentStructure<I, S>> structures)
	{
		List<PredicateAndArgument<I, S>> ret = new LinkedList<>();
		for (PredicateArgumentStructure<I, S> structure : structures)
		{
			for (TypedArgument<I, S> argument : structure.getArguments())
			{
				ret.add(new PredicateAndArgument<I,S>(structure, argument));
			}
		}
		return ret;
	}
	
	
	
	private void calculateLists()
	{
		calculatedNoMatchNamedEntities = new LinkedList<>();
		calculatedNoMatch = new LinkedList<>();
		calculatedMatchWrongPredicateMissingWords = new LinkedList<>();
		calculatedMatchWrongPredicate = new LinkedList<>();
		calculatedMatchMissingWords = new LinkedList<>();
		calculatedMatch = new LinkedList<>();

		for (PredicateAndArgument<I, S> hypothesisArgument : hypothesisArguments)
		{
			//if (!mapArgumentsHypothesisToText.containsKey(hypothesisArgument))

			String hypothesisArgumentLemma_lowerCase = InfoGetFields.getLemma(hypothesisArgument.getArgument().getArgument().getSemanticHead().getInfo()).toLowerCase();
			if (!lemmasOfText_lowerCase.contains(hypothesisArgumentLemma_lowerCase))
			{
				boolean namedEntity = (InfoGetFields.getNamedEntityAnnotation(hypothesisArgument.getArgument().getArgument().getSemanticHead().getInfo())!=null);
				if (namedEntity)
				{
					calculatedNoMatchNamedEntities.add(hypothesisArgument);
				}
				else
				{
					calculatedNoMatch.add(hypothesisArgument);
				}
			}
			else
			{
				boolean predicateOK = false;
				boolean wordsOK = false;
				
				Set<String> hypothesisArgumentContentLemmas_lowerCase = contentLemmasOfArgument_lowerCase(hypothesisArgument.getArgument());
				
				if (mapArgumentsHypothesisToText.containsKey(hypothesisArgument))
				{
					for (PredicateAndArgument<I, S> textArgument : mapArgumentsHypothesisToText.get(hypothesisArgument))
					{
						if (!predicateOK) {if (samePredicate(hypothesisArgument, textArgument))
						{
							predicateOK=true;
						}}
						if (!wordsOK) { if(  allLemmasOfArgument_lowerCase(textArgument.getArgument()).containsAll(hypothesisArgumentContentLemmas_lowerCase)  )
						{
							wordsOK=true;
						}}
					}
				}
				
				if (predicateOK&&wordsOK) {calculatedMatch.add(hypothesisArgument);}
				else if ((!predicateOK)&&wordsOK) {calculatedMatchWrongPredicate.add(hypothesisArgument);}
				else if (predicateOK&&(!wordsOK)) {calculatedMatchMissingWords.add(hypothesisArgument);}
				else if ((!predicateOK)&&(!wordsOK)) {calculatedMatchWrongPredicateMissingWords.add(hypothesisArgument);}
			}
		}
		
		calculatedTotallyOmittedHypothesisContentLemmas = new LinkedHashSet<>();
		for (String contentLemma : contentLemmasOfHypothesis_lowerCase)
		{
			if (!lemmasOfText_lowerCase.contains(contentLemma))
			{
				calculatedTotallyOmittedHypothesisContentLemmas.add(contentLemma);
			}
		}
	}
	
	
	
	private boolean samePredicate(PredicateAndArgument<I, S> hypothesisArgument, PredicateAndArgument<I, S> textArgument)
	{
		return (Utils.intersect(
				predicateLemmasLowerCase(hypothesisArgument.getPredicate().getPredicate()), 
				predicateLemmasLowerCase(textArgument.getPredicate().getPredicate()),
				new LinkedHashSet<String>()
				).size()>0);
	}
	
	private Set<String> predicateLemmasLowerCase(Predicate<I, S> predicate)
	{
		String mainLemma = InfoGetFields.getLemma(predicate.getHead().getInfo()).toLowerCase();
		if (null==predicate.getVerbsForNominal())
		{
			return Collections.singleton(mainLemma);
		}
		else
		{
			Set<String> ret = new LinkedHashSet<>();
			ret.add(mainLemma);
			for (String verbal : predicate.getVerbsForNominal())
			{
				ret.add(verbal.toLowerCase());
			}
			return ret;
		}
	}
	
	
	private Set<String> contentLemmasOfArgument_lowerCase(TypedArgument<I, S> argument)
	{
		return contentLemmasOfNodes_lowerCase(argument.getArgument().getNodes());
	}

	private Set<String> contentLemmasOfNodes_lowerCase(Iterable<S> nodes)
	{
		Set<String> ret = new LinkedHashSet<>();
		for (S node : nodes)
		{
			if (InfoObservations.infoIsContentWord(node.getInfo()))
			{
				ret.add(InfoGetFields.getLemma(node.getInfo()).toLowerCase());
			}
		}
		return ret;
	}

	
	private Set<String> allLemmasOfArgument_lowerCase(TypedArgument<I, S> argument)
	{
		Set<String> ret = new LinkedHashSet<>();
		for (S node : argument.getArgument().getNodes())
		{
			ret.add(InfoGetFields.getLemma(node.getInfo()).toLowerCase());
		}
		return ret;
	}
	
	
	
	
	// input
	private final TreeAndParentMap<I, S> hypothesisTree;
	private final Set<PredicateArgumentStructure<I, S>> hypothesisStructures;
	private final TreeAndParentMap<I, S> textTree;
	private final Set<PredicateArgumentStructure<I, S>> textStructures;
	
	
	// internals
	private Set<String> contentLemmasOfHypothesis_lowerCase;
	private Set<String> lemmasOfText_lowerCase;
	private ValueSetMap<PredicateAndArgument<I, S>, PredicateAndArgument<I, S>> mapArgumentsHypothesisToText;
	private List<PredicateAndArgument<I, S>> hypothesisArguments;
	private List<PredicateAndArgument<I, S>> textArguments;
	
	// output
	private List<PredicateAndArgument<I, S>> calculatedNoMatchNamedEntities;
	private List<PredicateAndArgument<I, S>> calculatedNoMatch;
	private List<PredicateAndArgument<I, S>> calculatedMatchWrongPredicateMissingWords;
	private List<PredicateAndArgument<I, S>> calculatedMatchWrongPredicate;
	private List<PredicateAndArgument<I, S>> calculatedMatchMissingWords;
	private List<PredicateAndArgument<I, S>> calculatedMatch;
	private Set<String> calculatedTotallyOmittedHypothesisContentLemmas;
}
