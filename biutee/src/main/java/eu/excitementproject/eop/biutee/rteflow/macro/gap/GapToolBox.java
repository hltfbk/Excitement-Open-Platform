package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
@ThreadSafe
public interface GapToolBox<I, S extends AbstractNode<I, S>>
{
	public boolean isHybridMode() throws GapException;
	public GapToolsFactory<I, S> getGapToolsFactory() throws GapException;
}
