package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2013
 *
 * @param <I>
 * @param <S>
 */
public class PastaGapFeaturesCalculator<I extends Info, S extends AbstractNode<I, S>>
{
	///////////// PUBLIC /////////////
	
	public static final boolean STRICT_ARGUMENT_HEAD_MODE = BiuteeConstants.PASTA_GAP_STRICT_ARGUMENT_HEAD_MODE;

	
	public PastaGapFeaturesCalculator(TreeAndParentMap<I, S> hypothesisTree,
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
		buildMatchingMap();
		buildMissingLemmasInText();
		calculateAll();
	}
	
	
	

	public List<PredicateArgumentStructure<I, S>> getMissingPredicates()
	{
		return missingPredicates;
	}

	public List<PredicateAndArgument<I, S>> getArgumentHeadNotConnected()
	{
		return argumentHeadNotConnected;
	}

	public List<PredicateAndArgument<I, S>> getMissingArgument()
	{
		return missingArgument;
	}

	public List<PredicateAndArgumentAndNode<I, S>> getLemmaNotInArgument()
	{
		return lemmaNotInArgument;
	}

	public List<PredicateAndArgumentAndNode<I, S>> getMissingLemmaOfArgument()
	{
		return missingLemmaOfArgument;
	}
	
	public List<S> getMissingNodes()
	{
		return missingNodes;
	}
	
	///////////// PRIVATE /////////////

	private void calculateAll()
	{
		missingPredicates = new LinkedList<>();
		argumentHeadNotConnected = new LinkedList<>();
		missingArgument = new LinkedList<>();
		lemmaNotInArgument = new LinkedList<>();
		missingLemmaOfArgument = new LinkedList<>();
		
		for (PredicateArgumentStructure<I, S> hypothesisStructure : hypothesisStructures)
		{
			if (matchingPredicates.containsKey(hypothesisStructure))
			{
				for (TypedArgument<I,S> hypothesisArgument : hypothesisStructure.getArguments())
				{
					Set<PredicateAndArgument<I, S>> textMatchingArguments = structuresHaveArgument(matchingPredicates.get(hypothesisStructure),hypothesisArgument);
					if (textMatchingArguments.size()>0)
					{
						Set<String> lemmasInText = matchingLemmasOfArguments(textMatchingArguments);
						Map<String,S> lemmasInHypothesis = lemmasOfArgument(hypothesisArgument);
						Set<String> lemmasInHypothesisArgumentButNotInTextArgument = setMinus(lemmasInHypothesis.keySet(), lemmasInText);
						for (String missingLemma : lemmasInHypothesisArgumentButNotInTextArgument)
						{
							S nodeOfMissingLemma = lemmasInHypothesis.get(missingLemma);
							if (isContentNode(nodeOfMissingLemma))
							{
								PredicateAndArgumentAndNode<I, S> missingLemmaAsPAAAN = new PredicateAndArgumentAndNode<I, S>(hypothesisStructure, hypothesisArgument, nodeOfMissingLemma);
								if (!(missingLemmasInText.contains(missingLemma)))
								{
									lemmaNotInArgument.add(missingLemmaAsPAAAN);
								}
								else
								{
									missingLemmaOfArgument.add(missingLemmaAsPAAAN);
								}
							}
						}
					}
					else
					{
						PredicateAndArgument<I, S> missingHypothesisArgument = new PredicateAndArgument<I, S>(hypothesisStructure,hypothesisArgument);
						String hypothesisArgumentLemma = InfoGetFields.getLemma(hypothesisArgument.getArgument().getSemanticHead().getInfo());
						if (missingLemmasInText.contains(hypothesisArgumentLemma))
						{
							missingArgument.add(missingHypothesisArgument);
						}
						else
						{
							argumentHeadNotConnected.add(missingHypothesisArgument);
							
						}
					}
				}
			}
			else
			{
				missingPredicates.add(hypothesisStructure);
			}
		}

	}
	
	private Set<String> matchingLemmasOfArguments(Set<PredicateAndArgument<I, S>> textMatchingArguments)
	{
		Set<String> ret = new LinkedHashSet<>();
		for (PredicateAndArgument<I, S> argument : textMatchingArguments)
		{
			for (S node : argument.getArgument().getArgument().getNodes())
			{
				ret.add(InfoGetFields.getLemma(node.getInfo()));
			}
		}
		return ret;
	}
	
