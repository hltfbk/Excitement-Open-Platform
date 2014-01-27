package eu.excitementproject.eop.transformations.operations.rules.distsimnew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.LeastCommonAncestor;
import eu.excitementproject.eop.common.representation.parse.tree.LeastCommonAncestor.LeastCommonAncestorException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

import static eu.excitementproject.eop.transformations.utilities.Constants.TEMPLATES_FROM_TREE_CACHE_SIZE;

/**
 * Extracts DIRT-templates from a given parse-tree.
 *  
 * @author Asher Stern
 * @since Dec 22, 2013
 *
 */
public class TemplatesFromTree<I extends Info, S extends AbstractNode<I, S>>
{
	////////// PUBLIC //////////
	
	public TemplatesFromTree(S tree) throws TeEngineMlException
	{
		this.tree = tree;
	}
	
	public void createTemplate() throws TeEngineMlException
	{
		templates = null;
		Set<String> theTemplates = null;
		if (cache.containsKey(tree))
		{
			synchronized(cache)
			{
				if (cache.containsKey(tree)) // the if condition appears twice, for efficiency (avoiding synchronized if possible).
				{
					theTemplates = cache.get(tree);
				}
			}
		}
		if (null==theTemplates)
		{
			theTemplates = createTemplateNotInCache();
			cache.put(tree, theTemplates);
		}
		templates = sortAndSeal(theTemplates);
	}
	

	
	
	
	public Set<String> getTemplates() throws TeEngineMlException
	{
		if (null==templates) throw new TeEngineMlException("Templates were not created.");
		return templates;
	}
	
	////////// PRIVATE //////////
	
	private void init() throws TeEngineMlException
	{
		this.parentMap = AbstractNodeUtils.parentMap(tree);
		lca = new LeastCommonAncestor<I, S>(tree);
		try
		{
			lca.compute();
		}
		catch (LeastCommonAncestorException e)
		{
			throw new TeEngineMlException("Failed to initialize TemplatesFromTree. See nested exception",e);
		}
	}
	
	
	private Set<String> createTemplateNotInCache() throws TeEngineMlException
	{
		init();
		Set<String> theTemplates = new LinkedHashSet<>();
		List<S> endpoints = findEndpoints();
		for (S begin : endpoints)
		{
			for (S end : endpoints)
			{
				if (
					(end != begin)
					&&
					(parentMap.get(begin)!=end)
					&&
					(parentMap.get(end)!=begin)
					)
				{
					try
					{
						S common = lca.getLeastCommonAncestorOf(begin, end);
						if (!filter(begin,end,common))
						{
							theTemplates.add(pathToString(begin,end,common));
						}
					}
					catch (LeastCommonAncestorException e)
					{
						throw new TeEngineMlException("Failed to find templates. See nested exception.",e);
					}
				}
			}
		}
		return theTemplates;
	}
	
	
	private static <T extends Comparable<T>> Set<T> sortAndSeal(Set<T> set)
	{
		List<T> list = new ArrayList<>(set.size());
		list.addAll(set);
		Collections.sort(list);
		Set<T> newSet = new LinkedHashSet<>();
		for (T t : list)
		{
			newSet.add(t);
		}
		return Collections.unmodifiableSet(newSet);
	}
	
	
	// Filtering methods here:
	
