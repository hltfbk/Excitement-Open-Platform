package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * A factory of {@link GapToolInstances}
 * 
 * @see GapToolBoxFactory
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
public interface GapToolsFactory<I, S extends AbstractNode<I, S>>
{
	public GapToolInstances<I, S> createInstances(final TreeAndParentMap<I, S> hypothesis, final LinearClassifier classifierForSearch) throws GapException;
}