	private Map<String,S> lemmasOfArgument(TypedArgument<I, S> argument)
	{
		Map<String,S> ret = new LinkedHashMap<String,S>();
		for (S node : argument.getArgument().getNodes())
		{
			String lemma = InfoGetFields.getLemma(node.getInfo());
			if (!(ret.containsKey(lemma)))
			{
				ret.put(lemma,node);
			}
		}
		return ret;
	}
	
	private Set<PredicateAndArgument<I, S>> structuresHaveArgument(Iterable<PredicateArgumentStructure<I, S>> textStructures, TypedArgument<I, S> hypothesisArgument)
	{
		Set<PredicateAndArgument<I, S>> ret = new LinkedHashSet<>();
		for (PredicateArgumentStructure<I, S> textStructure : textStructures)
		{
			for (TypedArgument<I, S> textArgument : structureHasArgument(textStructure,hypothesisArgument))
			{
				ret.add(new PredicateAndArgument<I,S>(textStructure, textArgument));
			}
		}
		return ret;
	}
	
	private Set<TypedArgument<I, S>> structureHasArgument(PredicateArgumentStructure<I, S> textStructure, TypedArgument<I, S> hypothesisArgument)
	{
		Set<TypedArgument<I, S>> ret = new LinkedHashSet<>();
		String hypothesisArgumentHeadLowerCase = InfoGetFields.getLemma(hypothesisArgument.getArgument().getSemanticHead().getInfo()).toLowerCase();
		for (TypedArgument<I, S> argument : textStructure.getArguments())
		{
			boolean add = false;
			if (STRICT_ARGUMENT_HEAD_MODE)
			{
				if (argumentsSameHead(hypothesisArgument,argument))
				{
					add = true;
				}
			}
			else // !STRICT_ARGUMENT_HEAD_MODE
			{
				if (lemmasLowerCaseOfNodes(argument.getArgument().getNodes()).contains(hypothesisArgumentHeadLowerCase))
				{
					add = true;
				}
			}
			if (add)
			{
				ret.add(argument);
			}
		}
		return ret;
	}
	
	private boolean argumentsSameHead(TypedArgument<I, S> argument1, TypedArgument<I, S> argument2)
	{
		String lemma1 = InfoGetFields.getLemma(argument1.getArgument().getSemanticHead().getInfo());
		String lemma2 = InfoGetFields.getLemma(argument2.getArgument().getSemanticHead().getInfo());
		if (lemma1.equalsIgnoreCase(lemma2)) return true;
		else return false;
		
	}
	
	private void buildMissingLemmasInText()
	{
		Map<String,S> hypothesisLemmasAndNodes = getLemmasOfTree(hypothesisTree.getTree());
		Set<String> hypothesisLemmas = hypothesisLemmasAndNodes.keySet();
		Set<String> textLemmas = getLemmasOfTree(textTree.getTree()).keySet();
		missingLemmasInText = setMinus(hypothesisLemmas,textLemmas);
		missingNodes = new ArrayList<S>(missingLemmasInText.size());
		{
			for (String lemma : missingLemmasInText)
			{
				missingNodes.add(hypothesisLemmasAndNodes.get(lemma));
			}
		}
	}
	
	private Map<String,S> getLemmasOfTree(S tree)
	{
		Map<String,S> ret = new LinkedHashMap<>();
		for (S node : TreeIterator.iterableTree(tree))
		{
			String lemma = InfoGetFields.getLemma(node.getInfo());
			if (!(ret.containsKey(lemma)))
			{
				ret.put(lemma,node);
			}
		}
		return ret;
	}
	
	private Set<String> extractLemmasOfPredicateLowerCase(PredicateArgumentStructure<I, S> structure)
	{
		Set<String> ret = null;
		String lemma = InfoGetFields.getLemma(structure.getPredicate().getHead().getInfo()).toLowerCase();
		ImmutableList<String> verbalForms = structure.getPredicate().getVerbsForNominal();
		if (verbalForms!=null)
		{
			ret = new LinkedHashSet<>();
			ret.add(lemma);
			for (String verbalForm : verbalForms)
			{
				ret.add(verbalForm);
			}
		}
		else
		{
			ret = Collections.singleton(lemma);
		}
		return ret;
	}
	
