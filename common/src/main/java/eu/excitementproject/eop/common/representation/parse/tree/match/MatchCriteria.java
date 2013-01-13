package eu.excitementproject.eop.common.representation.parse.tree.match;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultMatchCriteria;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * Used by {@link Matcher} and {@link AllEmbeddedMatcher}
 * The user of {@link Matcher} and {@link AllEmbeddedMatcher} should define the match
 * criteria of the match. For {@link BasicNode} there is a default implementation, named
 * {@link DefaultMatchCriteria}
 * 
 * 
 * @see DefaultMatchCriteria
 * 
 * @author Asher Stern
 * 
 * 
 *
 * @param <TM> The information type of the "main node"s (TM: T = type, M = main) (e.g. {@link Info}) 
 * @param <TT> The information type of the "tested node"s (TT: T = type, T = tested) (e.g. {@link Info})
 * @param <SM> The "main node"s type (SM: S = self, M = main) (e.g. {@link BasicNode})
 * @param <ST> The "tested node"s type (SM: S = self, T = tested) (e.g. {@link BasicNode})
 */
public interface MatchCriteria<TM,TT, SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>>
{
	public boolean nodesMatch(SM mainNode, ST testNode);
	
	public boolean edgesMatch(TM mainInfo, TT testInfo);
}
