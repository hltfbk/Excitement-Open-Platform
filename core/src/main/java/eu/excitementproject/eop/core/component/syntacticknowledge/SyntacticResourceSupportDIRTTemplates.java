package eu.excitementproject.eop.core.component.syntacticknowledge;

import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleMatch;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceException;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;

/**
 * An implementation of {@link SyntacticResource} which uses DIRT-like templates.<P>
 * <B>Most important: when calling more than once to {@link #findMatches(AbstractNode, AbstractNode)}, it
 * is assumed that the tree rooted by the node hypothesisTree is never changed between calls.</B>
 * More concretely, the tree hypothesisTree is stored in a cache. If it has been changed between calls to
 * {@link #findMatches(AbstractNode, AbstractNode)}, the change has no effect.
 * <P>
 * This class implements {@link #findMatches(AbstractNode, AbstractNode)} by converting hypothesisTree to a set
 * of DIRT-like templates, followed by a call to {@link #findMatches(AbstractNode, Set)}.
 * 
 * @author Asher Stern
 * @since Feb 16, 2014
 *
 * @param <I>
 * @param <S>
 */
public abstract class SyntacticResourceSupportDIRTTemplates<I,S extends AbstractNode<I,S>> implements SyntacticResource<I,S>
{
	public static final int CACHE_SIZE = 200;

	@Override
	public List<RuleMatch<I, S>> findMatches(S textTree, S hypothesisTree) throws SyntacticResourceException
	{
		Set<String> hypothesisTemplates = getTemplatesForTree(hypothesisTree);
		return findMatches(textTree,hypothesisTemplates);
	}
	
	/**
	 * Finds rules for which the LHS is matched in the textTree, and the RHS is matches in one of the given
	 * hypothesis templates.
	 * @param textTree
	 * @param hypothesisTemplates
	 * @return
	 * @throws SyntacticResourceException
	 */
	public abstract List<RuleMatch<I, S>> findMatches(S textTree, Set<String> hypothesisTemplates) throws SyntacticResourceException;
	
	/**
	 * Extracts DIRT-like dependency-paths from a given tree.  
	 * @param tree
	 * @return
	 * @throws SyntacticResourceException
	 */
	protected abstract Set<String> createTemplatesForTree(S tree) throws SyntacticResourceException;

	/**
	 * Returns (either from the cache or a newly created) set of DIRT-like dependency-paths for the given tree.
	 * @param tree
	 * @return
	 * @throws SyntacticResourceException
	 */
	private Set<String> getTemplatesForTree(S tree) throws SyntacticResourceException
	{
		Set<String> ret = null;
		if (cache.containsKey(tree))
		{
			synchronized(cache)
			{
				if (cache.containsKey(tree))
				{
					ret = cache.get(tree);
				}
			}
		}
		if (null==ret)
		{
			ret = createTemplatesForTree(tree);
			if (null==ret) {throw new SyntacticResourceException("Null templates returned for a given tree.");}
			synchronized(cache)
			{
				cache.put(tree, ret);
			}
		}
		
		return ret;
	}
	
	private Cache<S, Set<String>> cache = new CacheFactory<S, Set<String>>().getCache(CACHE_SIZE);
}