	private boolean lemmaOK(String lemma)
	{
		for (char c : lemma.toCharArray())
		{
			if (!Character.isLetter(c))
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean lemmasOK(S begin, S end, S common)
	{
		boolean ret = true;
		List<S> endpoints = new ArrayList<>(1+1);
		if (begin!=common) {endpoints.add(begin);}
		if (end != common) {endpoints.add(end);}
		for (S endpoint : endpoints)
		{
			if (true==ret)
			{
				S current = endpoint;
				while (current != common)
				{
					if (current != endpoint)
					{
						if (!("conj".equalsIgnoreCase(InfoGetFields.getRelation(current.getInfo()))))
						{
							String lemma = InfoGetFields.getLemma(current.getInfo());
							if (!lemmaOK(lemma))
							{
								ret = false;
								break;
							}
						}
					}
					current = parentMap.get(current);
				}
			}
		}
		String commonLemma = InfoGetFields.getLemma(common.getInfo());
		if (true==ret)
		{
			if ( (common!=begin) && (common!=end) )
			{
				ret = ret && lemmaOK(commonLemma);
			}
		}
		
		return ret;
	}
	
	private boolean filterAntecedent(S begin, S end, S common)
	{
		if ( (begin.getAntecedent()==end) || (end.getAntecedent()==begin) )
		{
			return true;
		}
		return false;
	}
	
	private boolean filterLemmas(S begin, S end, S common)
	{
		if (!(lemmasOK(begin,end,common)))
		{
			return true;
		}
		return false;
	}
	
	private boolean filterLength(S begin, S end, S common)
	{
		boolean ret = false;
		int contentCounter = 0;
		@SuppressWarnings("unused")
		int counter = 0;

		List<S> endpoints = new ArrayList<>(1+1);
		endpoints.add(begin);
		endpoints.add(end);
		for (S endpoint : endpoints)
		{
			S current = endpoint;
			while (current!=common)
			{
				if (current!=endpoint)
				{
					if (!("conj".equalsIgnoreCase(InfoGetFields.getRelation(current.getInfo()))))
					{
						++counter;
						if (posIsContent(current))
						{
							++contentCounter;
						}
					}
				}
				current = parentMap.get(current);
			}
		}

		// for common:
		if ( (common!=begin) && (common!=end) )
		{
			++counter;
			if (posIsContent(common))
			{
				++contentCounter;
			}
		}


		if ( (contentCounter<1) || (contentCounter>5) )
		{
			ret = true;
		}
		
		return ret;
	}
	
	private boolean filterConjDirectCommonChild(S begin, S end, S common)
	{
		boolean ret = false;
		List<S> endpoints = new ArrayList<>(1+1);
		if (begin!=common) {endpoints.add(begin);}
		if (end != common) {endpoints.add(end);}
		for (S endpoint : endpoints)
		{
			if (ret != true)
			{
				S current = endpoint;
				while (current!=common)
				{
					if (parentMap.get(current)==common)
					{
						String relation = InfoGetFields.getRelation(current.getInfo());
						if ("conj".equalsIgnoreCase(relation))
						{
							ret = true;
						}
					}

					current = parentMap.get(current);
				}
			}
		}
		return ret;
	}
	
	private boolean filterConjEndpoint(S begin, S end, S common)
	{
		boolean ret = false;
		List<S> endpoints = new ArrayList<>(1+1);
		if (begin!=common) {endpoints.add(begin);}
		if (end != common) {endpoints.add(end);}
		for (S endpoint : endpoints)
		{
			String relation = InfoGetFields.getRelation(endpoint.getInfo());
			if ("conj".equalsIgnoreCase(relation))
			{
				ret = true;
				break;
			}
		}
		return ret;
		

	}
	

	@SuppressWarnings("unused")
	private boolean filterCrossClauses(S begin, S end, S common)
	{
		boolean ret = false;
		List<S> endpoints = new ArrayList<>(1+1);
		endpoints.add(begin);
		endpoints.add(end);
		for (S endpoint : endpoints)
		{
			if (ret != true)
			{
				S current = endpoint;
				while (current!=common)
				{
					S parent = parentMap.get(current);
					String relation = InfoGetFields.getRelation(current.getInfo());
					SimplerCanonicalPosTag posParent = SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(parent.getInfo()));
					if (SimplerCanonicalPosTag.VERB.equals(posParent))
					{
						if (NEW_CLAUSE_RELATIONS.contains(relation))
						{
							ret = true;
							break;
						}
					}
					current = parentMap.get(current);
				}
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unused")
	private boolean filterSameWord(S begin, S end, S common)
	{
		String lemmaBegin = InfoGetFields.getLemma(begin.getInfo());
		String lemmaEnd = InfoGetFields.getLemma(end.getInfo());
		if (lemmaBegin.equalsIgnoreCase(lemmaEnd))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean filter(S begin, S end, S common)
	{
		boolean ret = false;

		//if (!ret) {ret = filterSameWord(begin, end, common);}
		if (!ret) {ret = filterAntecedent(begin, end, common);}
		if (!ret) {ret = filterLemmas(begin, end, common);}
		if (!ret) {ret = filterConjEndpoint(begin, end, common);}
		if (!ret) {ret = filterConjDirectCommonChild(begin, end, common);}
		//if (!ret) {ret = filterCrossClauses(begin, end, common);}
		if (!ret) {ret = filterLength(begin, end, common);}
		
		return ret;
	}
	
	private boolean posIsContent(S node)
	{
		SimplerCanonicalPosTag pos = SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()));
		switch(pos)
		{
		case NOUN:
			return true;
		case VERB:
			return true;
		case ADJECTIVE:
			return true;
		case ADVERB:
			return true;
		default:
			return false;
		}
	}

	
	// End of filtering methods

	
	private List<S> findEndpoints()
	{
		List<S> ret = new LinkedList<>();
		for (S node : TreeIterator.iterableTree(tree))
		{
			SimplerCanonicalPosTag pos = SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()));
			if ( (pos.equals(SimplerCanonicalPosTag.NOUN)) || (pos.equals(SimplerCanonicalPosTag.PRONOUN)) )
			{
				ret.add(node);
			}
		}
		return ret;
	}

	private String pathToString(S begin, S end, S common)
	{
		if (begin==common)
		{
			return halfPathToString(end,common,true,false);
		}
		else if (end==common)
		{
			return halfPathToString(begin,common,true,true);
		}
		else
		{
			String up = halfPathToString(begin,common,false,true);
			String down = halfPathToString(end,common,false,false);
			down = down.substring(down.indexOf(">"));
			return up+down;
		}
	}
	
	private String halfPathToString(S begin, S common, boolean commonIsEndpoint ,boolean direction) // direction=true means up
	{
		Stack<String> stack = new Stack<>();
		stack.push(posOfInfo(begin.getInfo()));
		if (begin!=common)
		{
			stack.push(relationToPrint(begin));
			S previous = begin;
			S current = parentMap.get(begin);
			while (current != common)
			{
				if ("conj".equalsIgnoreCase(InfoGetFields.getRelation(previous.getInfo())))
				{
					// skip it
				}
				else
				{
					stack.push(infoToString(current.getInfo()));
					stack.push(relationToPrint(current));
				}
				previous = current;
				current = parentMap.get(current);
			}
			if (commonIsEndpoint)
			{
				stack.push(posOfInfo(common.getInfo()));
			}
			else
			{
				stack.push(infoToString(common.getInfo()));
			}
		}
		String delimiter = ">";
		if (direction)
		{
			Stack<String> temp = new Stack<String>();
			while (!stack.isEmpty())
			{
				temp.push(stack.pop());
			}
			stack = temp;
			delimiter = "<";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(stack.pop());
		while (!stack.isEmpty())
		{
			sb.append(delimiter);
			sb.append(stack.pop());
		}
		return sb.toString();
	}
	
	private String relationToPrint(S node)
	{
		String relation = InfoGetFields.getRelation(node.getInfo());
		if (relation.equalsIgnoreCase("conj"))
		{
			S parent = parentMap.get(node);
			if (parentMap.get(parent)!=null)
			{
				relation = InfoGetFields.getRelation(parent.getInfo());
			}
		}
		return relation;
	}
	
	private String infoToString(I info)
	{
		String lemma = InfoGetFields.getLemma(info).trim().toLowerCase();
		lemma.replaceAll("<", "#");
		lemma.replaceAll(">", "#");
		lemma.replaceAll(":", "#");
		String pos = posOfInfo(info);
		return pos+":"+lemma+":"+pos;
	}
	
	private String posOfInfo(I info)
	{
		String ret = null;
		switch(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(info)))
		{
		case ADJECTIVE:
			ret = "a";
			break;
		case ADVERB:
			ret = "a";
			break;
		case DETERMINER:
			ret = "d";
			break;
		case NOUN:
			ret = "n";
			break;
		case OTHER:
			ret = "o";
			break;
		case PREPOSITION:
			ret = "p";
			break;
		case PRONOUN:
			ret = "p";
			break;
		case PUNCTUATION:
			ret = "o";
			break;
		case VERB:
			ret = "v";
			break;
		default: 
			ret = "o";
			break;
		}
		return ret;
	}
	
	
	
	
	private static final String[] NEW_CLAUSE_RELATIONS_ARRAY = new String[]{
		"advcl",
		//"partmod",
		"prepc",
		"purpcl",
		"ccomp",
		"csubjpass",
		"csubj",
		"parataxis",
		"pcomp",
		"rcmod",
		//"xcomp",
		"infmod"
	};
	private static final Set<String> NEW_CLAUSE_RELATIONS = Utils.arrayToCollection(NEW_CLAUSE_RELATIONS_ARRAY, new LinkedHashSet<String>());	
		
			
	
	
	private S tree;
	private Map<S, S> parentMap;
	private LeastCommonAncestor<I, S> lca;
	
	private Set<String> templates = null;

	private static Cache<AbstractNode<?,?>, Set<String>> cache = new CacheFactory<AbstractNode<?,?>, Set<String>>().getCache(TEMPLATES_FROM_TREE_CACHE_SIZE);
}