	private boolean structuresAreMatch(PredicateArgumentStructure<I, S> hypothesisStructure, PredicateArgumentStructure<I, S> textStructure)
	{
		return (Utils.intersect(extractLemmasOfPredicateLowerCase(hypothesisStructure),
				extractLemmasOfPredicateLowerCase(textStructure),
				new LinkedHashSet<String>()).size()>0);
	}
	
	private void buildMatchingMap()
	{
		matchingPredicates = new SimpleValueSetMap<PredicateArgumentStructure<I, S>, PredicateArgumentStructure<I, S>>();
		for (PredicateArgumentStructure<I, S> hypothesisStructure : hypothesisStructures)
		{
			for (PredicateArgumentStructure<I, S> textStructure : textStructures)
			{
				if (structuresAreMatch(hypothesisStructure,textStructure))
				{
					matchingPredicates.put(hypothesisStructure, textStructure);
				}
			}
		}
		
		
		
		
		
				
//		ValueSetMap<String, PredicateArgumentStructure<I, S>> hypothesisPredicateHeads = extractHeadLemmasOfStructures(hypothesisStructures);
//		ValueSetMap<String, PredicateArgumentStructure<I, S>> textPredicateHeads = extractHeadLemmasOfStructures(textStructures);
//		
//		for (String hypothesisHead : hypothesisPredicateHeads.keySet())
//		{
//			if (textPredicateHeads.containsKey(hypothesisHead))
//			{
//				for (PredicateArgumentStructure<I, S> hypothesisStructure : hypothesisPredicateHeads.get(hypothesisHead))
//				{
//					for (PredicateArgumentStructure<I, S> textStructure : textPredicateHeads.get(hypothesisHead))
//					{
//						matchingPredicates.put(hypothesisStructure,textStructure);
//					}
//				}
//			}
//		}
	}
	
//	private ValueSetMap<String, PredicateArgumentStructure<I, S>> extractHeadLemmasOfStructures(Set<PredicateArgumentStructure<I, S>> structures)
//	{
//		ValueSetMap<String, PredicateArgumentStructure<I, S>> map = new SimpleValueSetMap<>();
//		for (PredicateArgumentStructure<I, S> structure : structures)
//		{
//			String lemma = InfoGetFields.getLemma(structure.getPredicate().getHead().getInfo());
//			map.put(lemma, structure);
//		}
//		return map;
//	}
	
	private Set<String> lemmasLowerCaseOfNodes(Iterable<S> nodes)
	{
		Set<String> ret = new LinkedHashSet<>();
		for (S node : nodes)
		{
			ret.add(InfoGetFields.getLemma(node.getInfo()).toLowerCase());
		}
		return ret;
	}
	
	private boolean isContentNode(S node)
	{
		boolean ret = false;
		SimplerCanonicalPosTag pos = SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()));
		switch (pos)
		{
		case NOUN:
		case VERB:
		case ADJECTIVE:
		case ADVERB:
			ret=true;
			break;
		default:
			ret = false;
			break;
		}
		return ret;

	}
	
	private static <T> Set<T> setMinus(Set<T> set, Set<T> toBeRemoved)
	{
		Set<T> ret = new LinkedHashSet<>();
		for (T t : set)
		{
			if (!(toBeRemoved.contains(t)))
			{
				ret.add(t);
			}
		}
		return ret;
	}
	
	// input
	private final TreeAndParentMap<I, S> hypothesisTree;
	private final Set<PredicateArgumentStructure<I, S>> hypothesisStructures;
	private final TreeAndParentMap<I, S> textTree;
	private final Set<PredicateArgumentStructure<I, S>> textStructures;

	// internals
	/**
	 * Map from Hypothesis PASs to Text PASs.
	 */
	private ValueSetMap<PredicateArgumentStructure<I, S>, PredicateArgumentStructure<I, S>> matchingPredicates;
	private Set<String> missingLemmasInText;
	
	
	// output
	private List<PredicateArgumentStructure<I,S>> missingPredicates;
	private List<PredicateAndArgument<I, S>> argumentHeadNotConnected;
	private List<PredicateAndArgument<I, S>> missingArgument;
	private List<PredicateAndArgumentAndNode<I, S>> lemmaNotInArgument;
	private List<PredicateAndArgumentAndNode<I, S>> missingLemmaOfArgument;
	private List<S> missingNodes;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PastaGapFeaturesCalculator.class);
}
