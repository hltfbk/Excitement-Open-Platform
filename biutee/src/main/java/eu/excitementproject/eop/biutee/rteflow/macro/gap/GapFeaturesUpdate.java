package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * Given a parse tree, derived from the text parse tree, the method {@link #updateForGap(TreeAndParentMap, Map)}
 * updates its feature-vector to resemble the gap between it and the hypothesis parse tree.
 * <P>
 * An instance of this interface has information about the hypothesis in its member fields.
 * For each hypothesis (and actually for each pair) a new instance of
 * {@link GapFeaturesUpdate} implementation should be constructed. This is done be constructing
 * a new instance of {@link GapToolInstances} in the <code>init()</code> method of
 * {@link InitializationTextTreesProcessor}.
 * 
 * @see GapToolInstances
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
@NotThreadSafe
public interface GapFeaturesUpdate<I, S extends AbstractNode<I, S>>
{
	public Map<Integer, Double> updateForGap(TreeAndParentMap<I, S> tree, Map<Integer,Double> featureVector) throws GapException;
}
