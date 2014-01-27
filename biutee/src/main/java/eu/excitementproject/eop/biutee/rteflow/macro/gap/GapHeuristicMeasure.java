package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * Given a parse tree, derived from the text, an instance of this interface can measure
 * the gap between it and the hypothesis parse tree. This measure is a single real number,
 * which can be used as the h(x) function in search algorithms.
 * <P>
 * An instance of this interface is part of {@link GapToolInstances}, which is
 * constructed in the <code>init()</code> method of {@link InitializationTextTreesProcessor}.
 * 
 * @see GapToolInstances
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 */
@NotThreadSafe
public interface GapHeuristicMeasure<I, S extends AbstractNode<I, S>>
{
	public double measure(TreeAndParentMap<I, S> tree, Map<Integer, Double> featureVector, GapEnvironment<I, S> environment) throws GapException;

}
