package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Stores information required to calculate gap between a tree to the hypothesis tree.
 * The {@link GapEnvironment} is specific of a given T-H pair. It is created in
 * {@link InitializationTextTreesProcessor}.
 * 
 * @see InitializationTextTreesProcessor
 * @see GapFeaturesUpdate
 * @see GapHeuristicMeasure
 * 
 * @author Asher Stern
 * @since Aug 11, 2013
 *
 * @param <I>
 * @param <S>
 */
public class GapEnvironment<I, S extends AbstractNode<I, S>>
{

}
