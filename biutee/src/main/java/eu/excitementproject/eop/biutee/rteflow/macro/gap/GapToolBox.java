package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * A tool-box, global for the system, which creates utilities to measure gaps
 * between text parse trees and hypothesis parse trees.
 *
 * @see GapToolBoxFactory
 * @see SystemInitialization
 * @see TESystemEnvironment
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
