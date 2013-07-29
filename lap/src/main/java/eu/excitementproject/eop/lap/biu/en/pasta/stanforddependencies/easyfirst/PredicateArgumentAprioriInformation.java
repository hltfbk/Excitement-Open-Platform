package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * 
 * @author Asher Stern
 * @since Jul 28, 2013
 *
 * @param <I>
 * @param <S>
 */
public interface PredicateArgumentAprioriInformation<I extends Info, S extends AbstractNode<I, S>>
{
	public Boolean checkSemanticPassiveSubject(Predicate<I, S> predicate, List<S> pathFromPredicateToArgument, S syntacticArgumentNode) throws PredicateArgumentIdentificationException;
}
